/*
 * NGram: own small SQLite database for the "Save deleted" feature
 * (deleted-message marks and edit history). Independent from Telegram's message database.
 */

package com.radolyn.ayugram.ngsave;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;

import java.util.ArrayList;

public class NGSaveDb extends SQLiteOpenHelper {

    public static class Edit {
        public long editedAt;   // when we noticed the edit (unix seconds)
        public int versionDate; // date of the old version (unix seconds)
        public String text;
        public boolean hadMedia;
    }

    private static NGSaveDb instance;

    public static synchronized NGSaveDb getInstance() {
        if (instance == null) {
            instance = new NGSaveDb(ApplicationLoader.applicationContext);
        }
        return instance;
    }

    private NGSaveDb(Context context) {
        super(context, "ngsave.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE deleted_marks (acc INTEGER, dialog INTEGER, mid INTEGER, ts INTEGER, PRIMARY KEY (acc, dialog, mid))");
        db.execSQL("CREATE TABLE edits (id INTEGER PRIMARY KEY AUTOINCREMENT, acc INTEGER, dialog INTEGER, mid INTEGER, ts INTEGER, version_date INTEGER, text TEXT, had_media INTEGER)");
        db.execSQL("CREATE INDEX edits_idx ON edits (acc, dialog, mid)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public synchronized void insertDeleted(int acc, long dialog, int mid, long ts) {
        try {
            ContentValues v = new ContentValues();
            v.put("acc", acc);
            v.put("dialog", dialog);
            v.put("mid", mid);
            v.put("ts", ts);
            getWritableDatabase().insertWithOnConflict("deleted_marks", null, v, SQLiteDatabase.CONFLICT_IGNORE);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public synchronized void deleteDeleted(int acc, long dialog, int mid) {
        try {
            getWritableDatabase().delete("deleted_marks", "acc = ? AND dialog = ? AND mid = ?",
                    new String[]{String.valueOf(acc), String.valueOf(dialog), String.valueOf(mid)});
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    /** @return rows of {acc, dialog, mid} */
    public synchronized ArrayList<long[]> loadDeleted() {
        ArrayList<long[]> result = new ArrayList<>();
        Cursor c = null;
        try {
            c = getReadableDatabase().rawQuery("SELECT acc, dialog, mid FROM deleted_marks", null);
            while (c.moveToNext()) {
                result.add(new long[]{c.getLong(0), c.getLong(1), c.getLong(2)});
            }
        } catch (Throwable e) {
            FileLog.e(e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    public synchronized void insertEdit(int acc, long dialog, int mid, long ts, int versionDate, String text, boolean hadMedia) {
        try {
            ContentValues v = new ContentValues();
            v.put("acc", acc);
            v.put("dialog", dialog);
            v.put("mid", mid);
            v.put("ts", ts);
            v.put("version_date", versionDate);
            v.put("text", text);
            v.put("had_media", hadMedia ? 1 : 0);
            getWritableDatabase().insert("edits", null, v);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public synchronized boolean hasEdits(int acc, long dialog, int mid) {
        Cursor c = null;
        try {
            c = getReadableDatabase().rawQuery("SELECT 1 FROM edits WHERE acc = ? AND dialog = ? AND mid = ? LIMIT 1",
                    new String[]{String.valueOf(acc), String.valueOf(dialog), String.valueOf(mid)});
            return c.moveToFirst();
        } catch (Throwable e) {
            FileLog.e(e);
            return false;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    /** Newest first. */
    public synchronized ArrayList<Edit> getEdits(int acc, long dialog, int mid) {
        ArrayList<Edit> result = new ArrayList<>();
        Cursor c = null;
        try {
            c = getReadableDatabase().rawQuery("SELECT ts, version_date, text, had_media FROM edits WHERE acc = ? AND dialog = ? AND mid = ? ORDER BY id DESC",
                    new String[]{String.valueOf(acc), String.valueOf(dialog), String.valueOf(mid)});
            while (c.moveToNext()) {
                Edit e = new Edit();
                e.editedAt = c.getLong(0);
                e.versionDate = c.getInt(1);
                e.text = c.getString(2);
                e.hadMedia = c.getInt(3) != 0;
                result.add(e);
            }
        } catch (Throwable e) {
            FileLog.e(e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    public synchronized String getLastEditText(int acc, long dialog, int mid) {
        Cursor c = null;
        try {
            c = getReadableDatabase().rawQuery("SELECT text FROM edits WHERE acc = ? AND dialog = ? AND mid = ? ORDER BY id DESC LIMIT 1",
                    new String[]{String.valueOf(acc), String.valueOf(dialog), String.valueOf(mid)});
            return c.moveToFirst() ? c.getString(0) : null;
        } catch (Throwable e) {
            FileLog.e(e);
            return null;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public synchronized int count(String table) {
        Cursor c = null;
        try {
            c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + table, null);
            return c.moveToFirst() ? c.getInt(0) : 0;
        } catch (Throwable e) {
            FileLog.e(e);
            return 0;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public synchronized void clearDeleted() {
        try {
            getWritableDatabase().delete("deleted_marks", null, null);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public synchronized void clearEdits() {
        try {
            getWritableDatabase().delete("edits", null, null);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }
}
