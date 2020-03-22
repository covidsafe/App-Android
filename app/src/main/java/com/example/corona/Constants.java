package com.example.corona;

import android.Manifest;
import android.content.Context;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.LinkedList;

public class Constants {
    public static Fragment MainFragment;
    public static Fragment HelpFragment;
    public static Fragment HistoryFragment;
    public static Fragment ReportFragment;
    public static Fragment WarningFragment;
    public static Fragment SettingsFragment;
    public static Fragment CurrentFragment;
    public static String gpsDirName = "gps";
    public static String bleDirName = "ble";
    public static String formDirName = "form";
    public static String blacklistDirName = "blacklist";
    public static String logFileName = "log.txt";
    public static String blacklistFileName = "blacklist.txt";
    public static boolean DEBUG = false;
    public static int MaxBlacklistSize = 3;
    public static int NumFilesToDisplay = 14;
    public static int SubmitThresh = 0;
    public static float DistanceThresholdInMeters = 1609.34f;
    public static ArrayList<BlacklistRecord> blacklist;
//    public static String[] scope = {"user.read"};
//    public static String baliResourceURL = "https://graph.microsoft.com/v1.0/me";
    public static String[] permissions={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION};
    public static void init() {
        MainFragment = new MainFragment();
        HelpFragment = new HelpFragment();
        HistoryFragment = new HistoryFragment();
        ReportFragment = new ReportFragment();
        WarningFragment = new WarningFragment();
        SettingsFragment = new SettingsFragment();
    }
}
