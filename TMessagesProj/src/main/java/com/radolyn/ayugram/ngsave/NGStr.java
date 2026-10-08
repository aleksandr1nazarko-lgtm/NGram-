/*
 * NGram: string loader for NGram screens.
 * Telegram 12.x reads strings from a prebuilt bundle, so strings added to strings.xml are not found by
 * LocaleController.getString() and show up as "LOC_ERR:null". This reads them from Android resources
 * directly, always in Russian (section names stay in English where the resource says so).
 */
package com.radolyn.ayugram.ngsave;
import android.content.Context;
import android.content.res.Configuration;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import java.util.Locale;
public class NGStr {
    private static Context russianContext;
    public static synchronized String get(int res) {
        try {
            if (russianContext == null) {
                Context base = ApplicationLoader.applicationContext;
                Configuration configuration = new Configuration(base.getResources().getConfiguration());
                configuration.setLocale(new Locale("ru"));
                russianContext = base.createConfigurationContext(configuration);
            }
            return russianContext.getResources().getString(res);
        } catch (Throwable e) {
            FileLog.e(e);
            try {
                return ApplicationLoader.applicationContext.getString(res);
            } catch (Throwable ignore) {
                return "";
            }
        }
    }
}
