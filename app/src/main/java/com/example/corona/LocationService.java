package com.example.corona;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LocationService extends Service {
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 1f;
    LocationListener[] locListeners = new LocationListener[2];
    Handler serviceHandler;
//    IBinder mBinder = new LocalBinder();

//    public class LocalBinder extends Binder {
//        public void registerHandler(Handler handler) {
//            Log.e("logme","handler registered "+(handler==null));
//            serviceHandler = handler;
//        }
//    }

    private class LocationListener implements android.location.LocationListener {

        String provider;

        public LocationListener(String provider) {
            this.provider = provider;
        }

        @Override
        public void onLocationChanged(Location location) {
            DateFormat dateFormat = new SimpleDateFormat("hh:mm.ss aa");
            Date dd = new Date();
            Log.e("logme", dateFormat.format(dd));
            Utils.gpsLog(getApplicationContext(), location);
        }

        @Override
        public void onProviderDisabled(String provider) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Notification notification = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notif_message))
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .build();
        startForeground(1,notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        initializeLocationManager();
        try {
            String[] providers = new String[] {LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER};
            Log.e("logme","request");

            locListeners[0] = new LocationListener(LocationManager.NETWORK_PROVIDER);
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    locListeners[0]);
            locListeners[1] = new LocationListener(LocationManager.GPS_PROVIDER);
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    locListeners[1]);

            ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
            Constants.uploadTask = exec.scheduleWithFixedDelay(new UploadTask(getApplicationContext()), 0, 1, TimeUnit.HOURS);
            if (Constants.BLUETOOTH_ENABLED) {
                Log.e("ble","spin out task");
                Constants.bluetoothTask = exec.scheduleWithFixedDelay(new BluetoothHelper(), 0, 1, TimeUnit.HOURS);
            }
        } catch (java.lang.SecurityException ex) {
            Log.e("logme", "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.e("logme", "gps provider does not exist " + ex.getMessage());
        } catch(Exception e ){
            Log.e("logme",e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e("logme", "service destroyed");
        super.onDestroy();
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(locListeners[0]);
                mLocationManager.removeUpdates(locListeners[1]);
            } catch (Exception ex) {
                Log.e("logme", "fail to remove location listners, ignore", ex);
            }
        }
    }

    private void initializeLocationManager() {
        Log.e("logme", "initializeLocationManager");
        if (mLocationManager == null) {
            Log.e("logme", "initializeLocationManager2");
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}