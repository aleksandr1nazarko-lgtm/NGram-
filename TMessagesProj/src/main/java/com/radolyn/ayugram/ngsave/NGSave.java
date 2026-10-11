/*
 * NGram: core logic of the "Save deleted" feature.
 *
 * Instead of removing messages that the other side (or an admin) deleted, we simply do not delete them from
 * Telegram's local database and remember their ids in our own table, so the chat can mark them as deleted.
 * Edits are recorded into an edit-history table.
 *
 * The idea comes from AyuGram for Android (Radolyn Labs, https://github.com/AyuGram/AyuGram4A, GPL).
 */
package com.radolyn.ayugram.ngsave;
import android.os.SystemClock;
import android.text.TextUtils;
import androidx.collection.LongSparseArray;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
public class NGSave {
    private static final int KIND_TEXT = 0;
    private static final int KIND_PHOTO = 1;
    private static final int KIND_VIDEO = 2;
    private static final int KIND_VOICE = 3;
    private static final int KIND_FILE = 4;
    private static final int KIND_STICKER = 5;
    private static final int KIND_GIF = 6;
    private static final int KIND_OTHER = 7;
    private static final long OWN_DELETE_TTL_MS = 10 * 60 * 1000L;
    /** Messages that the user deleted himself on this device: these must really be deleted. */
    private static final ConcurrentHashMap<String, Long> ownDeleted = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> ownKept = new ConcurrentHashMap<>();
    /** account -> dialogId -> ids of messages that were kept after being deleted by someone else. */
    private static final ConcurrentHashMap<Long, Set<Integer>>[] marks = createMarks();
    private static volatile boolean marksLoaded;
    @SuppressWarnings("unchecked")
    private static ConcurrentHashMap<Long, Set<Integer>>[] createMarks() {
        ConcurrentHashMap<Long, Set<Integer>>[] arr = new ConcurrentHashMap[UserConfig.MAX_ACCOUNT_COUNT];
        for (int a = 0; a < arr.length; a++) {
            arr[a] = new ConcurrentHashMap<>();
        }
        return arr;
    }
    // ------------------------------------------------------------------ marks cache
    private static void ensureMarksLoaded() {
        if (marksLoaded) {
            return;
        }
        synchronized (NGSave.class) {
            if (marksLoaded) {
                return;
            }
            for (long[] row : NGSaveDb.getInstance().loadDeleted()) {
                cacheAdd((int) row[0], row[1], (int) row[2]);
            }
            marksLoaded = true;
        }
    }
    private static void cacheAdd(int account, long dialogId, int mid) {
        if (account < 0 || account >= marks.length) {
            return;
        }
        Set<Integer> set = marks[account].get(dialogId);
        if (set == null) {
            set = ConcurrentHashMap.newKeySet();
            Set<Integer> old = marks[account].putIfAbsent(dialogId, set);
            if (old != null) {
                set = old;
            }
        }
        set.add(mid);
    }
    private static void cacheRemove(int account, long dialogId, int mid) {
        if (account < 0 || account >= marks.length) {
            return;
        }
        Set<Integer> set = marks[account].get(dialogId);
        if (set != null) {
            set.remove(mid);
        }
    }
    /** Called from the chat UI for every message cell. */
    public static boolean isMarked(int account, long dialogId, int mid) {
        try {
            if (account < 0 || account >= marks.length || !NGSaveConfig.get(NGSaveConfig.SHOW_MARKER)) {
                return false;
            }
            ensureMarksLoaded();
            Set<Integer> set = marks[account].get(dialogId);
            return set != null && set.contains(mid);
        } catch (Throwable e) {
            FileLog.e(e);
            return false;
        }
    }
    public static CharSequence markerText() {
        switch (NGSaveConfig.getMarkerStyle()) {
            case NGSaveConfig.MARKER_ICON:
                return "\uD83D\uDDD1";
            case NGSaveConfig.MARKER_BOTH:
                return "\uD83D\uDDD1 " + NGStr.get(R.string.NGSaveDeletedMarker);
            default:
                return NGStr.get(R.string.NGSaveDeletedMarker);
        }
    }
    // ------------------------------------------------------------------ own deletions
    private static String ownKey(int account, int mid, boolean channel) {
        return account + ":" + mid + ":" + (channel ? 1 : 0);
    }
    private static boolean isChannelDialog(int account, long dialogId) {
        if (dialogId >= 0 || DialogObject.isEncryptedDialog(dialogId)) {
            return false;
        }
        TLRPC.Chat chat = MessagesController.getInstance(account).getChat(-dialogId);
        return ChatObject.isChannel(chat);
    }
    /** Hook: the user deletes messages himself. They must disappear for real. */
    public static void onOwnDelete(int account, long dialogId, ArrayList<Integer> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return;
            }
            long now = SystemClock.elapsedRealtime();
            Iterator<Map.Entry<String, Long>> it = ownDeleted.entrySet().iterator();
            while (it.hasNext()) {
                if (now - it.next().getValue() > OWN_DELETE_TTL_MS) {
                    it.remove();
                }
            }
            ownKept.clear();
            boolean channel = isChannelDialog(account, dialogId);
            boolean keepOwn = dialogId != 0 && !DialogObject.isEncryptedDialog(dialogId)
                    && NGSaveConfig.get(NGSaveConfig.SAVE_DELETED) && NGSaveConfig.get(NGSaveConfig.SAVE_OWN_DELETED);
            HashSet<Integer> keepIds = new HashSet<>();
            if (keepOwn) {
                ensureMarksLoaded();
                ArrayList<Integer> candidates = new ArrayList<>();
                Set<Integer> marked = marks[account].get(dialogId);
                for (int a = 0, n = ids.size(); a < n; a++) {
                    int mid = ids.get(a);
                    // уже помеченное удаляем по-настоящему
                    if (mid > 0 && (marked == null || !marked.contains(mid))) {
                        candidates.add(mid);
                    }
                }
                if (!candidates.isEmpty()) {
                    long ts = System.currentTimeMillis() / 1000;
                    for (Found f : lookup(account, dialogId, candidates)) {
                        if (shouldKeep(account, f.uid, f.msg)) {
                            keepIds.add(f.mid);
                            cacheAdd(account, f.uid, f.mid);
                            NGSaveDb.getInstance().insertDeleted(account, f.uid, f.mid, ts);
                        }
                    }
                }
            }
            for (int a = 0, n = ids.size(); a < n; a++) {
                int mid = ids.get(a);
                if (keepIds.contains(mid)) {
                    ownKept.put(ownKey(account, mid, channel), now);
                    continue;
                }
                ownDeleted.put(ownKey(account, mid, channel), now);
                if (marksLoaded) {
                    cacheRemove(account, dialogId, mid);
                }
                NGSaveDb.getInstance().deleteDeleted(account, dialogId, mid);
            }
            if (!keepIds.isEmpty()) {
                AndroidUtilities.runOnUIThread(() -> NotificationCenter.getInstance(account).postNotificationName(NotificationCenter.ngSaveMessagesKept, dialogId));
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }
    /** Какие из удаляемых id нужно реально убрать с устройства (сохранённые остаются). */
    public static ArrayList<Integer> localDeleteList(int account, long dialogId, ArrayList<Integer> ids) {
        try {
            if (ids == null || ownKept.isEmpty()) {
                return ids;
            }
            boolean channel = isChannelDialog(account, dialogId);
            ArrayList<Integer> rest = new ArrayList<>();
            for (int a = 0, n = ids.size(); a < n; a++) {
                int mid = ids.get(a);
                if (!ownKept.containsKey(ownKey(account, mid, channel))) {
                    rest.add(mid);
                }
            }
            return rest.size() == ids.size() ? ids : rest;
        } catch (Throwable e) {
            FileLog.e(e);
            return ids;
        }
    }
        private static boolean isOwnDeleted(int account, int mid, boolean channel) {
        Long t = ownDeleted.get(ownKey(account, mid, channel));
        return t != null && SystemClock.elapsedRealtime() - t <= OWN_DELETE_TTL_MS;
    }
    // ------------------------------------------------------------------ deletion filtering
    private static class Found {
        long uid;
        int mid;
        TLRPC.Message msg;
    }
    /** Looks messages up in Telegram's database (synchronously). dialogId == 0 means "any non-channel dialog". */
    private static ArrayList<Found> lookup(int account, long dialogId, ArrayList<Integer> mids) {
        final ArrayList<Found> out = new ArrayList<>();
        final MessagesStorage storage = MessagesStorage.getInstance(account);
        final String ids = TextUtils.join(",", mids);
        Runnable r = () -> {
            SQLiteCursor cursor = null;
            try {
                String sql;
                if (dialogId != 0) {
                    sql = "SELECT uid, mid, data FROM messages_v2 WHERE mid IN(" + ids + ") AND uid = " + dialogId;
                } else {
                    sql = "SELECT uid, mid, data FROM messages_v2 WHERE mid IN(" + ids + ") AND is_channel = 0";
                }
                cursor = storage.getDatabase().queryFinalized(sql);
                while (cursor.next()) {
                    NativeByteBuffer data = cursor.byteBufferValue(2);
                    if (data == null) {
                        continue;
                    }
                    TLRPC.Message m = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                    data.reuse();
                    if (m == null) {
                        continue;
                    }
                    Found f = new Found();
                    f.uid = cursor.longValue(0);
                    f.mid = cursor.intValue(1);
                    f.msg = m;
                    synchronized (out) {
                        out.add(f);
                    }
                }
            } catch (Throwable e) {
                FileLog.e(e);
            } finally {
                if (cursor != null) {
                    cursor.dispose();
                }
            }
        };
        if (Thread.currentThread() == storage.getStorageQueue()) {
            r.run();
        } else {
            CountDownLatch latch = new CountDownLatch(1);
            storage.getStorageQueue().postRunnable(() -> {
                try {
                    r.run();
                } finally {
                    latch.countDown();
                }
            });
            try {
                latch.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException ignore) {
            }
        }
        synchronized (out) {
            return new ArrayList<>(out);
        }
    }
    private static boolean chatTypeEnabled(int account, long uid) {
        if (DialogObject.isEncryptedDialog(uid)) {
            return NGSaveConfig.get(NGSaveConfig.IN_SECRET);
        }
        MessagesController mc = MessagesController.getInstance(account);
        if (uid > 0) {
            TLRPC.User user = mc.getUser(uid);
            if (user != null && user.bot) {
                return NGSaveConfig.get(NGSaveConfig.IN_BOTS);
            }
            return NGSaveConfig.get(NGSaveConfig.IN_PRIVATE);
        }
        TLRPC.Chat chat = mc.getChat(-uid);
        if (chat != null && ChatObject.isChannel(chat) && !chat.megagroup) {
            return NGSaveConfig.get(NGSaveConfig.IN_CHANNELS);
        }
        return NGSaveConfig.get(NGSaveConfig.IN_GROUPS);
    }
    private static int kindOf(TLRPC.Message m) {
        TLRPC.MessageMedia media = m.media;
        if (media == null || media instanceof TLRPC.TL_messageMediaEmpty || media instanceof TLRPC.TL_messageMediaWebPage) {
            return KIND_TEXT;
        }
        if (media instanceof TLRPC.TL_messageMediaPhoto) {
            return KIND_PHOTO;
        }
        if (media instanceof TLRPC.TL_messageMediaDocument && media.document != null) {
            TLRPC.Document d = media.document;
            if (MessageObject.isStickerDocument(d) || MessageObject.isAnimatedStickerDocument(d, true)) {
                return KIND_STICKER;
            }
            if (MessageObject.isGifDocument(d)) {
                return KIND_GIF;
            }
            if (MessageObject.isVoiceDocument(d) || MessageObject.isRoundVideoDocument(d)) {
                return KIND_VOICE;
            }
            if (MessageObject.isVideoDocument(d)) {
                return KIND_VIDEO;
            }
            return KIND_FILE;
        }
        return KIND_OTHER;
    }
    private static boolean kindEnabled(int kind) {
        switch (kind) {
            case KIND_TEXT:
                return NGSaveConfig.get(NGSaveConfig.K_TEXT);
            case KIND_PHOTO:
                return NGSaveConfig.get(NGSaveConfig.K_PHOTO);
            case KIND_VIDEO:
                return NGSaveConfig.get(NGSaveConfig.K_VIDEO);
            case KIND_VOICE:
                return NGSaveConfig.get(NGSaveConfig.K_VOICE);
            case KIND_FILE:
                return NGSaveConfig.get(NGSaveConfig.K_FILE);
            case KIND_STICKER:
                return NGSaveConfig.get(NGSaveConfig.K_STICKER);
            case KIND_GIF:
                return NGSaveConfig.get(NGSaveConfig.K_GIF);
            default:
                return NGSaveConfig.get(NGSaveConfig.K_OTHER);
        }
    }
    private static boolean shouldKeep(int account, long uid, TLRPC.Message msg) {
        if (msg instanceof TLRPC.TL_messageService) {
            return false;
        }
        return chatTypeEnabled(account, uid) && kindEnabled(kindOf(msg));
    }
    /**
     * Removes from {@code ids} everything that must be kept (and remembers it). What stays in the list
     * is deleted by Telegram as usual. {@code dialogKey} is 0 for ordinary (non-channel) updates.
     */
    private static void filterIds(int account, long dialogKey, ArrayList<Integer> ids) {
        final boolean channel = dialogKey != 0 && isChannelDialog(account, dialogKey);
        ArrayList<Integer> candidates = new ArrayList<>();
        ArrayList<Integer> remaining = new ArrayList<>();
        for (int a = 0, n = ids.size(); a < n; a++) {
            int mid = ids.get(a);
            if (isOwnDeleted(account, mid, channel)) {
                remaining.add(mid);
            } else {
                candidates.add(mid);
            }
        }
        if (candidates.isEmpty()) {
            return;
        }
        ensureMarksLoaded();
        HashSet<Integer> kept = new HashSet<>();
        HashSet<Long> changedDialogs = new HashSet<>();
        long now = System.currentTimeMillis() / 1000;
        for (Found f : lookup(account, dialogKey, candidates)) {
            if (kept.contains(f.mid) && !channel) {
                // same non-channel id in several dialogs is impossible, but be safe
                continue;
            }
            if (shouldKeep(account, f.uid, f.msg)) {
                kept.add(f.mid);
                cacheAdd(account, f.uid, f.mid);
                NGSaveDb.getInstance().insertDeleted(account, f.uid, f.mid, now);
                changedDialogs.add(f.uid);
            }
        }
        if (kept.isEmpty()) {
            return;
        }
        for (int mid : candidates) {
            if (!kept.contains(mid)) {
                remaining.add(mid);
            }
        }
        ids.clear();
        ids.addAll(remaining);
        for (long dialogId : changedDialogs) {
            AndroidUtilities.runOnUIThread(() -> NotificationCenter.getInstance(account).postNotificationName(NotificationCenter.ngSaveMessagesKept, dialogId));
        }
    }
    /** Hook for processUpdateArray: lists of deleted ids by dialog (0 = non-channel). Modified in place. */
    public static void filterDeleted(int account, LongSparseArray<ArrayList<Integer>> deleted) {
        try {
            if (deleted == null || !NGSaveConfig.get(NGSaveConfig.SAVE_DELETED)) {
                return;
            }
            for (int a = deleted.size() - 1; a >= 0; a--) {
                ArrayList<Integer> list = deleted.valueAt(a);
                if (list == null || list.isEmpty()) {
                    continue;
                }
                filterIds(account, deleted.keyAt(a), list);
                if (list.isEmpty()) {
                    deleted.removeAt(a);
                }
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }
    /** Hook: the other side deleted messages of a secret chat (ids are local message ids). Modified in place. */
    public static void filterSecret(int account, long dialogId, ArrayList<Integer> ids) {
        try {
            if (ids == null || ids.isEmpty() || !NGSaveConfig.get(NGSaveConfig.SAVE_DELETED) || !NGSaveConfig.get(NGSaveConfig.IN_SECRET)) {
                return;
            }
            filterIds(account, dialogId, ids);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }
    /** Hook: the other side cleared a whole secret chat. true = keep everything, do not clear. */
    public static boolean keepSecretFlush(int account, long dialogId) {
        return NGSaveConfig.get(NGSaveConfig.SAVE_DELETED) && NGSaveConfig.get(NGSaveConfig.IN_SECRET) && NGSaveConfig.get(NGSaveConfig.KEEP_CLEARED);
    }
    /** Hook for push-driven deletions. Modified in place. */
    public static void filterPush(int account, long channelId, ArrayList<Integer> ids) {
        try {
            if (ids == null || ids.isEmpty() || !NGSaveConfig.get(NGSaveConfig.SAVE_DELETED)) {
                return;
            }
            filterIds(account, channelId != 0 ? -channelId : 0, ids);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }
    /** Hook: "history of a dialog was cleared" updates. Dialogs we want to keep are removed from the array. */
    public static void filterClearHistory(int account, LongSparseIntArray cleared) {
        try {
            if (cleared == null || !NGSaveConfig.get(NGSaveConfig.SAVE_DELETED) || !NGSaveConfig.get(NGSaveConfig.KEEP_CLEARED)) {
                return;
            }
            for (int a = cleared.size() - 1; a >= 0; a--) {
                long dialogId = cleared.keyAt(a);
                if (dialogId != 0 && chatTypeEnabled(account, dialogId)) {
                    cleared.removeAt(a);
                }
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }
    // ------------------------------------------------------------------ edit history
    private static boolean sameMedia(TLRPC.Message o, TLRPC.Message n) {
        TLRPC.MessageMedia a = o.media;
        TLRPC.MessageMedia b = n.media;
        boolean aEmpty = a == null || a instanceof TLRPC.TL_messageMediaEmpty || a instanceof TLRPC.TL_messageMediaWebPage;
        boolean bEmpty = b == null || b instanceof TLRPC.TL_messageMediaEmpty || b instanceof TLRPC.TL_messageMediaWebPage;
        if (aEmpty || bEmpty) {
            return aEmpty && bEmpty;
        }
        if (a instanceof TLRPC.TL_messageMediaPhoto && b instanceof TLRPC.TL_messageMediaPhoto && a.photo != null && b.photo != null) {
            return a.photo.id == b.photo.id;
        }
        if (a instanceof TLRPC.TL_messageMediaDocument && b instanceof TLRPC.TL_messageMediaDocument && a.document != null && b.document != null) {
            return a.document.id == b.document.id;
        }
        return a.getClass() == b.getClass();
    }
    /** Hook: an already known message came back from the server changed. Runs on the storage thread. */
    public static void onMessageEdited(int account, long dialogId, TLRPC.Message oldMsg, TLRPC.Message newMsg) {
        try {
            if (!NGSaveConfig.get(NGSaveConfig.SAVE_EDITS) || oldMsg == null || newMsg == null) {
                return;
            }
            if (oldMsg instanceof TLRPC.TL_messageService) {
                return;
            }
            boolean sameMedia = sameMedia(oldMsg, newMsg);
            if (sameMedia && TextUtils.equals(oldMsg.message, newMsg.message)) {
                return;
            }
            String text = oldMsg.message == null ? "" : oldMsg.message;
            boolean hadMedia = oldMsg.media != null && !(oldMsg.media instanceof TLRPC.TL_messageMediaEmpty) && !(oldMsg.media instanceof TLRPC.TL_messageMediaWebPage);
            NGSaveDb db = NGSaveDb.getInstance();
            String last = db.getLastEditText(account, dialogId, oldMsg.id);
            if (last != null && last.equals(text) && sameMedia) {
                return;
            }
            int versionDate = oldMsg.edit_date != 0 ? oldMsg.edit_date : oldMsg.date;
            db.insertEdit(account, dialogId, oldMsg.id, System.currentTimeMillis() / 1000, versionDate, text, hadMedia && !sameMedia);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }
    /** Свои правки: старый текст сохраняется до отправки правки на сервер. */
    public static void onOwnEdit(int account, MessageObject mo, String newText) {
        try {
            if (mo == null || mo.messageOwner == null) {
                return;
            }
            TLRPC.Message old = mo.messageOwner;
            TLRPC.Message fresh = new TLRPC.TL_message();
            fresh.id = old.id;
            fresh.media = old.media;
            fresh.message = newText == null ? "" : newText;
            fresh.edit_date = (int) (System.currentTimeMillis() / 1000);
            onMessageEdited(account, mo.getDialogId(), old, fresh);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    /** Текст для цитаты, если у помеченного удалённого сообщения нет своего текста. */
    public static String quoteText(int account, MessageObject mo) {
        try {
            if (mo == null || mo.messageOwner == null) {
                return null;
            }
            String current = mo.messageOwner.message;
            if (current != null && !current.trim().isEmpty()) {
                return null;
            }
            long dialogId = mo.getDialogId();
            if (!isMarked(account, dialogId, mo.getId())) {
                return null;
            }
            String last = NGSaveDb.getInstance().getLastEditText(account, dialogId, mo.getId());
            if (last != null && !last.trim().isEmpty()) {
                return last;
            }
            return NGStr.get(R.string.NGSaveDeletedMarker);
        } catch (Throwable e) {
            FileLog.e(e);
            return null;
        }
    }

    public static boolean hasEditHistory(int account, long dialogId, int mid) {
        try {
            return NGSaveDb.getInstance().hasEdits(account, dialogId, mid);
        } catch (Throwable e) {
            FileLog.e(e);
            return false;
        }
    }
    // ------------------------------------------------------------------ maintenance
    public static void clearAll() {
        NGSaveDb.getInstance().clearDeleted();
        NGSaveDb.getInstance().clearEdits();
        for (ConcurrentHashMap<Long, Set<Integer>> m : marks) {
            m.clear();
        }
    }
    public static void clearEdits() {
        NGSaveDb.getInstance().clearEdits();
    }
    public static void clearMarks() {
        NGSaveDb.getInstance().clearDeleted();
        for (ConcurrentHashMap<Long, Set<Integer>> m : marks) {
            m.clear();
        }
    }
}
