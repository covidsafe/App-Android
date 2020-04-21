package edu.uw.covidsafe;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.covidsafe.R;

import edu.uw.covidsafe.ble.BluetoothUtils;
import edu.uw.covidsafe.crypto.Constant;
import edu.uw.covidsafe.gps.GpsUtils;
import edu.uw.covidsafe.preferences.AppPreferencesHelper;
import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Constants;

public class LoggingService extends IntentService {

    public LoggingService() {
        super("LoggingService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("service", "logging onCreate");

        // service running notification
        Notification notification = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notif_message))
                .setSmallIcon(R.drawable.logo_purple)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(1, notification);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("service", "logging onHandleIntent");
        Constants.LoggingServiceRunning = true;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.e("service", "logging onStartCommand");
        Constants.LoggingServiceRunning = true;

        Bundle bundle = intent.getExtras();
//        Log.e("service", "bundle status " + (bundle == null));

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        boolean bleEnabled = AppPreferencesHelper.isBluetoothEnabled(getApplicationContext(), Constants.BLUETOOTH_ENABLED);
        boolean gpsEnabled = AppPreferencesHelper.isGPSEnabled(getApplicationContext(), Constants.GPS_ENABLED);

        if (bleEnabled) {
            BluetoothUtils.startBle(getApplicationContext());
        }

        if (gpsEnabled) {
            GpsUtils.startGps(getApplicationContext());
        }

        //////////////////////////////////////////////////////////////////////////////////////////

        return START_NOT_STICKY;
    }

    //this call is not guaranteed by android system
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("service", "service destroyed");
        Constants.LoggingServiceRunning = false;
    }

}