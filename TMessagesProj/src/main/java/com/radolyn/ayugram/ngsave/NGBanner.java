/*
 * NGram: local profile banners. Stored on this device only, never uploaded.
 */
package com.radolyn.ayugram.ngsave;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.BaseFragment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
public class NGBanner {
    public static final int REQUEST_PICK = 9731;
    private static SharedPreferences preferences;
    private static final HashMap<Long, Bitmap> cache = new HashMap<>();
    private static final HashSet<Long> loading = new HashSet<>();
    private static final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
    private static final Paint dimPaint = new Paint();
    private static final Rect src = new Rect();
    private static final Rect dst = new Rect();
    private static synchronized SharedPreferences prefs() {
        if (preferences == null) {
            preferences = ApplicationLoader.applicationContext.getSharedPreferences("ngbanner", Activity.MODE_PRIVATE);
        }
        return preferences;
    }
    public static boolean enabled() {
        return prefs().getBoolean("enabled", true);
    }
    public static void setEnabled(boolean value) {
        prefs().edit().putBoolean("enabled", value).apply();
    }
    public static boolean dim() {
        return prefs().getBoolean("dim", true);
    }
    public static void setDim(boolean value) {
        prefs().edit().putBoolean("dim", value).apply();
    }
    private static File dir() {
        File d = new File(ApplicationLoader.applicationContext.getFilesDir(), "ngbanners");
        if (!d.exists()) {
            d.mkdirs();
        }
        return d;
    }
    private static File file(long key) {
        return new File(dir(), (key < 0 ? "c" + (-key) : "u" + key) + ".jpg");
    }
    public static boolean has(long key) {
        return key != 0 && file(key).exists();
    }
    /** Returns the banner bitmap or null. If it still has to be loaded, onLoaded is called later on the UI thread. */
    public static Bitmap get(long key, Runnable onLoaded) {
        if (key == 0 || !enabled()) {
            return null;
        }
        synchronized (cache) {
            Bitmap b = cache.get(key);
            if (b != null) {
                return b;
            }
            if (loading.contains(key) || !file(key).exists()) {
                return null;
            }
            loading.add(key);
        }
        Utilities.globalQueue.postRunnable(() -> {
            Bitmap loaded = null;
            try {
                loaded = BitmapFactory.decodeFile(file(key).getAbsolutePath());
            } catch (Throwable e) {
                FileLog.e(e);
            }
            final Bitmap result = loaded;
            synchronized (cache) {
                loading.remove(key);
                if (result != null) {
                    cache.put(key, result);
                }
            }
            if (result != null && onLoaded != null) {
                AndroidUtilities.runOnUIThread(onLoaded);
            }
        });
        return null;
    }
    public static void pick(BaseFragment fragment, int requestCode) {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            fragment.startActivityForResult(intent, requestCode);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }
    /** Saves the chosen picture as the banner of the given profile. Calls done on the UI thread. */
    public static void save(Context context, Uri uri, long key, Runnable done) {
        Utilities.globalQueue.postRunnable(() -> {
            try {
                BitmapFactory.Options bounds = new BitmapFactory.Options();
                bounds.inJustDecodeBounds = true;
                try (InputStream in = context.getContentResolver().openInputStream(uri)) {
                    BitmapFactory.decodeStream(in, null, bounds);
                }
                int sample = 1;
                while (bounds.outWidth / sample > 2048 || bounds.outHeight / sample > 2048) {
                    sample *= 2;
                }
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = sample;
                Bitmap bitmap;
                try (InputStream in = context.getContentResolver().openInputStream(uri)) {
                    bitmap = BitmapFactory.decodeStream(in, null, options);
                }
                if (bitmap != null) {
                    try (FileOutputStream out = new FileOutputStream(file(key))) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 88, out);
                    }
                    synchronized (cache) {
                        cache.put(key, bitmap);
                    }
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
            if (done != null) {
                AndroidUtilities.runOnUIThread(done);
            }
        });
    }
    public static void remove(long key) {
        synchronized (cache) {
            cache.remove(key);
        }
        file(key).delete();
    }
    public static void clearAll() {
        synchronized (cache) {
            cache.clear();
        }
        File[] files = dir().listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }
    /** Handles the result of the gallery picker. Returns true if the result was ours. */
    public static boolean handleResult(BaseFragment fragment, int requestCode, int resultCode, Intent data, long key, Runnable done) {
        if (requestCode != REQUEST_PICK) {
            return false;
        }
        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null && key != 0 && fragment.getParentActivity() != null) {
            save(fragment.getParentActivity(), data.getData(), key, done);
        }
        return true;
    }
    /** Draws the bitmap like "center crop" into (0, 0, width, height). */
    public static void draw(Canvas canvas, Bitmap bitmap, int width, int height) {
        if (bitmap == null || width <= 0 || height <= 0) {
            return;
        }
        float scale = Math.max(width / (float) bitmap.getWidth(), height / (float) bitmap.getHeight());
        int sw = Math.min(bitmap.getWidth(), Math.round(width / scale));
        int sh = Math.min(bitmap.getHeight(), Math.round(height / scale));
        int sx = (bitmap.getWidth() - sw) / 2;
        int sy = (bitmap.getHeight() - sh) / 2;
        src.set(sx, sy, sx + sw, sy + sh);
        dst.set(0, 0, width, height);
        canvas.drawBitmap(bitmap, src, dst, paint);
        if (dim()) {
            dimPaint.setColor(0x40000000);
            canvas.drawRect(dst, dimPaint);
        }
    }
}
