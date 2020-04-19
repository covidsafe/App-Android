package edu.uw.covidsafe.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import edu.uw.covidsafe.crypto.Constant;
import edu.uw.covidsafe.utils.Constants;

public class AppPreferencesHelper {

    public static final String ONBOARDING_PAGER_SHOWN = "onboardingshownalready";
    public static final String BLUETOOTH_ENABLED = "bleEnabled";
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

    public static void setBluetoothEnabled(Context context) {
        setBluetoothEnabled(context, true);
    }

    public static boolean isBluetoothEnabled(Context context) {
        return getSharedPreferences(context).getBoolean(BLUETOOTH_ENABLED, false);
    }

    public static boolean isBluetoothEnabled(Context context, boolean defaultFlag) {
        return getSharedPreferences(context).getBoolean(BLUETOOTH_ENABLED, defaultFlag);
    }

    public static void setBluetoothEnabled(Context context, boolean bleFlagVal) {
        getSharedPreferences(context).edit().putBoolean(BLUETOOTH_ENABLED, bleFlagVal);
    }
}
