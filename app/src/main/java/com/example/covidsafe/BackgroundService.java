package com.example.covidsafe;
import android.app.IntentService;
import android.app.Notification;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.covidsafe.R;
import com.example.covidsafe.ble.BluetoothHelper;
import com.example.covidsafe.utils.Constants;
import com.example.covidsafe.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BackgroundService extends IntentService {

    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 1f;
    LocationListener[] locListeners = new LocationListener[2];
    Messenger messenger;

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

            Bundle bb = new Bundle();
            bb.putString("gps", location.getLatitude()+","+location.getLongitude());
            Message msg = new Message();
            msg.setData(bb);
            try {
//                Log.e("test","sending");
                messenger.send(msg);
            } catch (RemoteException e) {
                Log.i("error", "error");
            }

            Utils.gpsLogToDatabase(getApplicationContext(), location);
        }

        @Override
        public void onProviderDisabled(String provider) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }
    }

    public BackgroundService() {
        super("LocationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("logme","handle intent");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        Log.e("ex","bundle status "+(bundle==null));
        if (bundle != null) {
            messenger = (Messenger) bundle.get("messenger");
//            msg = Message.obtain();
        }

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
//            Constants.uploadTask = exec.scheduleWithFixedDelay(new UploadTask(getApplicationContext()), 0, 1, TimeUnit.HOURS);
            if (Constants.BLUETOOTH_ENABLED) {
                Log.e("ble","spin out task "+(messenger==null));
                Constants.bluetoothTask = exec.scheduleWithFixedDelay(new BluetoothHelper(getApplicationContext(), messenger), 0, 1, TimeUnit.HOURS);
                Log.e("ble","make beacon");
                mkBeacon();
            }
        } catch (java.lang.SecurityException ex) {
            Log.e("logme", "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.e("logme", "gps provider does not exist " + ex.getMessage());
        } catch(Exception e ){
            Log.e("logme",e.getMessage());
        }

        Notification notification = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notif_message))
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(1,notification);

        return START_NOT_STICKY;
    }

    public void mkBeacon() {

        if (Constants.BLUETOOTH_ENABLED) {
            AdvertiseSettings settings = new AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                    .setConnectable(true)
                    .build();

            AdvertiseData advertiseData = new AdvertiseData.Builder()
                    .setIncludeDeviceName(true)
                    .setIncludeTxPowerLevel(true)
//                    .addServiceUuid(ParcelUuid(serviceUUID))
//                    .addServiceData(ParcelUuid(serviceUUID), contactEventUUID?.toBytes())
                    .build();

//            AdvertiseData scanResponseData = new AdvertiseData.Builder()
//                    .addServiceUuid(new ParcelUuid(UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca3e")))
//                    .setIncludeTxPowerLevel(true)
//                    .build();

            Constants.blueAdapter.setName(getString(R.string.app_name));
            BluetoothLeAdvertiser bluetoothLeAdvertiser = Constants.blueAdapter.getBluetoothLeAdvertiser();
            bluetoothLeAdvertiser.startAdvertising(settings, advertiseData, BluetoothHelper.callback);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("logme", "service destroyed");
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(locListeners[0]);
                mLocationManager.removeUpdates(locListeners[1]);
                Constants.blueAdapter.getBluetoothLeAdvertiser().stopAdvertising(BluetoothHelper.callback);
                Constants.blueAdapter.getBluetoothLeScanner().stopScan(BluetoothHelper.mLeScanCallback);
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