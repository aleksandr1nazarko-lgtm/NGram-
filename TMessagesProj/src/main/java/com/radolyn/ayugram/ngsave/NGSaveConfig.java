/*
 * NGram: settings storage for the "Save deleted" feature.
 * The idea (keeping deleted messages / edit history) comes from AyuGram for Android
 * (Radolyn Labs, https://github.com/AyuGram/AyuGram4A, GPL). This is an independent implementation.
 */

package com.radolyn.ayugram.ngsave;

import android.app.Activity;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;

public class NGSaveConfig {

    public static final String SAVE_DELETED = "saveDeleted";

    public static final String IN_PRIVATE = "inPrivate";
    public static final String IN_BOTS = "inBots";
    public static final String IN_GROUPS = "inGroups";
    public static final String IN_CHANNELS = "inChannels";

    public static final String K_TEXT = "kText";
    public static final String K_PHOTO = "kPhoto";
    public static final String K_VIDEO = "kVideo";
    public static final String K_VOICE = "kVoice";
    public static final String K_FILE = "kFile";
    public static final String K_STICKER = "kSticker";
    public static final String K_GIF = "kGif";
    public static final String K_OTHER = "kOther";

    public static final String KEEP_CLEARED = "keepCleared";
    public static final String SHOW_MARKER = "showMarker";
    public static final String SAVE_EDITS = "saveEdits";

    public static final int MARKER_TEXT = 0;
    public static final int MARKER_ICON = 1;
    public static final int MARKER_BOTH = 2;

    private static SharedPreferences preferences;

    private static synchronized SharedPreferences prefs() {
        if (preferences == null) {
            preferences = ApplicationLoader.applicationContext.getSharedPreferences("ngsaveconfig", Activity.MODE_PRIVATE);
        }
        return preferences;
    }

    /** All options are ON by default. */
    public static boolean get(String key) {
        return prefs().getBoolean(key, true);
    }

    public static void set(String key, boolean value) {
        prefs().edit().putBoolean(key, value).apply();
    }

    public static boolean toggle(String key) {
        boolean value = !get(key);
        set(key, value);
        return value;
    }

    public static int getMarkerStyle() {
        return prefs().getInt("markerStyle", MARKER_TEXT);
    }

    public static void setMarkerStyle(int style) {
        prefs().edit().putInt("markerStyle", style).apply();
    }
}
