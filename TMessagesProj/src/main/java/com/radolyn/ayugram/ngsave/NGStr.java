/*
 * NGram: string loader for NGram screens. Texts come from the generated NGTexts class
 * (ru or en, chosen in NGram -> Language), then from Android resources as a fallback.
 */
package com.radolyn.ayugram.ngsave;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;

import java.util.Locale;

public class NGStr {
    private static final String PREFS = "ngram";
    private static final String KEY_LANGUAGE = "ngram_language";
    private static volatile String language;
    private static Context russianContext;

    private static SharedPreferences prefs() {
        return ApplicationLoader.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    /** Возвращает "ru" или "en", по умолчанию "ru". */
    public static String getLanguage() {
        String value = language;
        if (value == null) {
            try {
                value = prefs().getString(KEY_LANGUAGE, "ru");
            } catch (Throwable e) {
                value = "ru";
            }
            value = "en".equals(value) ? "en" : "ru";
            language = value;
        }
        return value;
    }

    public static void setLanguage(String lang) {
        String value = "en".equals(lang) ? "en" : "ru";
        language = value;
        try {
            prefs().edit().putString(KEY_LANGUAGE, value).apply();
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public static synchronized String get(int res) {
        String ngText = NGTexts.find(res);
        if (ngText != null) {
            return ngText;
        }
        try {
            Context base = ApplicationLoader.applicationContext;
            if ("en".equals(getLanguage())) {
                return base.getString(res);
            }
            if (russianContext == null) {
                Configuration configuration = new Configuration(base.getResources().getConfiguration());
                configuration.setLocale(new Locale("ru"));
                russianContext = base.createConfigurationContext(configuration);
            }
            return russianContext.getResources().getString(res);
        } catch (Throwable e) {
            FileLog.e(e);
            return "";
        }
    }
}
