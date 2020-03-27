package com.example.covidsafe.utils;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.example.covidsafe.BackgroundService;
import com.example.covidsafe.R;
import com.example.covidsafe.ui.MainFragment;
import com.example.covidsafe.ui.ReportFragment;
import com.example.covidsafe.ui.WarningFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import unused.BlacklistRecord;
import unused.HelpFragment;
import unused.HistoryFragment;
import unused.SettingsFragment;

public class Constants {

    public enum UploadSources {
        Disk, Db
    }

    public static boolean WRITE_TO_DISK = true;
    public static boolean DEBUG = true;
    public static UploadSources UploadSource = UploadSources.Disk;

    public enum BleDatabaseOps {
        Insert,ViewAll
    }

    public enum GpsDatabaseOps {
        Insert,ViewAll
    }

    public enum UUIDDatabaseOps {
        Insert,ViewAll
    }

    public static int BluetoothScanIntervalInMinutes = 5;
    public static int BluetoothScanPeriodInSeconds = 10;
    public static int PullFromServerIntervalInMinutes = 60;
    public static int UUIDGenerationIntervalInMinutes = 5;
    public static UUID serviceUUID = UUID.fromString("0000D028-0000-1000-8000-00805F9B34FB");
    public static UUID contactUUID = null;

    public static String insertAsyncTaskRunning = "";

    public static boolean GPS_ENABLED = true;
    public static boolean BLUETOOTH_ENABLED = true;
    public static boolean LOG_TO_DISK = false;

    public static BluetoothDevice device;
    public static BluetoothGatt gatt;
    public static BluetoothAdapter blueAdapter;
    public static int statusSubmitted = -1;
    public static ScheduledFuture uploadTask;
    public static ScheduledFuture uuidGeneartionTask;
    public static ScheduledFuture bluetoothTask;
    public static ScheduledFuture pullFromServerTask;
    public static boolean startingToTrack = false;
    public static String SHARED_PREFENCE_NAME = "preferences";
    public static String NOTIFICATION_CHANNEL = "channel";
    public static Fragment MainFragment;
    public static Fragment HelpFragment;
    public static Fragment HistoryFragment;
    public static Fragment ReportFragment;
    public static Fragment WarningFragment;
    public static Fragment SettingsFragment;
    public static Fragment CurrentFragment;
    public static String gpsDirName = "gps";
    public static String bleDirName = "ble";
    public static String uuidDirName = "uuid";
    public static String formDirName = "form";
    public static String blacklistDirName = "blacklist";
    public static String logFileName = "log.txt";
    public static String lastSentName = "lastsent";
    public static String lastSentFileName = "lastsent.txt";
    public static String blacklistFileName = "blacklist.txt";
    public static String DiagnosisReportFileName = "diagnosis.txt";
    public static boolean tracking = false;
    public static int MaxBlacklistSize = 3;
    public static int NumFilesToDisplay = 14;
    public static int SubmitThresh = 0;
    public static float DistanceThresholdInMeters = 1609.34f;
    public static ArrayList<BlacklistRecord> blacklist;
    public static LocationManager mLocationManager = null;
    public static BackgroundService.LocationListener[] locListeners = new BackgroundService.LocationListener[2];
    public static HashSet<String> scannedUUIDs;

    public static String[] gpsPermissions= {
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    public static String[] blePermissions= {
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH,
    };
    public static String[] miscPermissions= {
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.INTERNET
    };

    public static void init(Activity av) {
        Log.e("logme","constants init");
        MainFragment = new MainFragment();
        HelpFragment = new HelpFragment();
        HistoryFragment = new HistoryFragment();
        ReportFragment = new ReportFragment();
        WarningFragment = new WarningFragment();
        SettingsFragment = new SettingsFragment();
        if (!DEBUG) {
            LOG_TO_DISK = false;
        }
        else {
            LOG_TO_DISK = true;
        }

        SharedPreferences prefs = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        Constants.BLUETOOTH_ENABLED = prefs.getBoolean(av.getString(R.string.ble_enabled_pkey), Constants.BLUETOOTH_ENABLED);
        Constants.GPS_ENABLED = prefs.getBoolean(av.getString(R.string.gps_enabled_pkey), Constants.GPS_ENABLED);
    }
}
