package edu.uw.covidsafe.utils;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattServer;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.covidsafe.R;

import edu.uw.covidsafe.ui.MainFragment;
import edu.uw.covidsafe.ui.health.TipRecyclerViewAdapter;
import edu.uw.covidsafe.ui.notif.HistoryRecyclerViewAdapter;
import edu.uw.covidsafe.ui.notif.NotifRecyclerViewAdapter;
import unused.SymptomTrackerFragment;
import edu.uw.covidsafe.ui.health.DiagnosisFragment;

import java.security.KeyStore;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import javax.crypto.SecretKey;

import edu.uw.covidsafe.ui.faq.FaqFragment;
import edu.uw.covidsafe.ui.health.HealthFragment;
import edu.uw.covidsafe.ui.settings.SettingsFragment;
import edu.uw.covidsafe.ui.contact_log.ContactLogFragment;

import edu.uw.covidsafe.ui.onboarding.PermissionFragment;
import edu.uw.covidsafe.ui.onboarding.PagerFragment;

public class Constants {

    public enum MessageType {
        Exposure,NarrowCast
    }

    public static boolean WRITE_TO_DISK = false;
    public static boolean DEBUG = true;

    public enum BleDatabaseOps {
        Insert,ViewAll
    }

    public enum NotifDatabaseOps {
        Insert,ViewAll,DeleteAll
    }

    public enum SymptomsDatabaseOps {
        Insert,ViewAll
    }

    public enum GpsDatabaseOps {
        Insert,ViewAll
    }

    public enum UUIDDatabaseOps {
        BatchInsert, Insert,ViewAll,DeleteAll
    }

    public static TipRecyclerViewAdapter MainTipAdapter;
    public static TipRecyclerViewAdapter DiagnosisTipAdapter;
    public static NotifRecyclerViewAdapter NotificationAdapter;
    public static HistoryRecyclerViewAdapter HistoryAdapter;
    public static boolean PullServiceRunning = false;
    public static boolean LoggingServiceRunning = false;
    public static boolean SuppressSwitchStateCheck = false;
    public static int QuarantineLengthInDays = 14;
    public static int MaxPayloadSize = 1000;
    public static int rssiCutoff = -82;
    public static int MaximumGpsPrecision = 4;
    public static int MinimumGpsPrecision = 0;
    public static int SPLASH_DISPLAY_LENGTH = 1000;
    public static String AnalyticsSecret = "4cd15ae0-9294-40ba-a8b5-a8d77b76783b";
    public static int BluetoothScanIntervalInMinutes = 5;
    public static int BluetoothScanPeriodInSeconds = 10;
    public static int PullFromServerIntervalInMinutes = 60;
    public static int PullFromServerIntervalInMilliseconds = PullFromServerIntervalInMinutes*60*1000;
    public static boolean PullFromServerTaskRunning = false;
    public static int LogPurgerIntervalInDays = 1;
    public static int UUIDGenerationIntervalInMinutes = 15;
    public static int UUIDGenerationIntervalInSeconds = UUIDGenerationIntervalInMinutes*60;
    public static int CDCExposureTimeInMinutes = 10;
    public static int TimestampDeviationInMilliseconds = 10*1000;
    public static int InfectionWindowIntervalDeviationInMilliseconds = 60*1000;
    public static UUID GATT_SERVICE_UUID = UUID.fromString("8cf0282e-d80f-4eb7-a197-e3e0f965848d");
    public static UUID CHARACTERISTIC_UUID = UUID.fromString("d945590b-5b09-4144-ace7-4063f95bd0bb");
    public static UUID BEACON_SERVICE_UUID = UUID.fromString("0000D028-0000-1000-8000-00805F9B34FB");
    public static UUID contactUUID = null;

    //GPS_TIME_INTERVAL and GPS_LOCATION_INTERVAL used to control frequency of location updates
    //to optimize for power, note that GPS_TIME_INTERVAL is the primary method by which
    // power is conserverd.
    public static final int GPS_TIME_INTERVAL_IN_MINUTES = 10;
    public static final int GPS_TIME_INTERVAL_IN_MILLISECONDS = 1000*60*GPS_TIME_INTERVAL_IN_MINUTES;

    // our maximum GPS precision corresponds to ~7km
    // our spatial sampling rate should be 2x less than that
    public static final float GPS_LOCATION_INTERVAL_IN_METERS = 3500;

    public static boolean NOTIFS_ENABLED = false;
    public static boolean GPS_ENABLED = false;
    public static boolean BLUETOOTH_ENABLED = false;
    public static boolean LOG_TO_DISK = false;

    public static Switch gpsSwitch;
    public static Switch bleSwitch;
    public static TextView bleDesc;
    public static Switch notifSwitch;

    public static SecretKey secretKey;
    public static KeyStore keyStore;
    public static int IV_LEN = 12;
    public static int GCM_TLEN = 128;
    public static String CharSet = "UTF-8";
    public static String AES_SETTINGS = "AES/GCM/NoPadding";
    public static String RSA_SETTINGS = "RSA/ECB/PKCS1Padding";
    public static String KEY_PROVIDER = "AndroidKeyStore";
    public static String KEY_ALIAS = "mykeys";
    public static BluetoothGattServer gattServer;
    public static BluetoothAdapter blueAdapter;
    public static int statusSubmitted = -1;
    public static ScheduledFuture uuidGeneartionTask;
    public static ScheduledFuture bluetoothScanTask;
    public static Timer pullFromServerTaskTimer;
    public static ScheduledFuture logPurgerTask;
    public static boolean startingToTrack = false;
    public static String SHARED_PREFENCE_NAME = "preferences";
    public static String NOTIFICATION_CHANNEL = "channel";
    public static Fragment MainFragment;
    public static Fragment ReportFragmentState;
    public static Fragment MainFragmentState;
    public static Fragment HealthFragment;
    public static Fragment SymptomTrackerFragment;
    public static Fragment DiagnosisFragment;
    public static Fragment SettingsFragment;
    public static Fragment FaqFragment;
    public static Fragment ContactLogFragment;
    public static Fragment CurrentFragment;
    public static Fragment PermissionsFragment;
    public static Fragment PagerFragment;
    public static String notifDirName = "notif";
    public static String gpsDirName = "gps";
    public static String bleDirName = "ble";
    public static String symptomsDirName = "symptoms";
    public static String uuidDirName = "uuid";
    public static String lastSentName = "lastsent";
    public static boolean tracking = false;
    public static int NumFilesToDisplay = 14;
    public static int SubmitThresh = 1;
    public static int DefaultInfectionWindowInDays = 14;
    public static int DefaultDaysOfLogsToKeep = DefaultInfectionWindowInDays;
    public static LocationManager mLocationManager = null;
    public static HashSet<String> scannedUUIDs;
    public static HashMap<String,Integer> scannedUUIDsRSSIs;
    public static HashSet<String> writtenUUIDs;
    public static int pageNumber = -1;

    public static String[] gpsPermissions= {
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    public static String[] gpsPermissionsLite= {
//            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
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
        SettingsFragment = new SettingsFragment();

        ContactLogFragment = new ContactLogFragment();

        DiagnosisFragment = new DiagnosisFragment();
        HealthFragment = new HealthFragment();

        FaqFragment = new FaqFragment();

        PermissionsFragment = new PermissionFragment();
        PagerFragment = new PagerFragment();

        SymptomTrackerFragment = new SymptomTrackerFragment();
        ReportFragmentState = HealthFragment;
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
