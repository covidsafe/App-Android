package edu.uw.covidsafe.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.example.covidsafe.R;

import java.util.Locale;

import edu.uw.covidsafe.utils.Constants;

public class LocaleHelper {

    public static Context onAttach(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Locale systemLocale = context.getResources().getConfiguration().locale;

        String storedLocale = prefs.getString(context.getString(R.string.lang_pkey), "");
        if (storedLocale.isEmpty()) {
            if (Constants.languages.contains(systemLocale.getLanguage())) {
                editor.putString(context.getString(R.string.lang_pkey),
                        systemLocale.getLanguage());
                editor.commit();
                return setLocale(context, systemLocale.getLanguage());
            }
            else {
                editor.putString(context.getString(R.string.lang_pkey),
                        Constants.defaultLocale);
                editor.commit();
                return setLocale(context, Constants.defaultLocale);
            }
        }
        else {
            return setLocale(context, storedLocale);
        }
    }

    /**
     * Set the app's locale to the one specified by the given String.
     *
     * @param context
     * @param localeSpec a locale specification as used for Android resources (NOTE: does not
     *                   support country and variant codes so far); the special string "system" sets
     *                   the locale to the locale specified in system settings
     * @return
     */
    public static Context setLocale(Context context, String localeSpec) {
        Locale locale;
        if (localeSpec.equals("system")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = Resources.getSystem().getConfiguration().getLocales().get(0);
            } else {
                //noinspection deprecation
                locale = Resources.getSystem().getConfiguration().locale;
            }
        } else {
            locale = new Locale(localeSpec);
        }
        Locale.setDefault(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, locale);
        } else {
            return updateResourcesLegacy(context, locale);
        }
    }

    // sets the locale in the context for the newer APIs
    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, Locale locale) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration);
    }

    // sets the locale in the context for the older APIs
    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }
}
