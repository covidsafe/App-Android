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
import edu.uw.covidsafe.gps.GpsUtils;
import edu.uw.covidsafe.utils.Constants;

public class LoggingService extends IntentService {

    public LoggingService() {
        super("LocationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("service", "create");

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
        Log.e("service", "handle intent");
        Constants.LoggingServiceRunning = true;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.e("service", "start");
        Constants.LoggingServiceRunning = true;

        Bundle bundle = intent.getExtras();
        Log.e("service", "bundle status " + (bundle == null));

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        boolean bleEnabled = prefs.getBoolean(getApplicationContext().getString(R.string.ble_enabled_pkey), Constants.BLUETOOTH_ENABLED);
        boolean gpsEnabled = prefs.getBoolean(getApplicationContext().getString(R.string.gps_enabled_pkey), Constants.GPS_ENABLED);

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