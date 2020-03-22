package com.example.corona;

import android.Manifest;
import android.content.Context;

import androidx.fragment.app.Fragment;

public class Constants {
    public static Fragment MainFragment;
    public static Fragment HelpFragment;
    public static Fragment HistoryFragment;
    public static Fragment ReportFragment;
    public static Fragment CurrentFragment;
    public static String gpsDirName = "gps";
    public static String bleDirName = "ble";
    public static String formDirName = "form";
    public static String logFileName = "log.txt";
    public static int SubmitThresh = 1;
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
    }
}
