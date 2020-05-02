package edu.uw.covidsafe;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.covidsafe.R;

import edu.uw.covidsafe.ble.BluetoothUtils;
import edu.uw.covidsafe.gps.GpsUtils;
import edu.uw.covidsafe.preferences.AppPreferencesHelper;
import edu.uw.covidsafe.utils.Constants;

import static edu.uw.covidsafe.CovidSafeApplication.LOGGING_SERVICE_CHANNEL_ID;

public class LoggingService extends Service {

    private static final String TAG = "LoggingServiceV2";


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service stopped");
        Constants.LoggingServiceRunning = true;
        Notification notification = new NotificationCompat.Builder(this, LOGGING_SERVICE_CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notif_message))
                .setSmallIcon(R.drawable.logo_purple)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setVibrate(new long[]{0L})
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Perform logging work
        Log.d(TAG, "Service is running...");
        performLoggingWork();
//        return START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    private void performLoggingWork() {
        boolean bleEnabled = AppPreferencesHelper.isBluetoothEnabled(getApplicationContext(), Constants.BLUETOOTH_ENABLED);
        boolean gpsEnabled = AppPreferencesHelper.isGPSEnabled(getApplicationContext(), Constants.GPS_ENABLED);

        if (bleEnabled) {
            BluetoothUtils.startBle(getApplicationContext());
        }

        if (gpsEnabled) {
            GpsUtils.startGps(getApplicationContext());
        }
    }

    @Override
    public void onDestroy() {
        Constants.LoggingServiceRunning = false;
        stopLoggingService();
        super.onDestroy();
    }

    private void stopLoggingService() {
        //GpsUtils.haltGps();
      //  BluetoothUtils.haltBle(this);
        Log.d(TAG, "Service destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
