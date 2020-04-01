package edu.uw.covidsafe.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Messenger;
import android.os.ParcelUuid;
import android.util.Log;

import edu.uw.covidsafe.utils.ByteUtils;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BluetoothUtils {
    public static Messenger messenger;
    // react appropriately if user turns of bluetooth off/on in middle of logging
    public static final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // It means the user has changed his bluetooth state.
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                if (Constants.blueAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                    Log.e("ble","BLE TURNED OFF");
                    if (Constants.blueAdapter != null && Constants.blueAdapter.getBluetoothLeAdvertiser() != null) {
                        Constants.blueAdapter.getBluetoothLeAdvertiser().stopAdvertising(BluetoothHelper.advertiseCallback);
                        BluetoothUtils.finishScan(context);
                    }
                    return;
                }
                if (Constants.blueAdapter.getState() == BluetoothAdapter.STATE_ON) {
                    Log.e("ble","BLE TURNED ON");
                    BluetoothUtils.startBluetoothScan(context, messenger);
                    mkBeacon();
                    return;
                }
            }
        }
    };

    public static void startBluetoothScan(Context context, Messenger messenger) {
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        Log.e("ble","start bluetooth scan "+(messenger==null));
        Constants.bluetoothTask = exec.scheduleWithFixedDelay(new BluetoothHelper(context, messenger), 0, Constants.BluetoothScanIntervalInMinutes, TimeUnit.MINUTES);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothUtils.finishScan(context);
                Log.e("ble", "STOPPED SCANNING");
            }
        }, Constants.BluetoothScanPeriodInSeconds*1000);
    }

    public static void finishScan(Context cxt) {
        Constants.blueAdapter.getBluetoothLeScanner().stopScan(BluetoothHelper.mLeScanCallback);
        for (String uuids : Constants.scannedUUIDs) {
            String[] elts = uuids.split("-");
            int rssi = Constants.scannedUUIDsRSSIs.get(uuids);
            Utils.bleLogToDatabase(cxt, uuids, rssi);
        }
    }

    public static void mkBeacon() {
        if (Constants.BLUETOOTH_ENABLED) {
            AdvertiseSettings settings = new AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                    .setConnectable(true)
                    .build();

            AdvertiseData advertiseData = new AdvertiseData.Builder()
                    .setIncludeDeviceName(false)
                    .addServiceUuid(new ParcelUuid(Constants.serviceUUID))
                    .addServiceData(new ParcelUuid(Constants.serviceUUID), ByteUtils.uuid2bytes(Constants.contactUUID))
                    .build();
            Log.e("ble","MKBEACON");
            Constants.scannedUUIDs = new HashSet<String>();
            Constants.scannedUUIDsRSSIs = new HashMap<String,Integer>();
            BluetoothLeAdvertiser bluetoothLeAdvertiser = Constants.blueAdapter.getBluetoothLeAdvertiser();
            bluetoothLeAdvertiser.startAdvertising(settings, advertiseData, BluetoothHelper.advertiseCallback);
        }
    }
}
