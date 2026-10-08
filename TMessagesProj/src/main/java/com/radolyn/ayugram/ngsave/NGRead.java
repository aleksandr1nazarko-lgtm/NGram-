/*
 * NGram: local part of the manual "Read until" action (Ghost mode).
 */
package com.radolyn.ayugram.ngsave;
import com.radolyn.ayugram.utils.AyuGhostUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.tgnet.TLRPC;
public class NGRead {
    /** Marks everything up to untilId as read on this device (the server request is sent separately). */
    public static void afterReadUntil(int account, long dialogId, int untilId, int readCount) {
        try {
            MessagesController mc = MessagesController.getInstance(account);
            TLRPC.Dialog dialog = mc.dialogs_dict.get(dialogId);
            int unread = dialog != null ? Math.max(0, dialog.unread_count - readCount) : 0;
            AyuGhostUtils.markReadLocally(account, dialogId, untilId, unread);
            if (dialog != null) {
                dialog.unread_count = unread;
            }
            AndroidUtilities.runOnUIThread(() -> {
                NotificationsController.getInstance(account).processReadMessages(null, dialogId, 0, untilId, false);
                NotificationCenter.getInstance(account).postNotificationName(NotificationCenter.updateInterfaces, MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE);
                NotificationCenter.getInstance(account).postNotificationName(NotificationCenter.dialogsNeedReload);
            });
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }
}
