/*
 * NGram: Local premium switch (client side only, nothing is bought or sent to the server).
 */
package com.radolyn.ayugram.ngsave;
import android.app.Activity;
import android.content.SharedPreferences;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
public class NGPremium {
    private static SharedPreferences preferences;
    private static Boolean cached;
    private static synchronized SharedPreferences prefs() {
        if (preferences == null) {
            preferences = ApplicationLoader.applicationContext.getSharedPreferences("ngpremium", Activity.MODE_PRIVATE);
        }
        return preferences;
    }
    public static boolean enabled() {
        Boolean value = cached;
        if (value == null) {
            value = prefs().getBoolean("localPremium", false);
            cached = value;
        }
        return value;
    }
    public static void set(boolean value) {
        cached = value;
        prefs().edit().putBoolean("localPremium", value).apply();
        for (int a = 0; a < UserConfig.MAX_ACCOUNT_COUNT; a++) {
            if (UserConfig.getInstance(a).isClientActivated()) {
                MessagesController.getInstance(a).updatePremium(value || UserConfig.getInstance(a).getCurrentUser() != null && UserConfig.getInstance(a).getCurrentUser().premium);
                NotificationCenter.getInstance(a).postNotificationName(NotificationCenter.currentUserPremiumStatusChanged);
            }
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.premiumStatusChangedGlobal);
    }
}
