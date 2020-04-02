package edu.uw.covidsafe.utils;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattServer;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.util.Log;

import androidx.fragment.app.Fragment;

import edu.uw.covidsafe.BackgroundService;
import com.example.covidsafe.R;
import edu.uw.covidsafe.ui.MainFragment;
import edu.uw.covidsafe.ui.health.SymptomTrackerFragment;
import edu.uw.covidsafe.ui.health.DiagnosisFragment;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import edu.uw.covidsafe.ui.HelpFragment;
import edu.uw.covidsafe.ui.onboarding.HealthFragment;
import edu.uw.covidsafe.ui.settings.SettingsFragment;
import unused.BlacklistRecord;
import unused.HistoryFragment;

import edu.uw.covidsafe.ui.onboarding.PermissionFragment;
import edu.uw.covidsafe.ui.onboarding.StartFragment;

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

    public enum SymptomsDatabaseOps {
        Insert,ViewAll
    }

    public enum GpsDatabaseOps {
        Insert,ViewAll
    }

    public enum UUIDDatabaseOps {
        Insert,ViewAll
    }

    public static int MaximumGpsPrecisionAllowed = 5;
    public static int DefaultGpsCoarsenessInDecimalPoints = 2;
    public static int InfectionWindowInDays = 14;
    public static int SPLASH_DISPLAY_LENGTH = 1000;
    public static String AnalyticsSecret = "4cd15ae0-9294-40ba-a8b5-a8d77b76783b";
    public static int BluetoothScanIntervalInMinutes = 5;
    public static int BluetoothScanPeriodInSeconds = 10;
    public static int PullFromServerIntervalInMinutes = 60;
    public static int LogPurgerIntervalInDays = 60;
    public static int UUIDGenerationIntervalInMinutes = 5;
    public static int CDCExposureTimeInMinutes = 10;
    public static int TimestampDeviationInMilliseconds = 1000;
    public static UUID GATT_SERVICE_UUID = UUID.fromString("8cf0282e-d80f-4eb7-a197-e3e0f965848d");
    public static UUID CHARACTERISTIC_UUID = UUID.fromString("d945590b-5b09-4144-ace7-4063f95bd0bb");
    public static UUID BEACON_SERVICE_UUID = UUID.fromString("0000D028-0000-1000-8000-00805F9B34FB");
    public static UUID contactUUID = null;

    public static String insertAsyncTaskRunning = "";

    public static boolean NOTIFS_ENABLED = true;
    public static boolean GPS_ENABLED = false;
    public static boolean BLUETOOTH_ENABLED = false;
    public static boolean LOG_TO_DISK = false;

    public static BluetoothGattServer gattServer;
    public static BluetoothDevice device;
    public static BluetoothGatt gatt;
    public static BluetoothAdapter blueAdapter;
    public static int statusSubmitted = -1;
    public static ScheduledFuture uploadTask;
    public static ScheduledFuture uuidGeneartionTask;
    public static ScheduledFuture bluetoothScanTask;
    public static ScheduledFuture bluetoothServerTask;
    public static ScheduledFuture pullFromServerTask;
    public static ScheduledFuture logPurgerTask;
    public static boolean startingToTrack = false;
    public static String SHARED_PREFENCE_NAME = "preferences";
    public static String NOTIFICATION_CHANNEL = "channel";
    public static Fragment MainFragment;
    public static Fragment HealthFragment;
    public static Fragment HelpFragment;
    public static Fragment HistoryFragment;
    public static Fragment SymptomTrackerFragment;
    public static Fragment DiagnosisFragment;
    public static Fragment SettingsFragment;
    public static Fragment CurrentFragment;
    public static Fragment PermissionsFragment;
    public static Fragment StartFragment;
    public static String gpsDirName = "gps";
    public static String bleDirName = "ble";
    public static String symptomsDirName = "symptoms";
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
    public static int SubmitThresh = 1;
    public static int DaysOfLogsToKeep= 14;
    public static float DistanceThresholdInMeters = 1609.34f;
    public static ArrayList<BlacklistRecord> blacklist;
    public static LocationManager mLocationManager = null;
    public static BackgroundService.LocationListener[] locListeners = new BackgroundService.LocationListener[2];
    public static HashSet<String> scannedUUIDs;
    public static HashMap<String,Integer> scannedUUIDsRSSIs;
    public static HashSet<String> writtenUUIDs;

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
        HealthFragment = new HealthFragment();
        HistoryFragment = new HistoryFragment();
        SymptomTrackerFragment = new SymptomTrackerFragment();
        DiagnosisFragment = new DiagnosisFragment();
        SettingsFragment = new SettingsFragment();
        PermissionsFragment = new PermissionFragment();
        StartFragment = new StartFragment();
        if (!DEBUG) {
            LOG_TO_DISK = false;
        }
        else {
            LOG_TO_DISK = true;
        }

        SharedPreferences prefs = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        Constants.BLUETOOTH_ENABLED = prefs.getBoolean(av.getString(R.string.ble_enabled_pkey), Constants.BLUETOOTH_ENABLED);
        Constants.GPS_ENABLED = prefs.getBoolean(av.getString(R.string.gps_enabled_pkey), Constants.GPS_ENABLED);
        Constants.NOTIFS_ENABLED = prefs.getBoolean(av.getString(R.string.notifs_enabled_pkey), Constants.NOTIFS_ENABLED);

    }
}
