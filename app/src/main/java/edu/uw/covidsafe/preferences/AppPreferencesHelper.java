package edu.uw.covidsafe.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferencesHelper {

    public static final String ONBOARDING_PAGER_SHOWN = "onboardingshownalready";
    public static String SHARED_PREFENCE_NAME = "preferences";


    public static final SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
    }

    public static void setOnboardingShownToUser(Context context) {
        getSharedPreferences(context).edit().putBoolean(ONBOARDING_PAGER_SHOWN, true).apply();
    }

    public static boolean isOnboardingShownToUser(Context context) {
        return getSharedPreferences(context).getBoolean(ONBOARDING_PAGER_SHOWN, false);
    }
}
