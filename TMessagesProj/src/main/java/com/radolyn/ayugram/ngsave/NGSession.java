/*
 * NGram: export and import of account sessions (.ngsess is a plain ZIP).
 * A session = tgnet.dat + dc*conf.dat (auth keys) + userconfig xml, optionally the message database.
 */
package com.radolyn.ayugram.ngsave;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.UserConfig;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class NGSession {
    public static final String EXT = ".ngsess";
    public static final int ERR_BAD_FILE = 1;
    public static final int ERR_NO_SLOT = 2;
    public static final int ERR_EXISTS = 3;
    public static final int ERR_IO = 4;

    private static final long MAX_TOTAL = 3L * 1024 * 1024 * 1024;
    private static final Pattern DC_FILE = Pattern.compile("^(dc\\d+conf|cdnkeys)\\.dat(\\.bak)?$");
    private static final Pattern E_META = Pattern.compile("^meta\\.txt$");
    private static final Pattern E_TGNET = Pattern.compile("^account(\\d+)\\.dat$");
    private static final Pattern E_PREFS = Pattern.compile("^userconf(\\d+)\\.xml$");
    private static final Pattern E_DB = Pattern.compile("^cache4_(\\d+)\\.db$");
    private static final Pattern E_WAL = Pattern.compile("^cache4_(\\d+)\\.db-wal$");
    private static final Pattern E_DC = Pattern.compile("^dcfiles/([A-Za-z0-9_]+\\.dat(\\.bak)?)$");

    public static class SessionException extends Exception {
        public final int code;

        public SessionException(int code, String message) {
            super(message);
            this.code = code;
        }
    }

    private static File accountDir(int account) {
        File dir = ApplicationLoader.getFilesDirFixed();
        if (account != 0) {
            dir = new File(dir, "account" + account);
        }
        return dir;
    }

    private static File prefsFile(int account) {
        File shared = new File(ApplicationLoader.applicationContext.getApplicationInfo().dataDir, "shared_prefs");
        return new File(shared, (account == 0 ? "userconfing" : "userconfig" + account) + ".xml");
    }

    /** Сохраняет сессию аккаунта в Download/NGram и возвращает путь для показа. */
    public static String export(Context context, int account, boolean withMessages) throws SessionException {
        File tmp = null;
        try {
            UserConfig config = UserConfig.getInstance(account);
            if (!config.isClientActivated()) {
                throw new SessionException(ERR_IO, "account is not active");
            }
            config.saveConfig(false);
            try {
                Thread.sleep(400);
            } catch (InterruptedException ignore) {
            }
            long userId = config.getClientUserId();
            tmp = new File(context.getCacheDir(), "ngsess_" + System.nanoTime() + EXT);
            try (ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(tmp)))) {
                zip.setLevel(5);
                putText(zip, "meta.txt", "ngram_session=1\nuserId=" + userId + "\nslot=" + account + "\nmessages=" + withMessages + "\n");
                File dir = accountDir(account);
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        String n = f.getName();
                        if (!f.isFile()) {
                            continue;
                        }
                        if (n.equals("tgnet.dat")) {
                            putFile(zip, "account" + account + ".dat", f);
                        } else if (DC_FILE.matcher(n).matches()) {
                            putFile(zip, "dcfiles/" + n, f);
                        }
                    }
                }
                File prefs = prefsFile(account);
                if (prefs.exists()) {
                    putFile(zip, "userconf" + account + ".xml", prefs);
                }
                if (withMessages) {
                    File db = new File(dir, "cache4.db");
                    if (db.exists()) {
                        putFile(zip, "cache4_" + account + ".db", db);
                    }
                    File wal = new File(dir, "cache4.db-wal");
                    if (wal.exists()) {
                        putFile(zip, "cache4_" + account + ".db-wal", wal);
                    }
                }
            }
            return saveToDownloads(context, tmp, "ngram_session_" + userId + "_" + System.currentTimeMillis() + EXT);
        } catch (SessionException e) {
            throw e;
        } catch (Throwable e) {
            FileLog.e(e);
            throw new SessionException(ERR_IO, String.valueOf(e));
        } finally {
            if (tmp != null) {
                tmp.delete();
            }
        }
    }

    private static void putText(ZipOutputStream zip, String name, String text) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(text.getBytes("UTF-8"));
        zip.closeEntry();
    }

    private static void putFile(ZipOutputStream zip, String name, File file) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        try (InputStream in = new FileInputStream(file)) {
            copy(in, zip);
        }
        zip.closeEntry();
    }

    private static long copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[64 * 1024];
        long total = 0;
        int n;
        while ((n = in.read(buffer)) > 0) {
            out.write(buffer, 0, n);
            total += n;
        }
        return total;
    }

    private static String saveToDownloads(Context context, File src, String name) throws IOException {
        if (Build.VERSION.SDK_INT >= 29) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/NGram");
            values.put(MediaStore.MediaColumns.IS_PENDING, 1);
            ContentResolver resolver = context.getContentResolver();
            Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri == null) {
                throw new IOException("MediaStore insert failed");
            }
            try (OutputStream out = resolver.openOutputStream(uri); InputStream in = new FileInputStream(src)) {
                if (out == null) {
                    throw new IOException("openOutputStream failed");
                }
                copy(in, out);
            }
            ContentValues done = new ContentValues();
            done.put(MediaStore.MediaColumns.IS_PENDING, 0);
            resolver.update(uri, done, null, null);
        } else {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "NGram");
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException("mkdirs failed");
            }
            try (OutputStream out = new FileOutputStream(new File(dir, name)); InputStream in = new FileInputStream(src)) {
                copy(in, out);
            }
        }
        return "Download/NGram/" + name;
    }

    /** Импортирует .ngsess в первый свободный слот. Возвращает номер слота. */
    public static int importSession(Context context, Uri uri) throws SessionException {
        File tmpDir = new File(context.getCacheDir(), "ngsess_import_" + System.nanoTime());
        try {
            if (!tmpDir.mkdirs()) {
                throw new SessionException(ERR_IO, "cache dir");
            }
            try (InputStream raw = context.getContentResolver().openInputStream(uri)) {
                if (raw == null) {
                    throw new SessionException(ERR_IO, "open failed");
                }
                BufferedInputStream in = new BufferedInputStream(raw);
                in.mark(8);
                byte[] magic = new byte[4];
                int read = 0;
                while (read < 4) {
                    int n = in.read(magic, read, 4 - read);
                    if (n < 0) {
                        break;
                    }
                    read += n;
                }
                if (read < 4 || magic[0] != 0x50 || magic[1] != 0x4B || magic[2] != 0x03 || magic[3] != 0x04) {
                    throw new SessionException(ERR_BAD_FILE, "not a zip");
                }
                in.reset();
                unpack(in, tmpDir);
            }
            File meta = new File(tmpDir, "meta.txt");
            File tgnet = new File(tmpDir, "tgnet.dat");
            File prefs = new File(tmpDir, "userconf.xml");
            if (!meta.exists() || !tgnet.exists() || !prefs.exists()) {
                throw new SessionException(ERR_BAD_FILE, "missing files");
            }
            long userId = 0;
            try (InputStream in = new FileInputStream(meta)) {
                java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
                copy(in, bos);
                for (String line : bos.toString("UTF-8").split("\n")) {
                    if (line.startsWith("userId=")) {
                        userId = Long.parseLong(line.substring(7).trim());
                    }
                }
            } catch (NumberFormatException e) {
                throw new SessionException(ERR_BAD_FILE, "bad meta");
            }
            if (userId == 0) {
                throw new SessionException(ERR_BAD_FILE, "bad meta");
            }
            int slot = -1;
            for (int a = 0; a < UserConfig.MAX_ACCOUNT_COUNT; a++) {
                UserConfig config = UserConfig.getInstance(a);
                if (config.isClientActivated()) {
                    if (config.getClientUserId() == userId) {
                        throw new SessionException(ERR_EXISTS, "already added");
                    }
                } else if (slot < 0) {
                    slot = a;
                }
            }
            if (slot < 0) {
                throw new SessionException(ERR_NO_SLOT, "no free slot");
            }
            File dir = accountDir(slot);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new SessionException(ERR_IO, "mkdirs");
            }
            File[] old = dir.listFiles();
            if (old != null) {
                for (File f : old) {
                    String n = f.getName();
                    if (n.startsWith("tgnet.dat") || DC_FILE.matcher(n).matches()) {
                        f.delete();
                    }
                }
            }
            place(tgnet, new File(dir, "tgnet.dat"));
            File[] tmpFiles = tmpDir.listFiles();
            if (tmpFiles != null) {
                for (File f : tmpFiles) {
                    if (f.getName().startsWith("dc_")) {
                        place(f, new File(dir, f.getName().substring(3)));
                    }
                }
            }
            File prefsTarget = prefsFile(slot);
            File prefsDir = prefsTarget.getParentFile();
            if (prefsDir != null && !prefsDir.exists()) {
                prefsDir.mkdirs();
            }
            place(prefs, prefsTarget);
            File db = new File(tmpDir, "cache4.db");
            if (db.exists()) {
                new File(dir, "cache4.db").delete();
                new File(dir, "cache4.db-wal").delete();
                new File(dir, "cache4.db-shm").delete();
                place(db, new File(dir, "cache4.db"));
                File wal = new File(tmpDir, "cache4.db-wal");
                if (wal.exists()) {
                    place(wal, new File(dir, "cache4.db-wal"));
                }
            }
            return slot;
        } catch (SessionException e) {
            throw e;
        } catch (Throwable e) {
            FileLog.e(e);
            throw new SessionException(ERR_IO, String.valueOf(e));
        } finally {
            deleteRecursive(tmpDir);
        }
    }

    private static void unpack(InputStream in, File tmpDir) throws IOException {
        long total = 0;
        try (ZipInputStream zip = new ZipInputStream(in)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                String name = entry.getName();
                String target = null;
                Matcher m;
                if (E_META.matcher(name).matches()) {
                    target = "meta.txt";
                } else if (E_TGNET.matcher(name).matches()) {
                    target = "tgnet.dat";
                } else if (E_PREFS.matcher(name).matches()) {
                    target = "userconf.xml";
                } else if (E_DB.matcher(name).matches()) {
                    target = "cache4.db";
                } else if (E_WAL.matcher(name).matches()) {
                    target = "cache4.db-wal";
                } else if ((m = E_DC.matcher(name)).matches()) {
                    target = "dc_" + m.group(1);
                }
                if (target == null || entry.isDirectory()) {
                    continue;
                }
                try (OutputStream out = new FileOutputStream(new File(tmpDir, target))) {
                    byte[] buffer = new byte[64 * 1024];
                    int n;
                    while ((n = zip.read(buffer)) > 0) {
                        total += n;
                        if (total > MAX_TOTAL) {
                            throw new IOException("session is too large");
                        }
                        out.write(buffer, 0, n);
                    }
                }
            }
        }
    }

    private static void place(File from, File to) throws IOException {
        File part = new File(to.getParentFile(), to.getName() + ".part");
        try (InputStream in = new FileInputStream(from); OutputStream out = new FileOutputStream(part)) {
            copy(in, out);
        }
        if (to.exists() && !to.delete()) {
            throw new IOException("cannot replace " + to);
        }
        if (!part.renameTo(to)) {
            throw new IOException("cannot rename " + part);
        }
    }

    private static void deleteRecursive(File file) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File c : children) {
                deleteRecursive(c);
            }
        }
        file.delete();
    }

    public static void restartApp(Context context) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pending = PendingIntent.getActivity(context, 7761, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (alarm != null) {
                    alarm.set(AlarmManager.RTC, System.currentTimeMillis() + 400, pending);
                }
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
