/*
 * Based on the source code of AyuGram for Android (Radolyn Labs, 2023).
 * https://github.com/AyuGram/AyuGram4A  -- licensed under GPL, keep this notice.
 * Trimmed to the ghost-mode subset.
 */

package com.radolyn.ayugram;

import android.app.Activity;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;

/** Ghost-mode settings only (trimmed from AyuGram's AyuConfig). */
public class AyuConfig {
    private static final Object sync = new Object();

    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;

    public static boolean sendReadPackets;
    public static boolean sendReadStories;
    public static boolean sendOnlinePackets;
    public static boolean sendOfflinePacketAfterOnline;
    public static boolean sendUploadProgress;
    public static boolean markReadAfterSend;

    private static boolean configLoaded;

    static {
        loadConfig();
    }

    public static void loadConfig() {
        synchronized (sync) {
            if (configLoaded) {
                return;
            }

            preferences = ApplicationLoader.applicationContext.getSharedPreferences("ayuconfig", Activity.MODE_PRIVATE);
            editor = preferences.edit();

            // defaults = normal (non-ghost) behaviour
            sendReadPackets = preferences.getBoolean("sendReadPackets", true);
            sendReadStories = preferences.getBoolean("sendReadStories", true);
            sendOnlinePackets = preferences.getBoolean("sendOnlinePackets", true);
            sendUploadProgress = preferences.getBoolean("sendUploadProgress", true);
            sendOfflinePacketAfterOnline = preferences.getBoolean("sendOfflinePacketAfterOnline", false);
            markReadAfterSend = preferences.getBoolean("markReadAfterSend", true);

            configLoaded = true;
        }
    }

    /** Ghost mode is "on" when every ghost option is on ("Send read status after reply" is independent). */
    public static boolean isGhostModeActive() {
        return !sendReadPackets && !sendReadStories && !sendOnlinePackets && !sendUploadProgress && sendOfflinePacketAfterOnline;
    }

    public static void setGhostMode(boolean enabled) {
        sendReadPackets = !enabled;
        sendReadStories = !enabled;
        sendOnlinePackets = !enabled;
        sendUploadProgress = !enabled;
        sendOfflinePacketAfterOnline = enabled;

        editor.putBoolean("sendReadPackets", sendReadPackets)
                .putBoolean("sendReadStories", sendReadStories)
                .putBoolean("sendOnlinePackets", sendOnlinePackets)
                .putBoolean("sendUploadProgress", sendUploadProgress)
                .putBoolean("sendOfflinePacketAfterOnline", sendOfflinePacketAfterOnline)
                .apply();
    }

    public static void toggleGhostMode() {
        setGhostMode(!isGhostModeActive());
    }
}
