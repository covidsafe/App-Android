package edu.uw.covidsafe.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.ParcelUuid;
import android.util.Log;

import com.example.covidsafe.R;

import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.uw.covidsafe.preferences.AppPreferencesHelper;
import edu.uw.covidsafe.seed_uuid.UUIDGeneratorTask;
import edu.uw.covidsafe.utils.ByteUtils;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

import static android.content.Context.BLUETOOTH_SERVICE;
import java.util.Collection;

public class BluetoothUtils {

    public static AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.e("ble", "BLE advertisement added successfully " + settingsInEffect.toString());
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.e("ble", "Failed to add BLE advertisement, reason: " + errorCode);
        }
    };

    // react appropriately if user turns of bluetooth off/on in middle of logging
    // this broadcast receiver is only registered when the logging is in process
    public static final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();

            if (Constants.blueAdapter == null) {
                BluetoothManager bluetoothManager =
                        (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
                Constants.blueAdapter = bluetoothManager.getAdapter();
            }

            // It means the user has changed his bluetooth state.
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                if (Constants.blueAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                    if (Constants.LoggingServiceRunning) {
                        BluetoothUtils.haltBle(context);
                    }
                    AppPreferencesHelper.setBluetoothEnabled(context, false);
                    if (Constants.bleSwitch != null) {
                        Constants.bleSwitch.setChecked(false);
                    }
                    if (Constants.bleDesc != null) {
                        Constants.bleDesc.setText(context.getString(R.string.bluetooth_is_off));
                    }
                    return;
                }
                if (Constants.blueAdapter.getState() == BluetoothAdapter.STATE_ON) {
                    Log.e("ble", "BLE TURNED ON");
                    if (Constants.LoggingServiceRunning) {
                        // the bluetooth sensor is turned on
                        BluetoothUtils.startBluetoothScan(context);
                        BluetoothServerHelper.createServer(context);
                        mkBeacon(context);
                    }

                    if (Utils.hasBlePermissions(context)) {
                        AppPreferencesHelper.setBluetoothEnabled(context);
                        if (Constants.bleSwitch != null) {
                            Constants.bleSwitch.setChecked(true);
                        }
                        if (Constants.bleDesc != null) {
                            Constants.bleDesc.setText(context.getString(R.string.perm3desc));
                        }
                    }
                    return;
                }
            }
        }
    };

    public static void startBluetoothScan(Context cxt) {
        if (Constants.BLE_PROTOCOL_VERSION == 1) {
            startBluetoothScanV1(cxt);
        }
        else if (Constants.BLE_PROTOCOL_VERSION == 2) {
            startBluetoothScanV2(cxt);
        }
    }

    public static void startBluetoothScanV1(Context cxt) {
        if (Constants.bluetoothScanTask == null || Constants.bluetoothScanTask.isDone()) {
            Log.e("blebug", "start bluetooth scan ");
            ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
            if (Constants.DEBUG) {
                Constants.bluetoothScanTask = exec.scheduleWithFixedDelay(new BluetoothScanHelper(cxt),
                        0, Constants.BluetoothScanIntervalInSecondsDebug, TimeUnit.SECONDS);
            } else {
                Constants.bluetoothScanTask = exec.scheduleWithFixedDelay(new BluetoothScanHelper(cxt),
                        0, Constants.BluetoothScanIntervalInMinutes, TimeUnit.MINUTES);
            }
        }
    }

    public static void startBluetoothScanV2(Context cxt) {
        if (Constants.bluetoothScanTask == null || Constants.bluetoothScanTask.isDone()) {
            Log.e("blebug", "start bluetooth scan ");
            ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
            if (Constants.DEBUG) {
                Constants.bluetoothScanTask = exec.scheduleWithFixedDelay(new BluetoothScanHelperV2(cxt),
                        0, Constants.BluetoothScanIntervalInSecondsDebug, TimeUnit.SECONDS);
            } else {
                Constants.bluetoothScanTask = exec.scheduleWithFixedDelay(new BluetoothScanHelperV2(cxt),
                        0, Constants.BluetoothScanIntervalInMinutes, TimeUnit.MINUTES);
            }
        }
    }

    public static void finishScan(Context cxt) {
        if (Constants.BLE_PROTOCOL_VERSION == 1) {
            finishScanV1(cxt);
        }
        else if (Constants.BLE_PROTOCOL_VERSION == 2) {
            finishScanV2(cxt);
        }
    }

    public static void finishScanV1(Context cxt) {
        if (cxt != null) {
            BluetoothManager bluetoothManager =
                    (BluetoothManager) cxt.getSystemService(Context.BLUETOOTH_SERVICE);
            if (Constants.blueAdapter != null && bluetoothManager != null && isBluetoothOn() &&
                    Constants.blueAdapter.getBluetoothLeScanner() != null) {
                Constants.blueAdapter.getBluetoothLeScanner().stopScan(BluetoothScanHelper.mLeScanCallback);
            }
            Log.e("blebug", "finish scan");
            Log.e("blebug", (Constants.scannedUUIDs == null) + "," + (Constants.scannedUUIDsRSSIs == null) + "," + (Constants.scannedUUIDsTimes == null));
            if (Constants.scannedUUIDs != null && Constants.scannedUUIDsRSSIs != null &&
                    Constants.scannedUUIDsTimes != null) {
                Log.e("blebug", (Constants.scannedUUIDs.size()) + "," + (Constants.scannedUUIDsRSSIs.keySet().size()) + "," + (Constants.scannedUUIDsTimes.keySet().size()));
                for (String uuid : Constants.scannedUUIDs) {
                    if (Constants.scannedUUIDsRSSIs.containsKey(uuid) &&
                            Constants.scannedUUIDsTimes.containsKey(uuid)) {
                        int rssi = Constants.scannedUUIDsRSSIs.get(uuid);
                        long ts = Constants.scannedUUIDsTimes.get(uuid);
                        Utils.bleLogToDatabase(cxt, uuid, rssi, ts, 0);
                    }
                }
            }
        }
    }

    public static void finishScanV2(Context cxt) {
        if (cxt != null) {
            BluetoothManager bluetoothManager =
                    (BluetoothManager) cxt.getSystemService(Context.BLUETOOTH_SERVICE);
            if (Constants.blueAdapter != null && bluetoothManager != null && isBluetoothOn() &&
                    Constants.blueAdapter.getBluetoothLeScanner() != null) {
                Constants.blueAdapter.getBluetoothLeScanner().stopScan(BluetoothScanHelperV2.mLeScanCallback);
            }
            Log.e("blebug", "finish scan");
            Log.e("blebug", (Constants.scannedUUIDs == null) + "," + (Constants.scannedUUIDsRSSIs == null) + "," + (Constants.scannedUUIDsTimes == null));
        }
    }

    public static void haltBle(Context av) {
        if (Constants.blueAdapter != null && Constants.blueAdapter.getBluetoothLeAdvertiser() != null) {
            Constants.blueAdapter.getBluetoothLeAdvertiser().stopAdvertising(BluetoothUtils.advertiseCallback);
        }
        BluetoothUtils.finishScan(av);
        BluetoothServerHelper.stopServer();

        if (Constants.uuidGeneartionTask != null) {
            Constants.uuidGeneartionTask.cancel(true);
        }
    }

    public static boolean rssiThresholdCheck(int rssi, int device) {
        if (device == 0 || !Constants.bleThresholds.containsKey(device)) {
            return rssi >= Constants.rssiCutoff;
        }
        else {
            return rssi >= Constants.bleThresholds.get(device);
        }
    }

    public static void startBle(Context cxt) {
        Log.e("ble", "spin out task ");
        BluetoothUtils.startBluetoothScan(cxt);
        BluetoothServerHelper.createServer(cxt);
        Log.e("ble", "make beacon");
        // run this once to get a seed and broadcast it
        // have the generator be triggered at synchronized fixed 15 minute intervals:
        // e.g. 10:15, 10:30, 10:45
//        OneTimeWorkRequest oneTimePullRequest = new OneTimeWorkRequest.Builder(
//                UUIDGeneratorWorker.class)
//                .build();
//        WorkManager.getInstance(cxt).enqueue(oneTimePullRequest);

        if (Constants.uuidGeneartionTask == null || Constants.uuidGeneartionTask.isDone()) {
            ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
            if (Constants.DEBUG) {
                Constants.uuidGeneartionTask = exec.scheduleWithFixedDelay(
                        new UUIDGeneratorTask(cxt), 0, Constants.UUIDGenerationIntervalInSecondsDebug, TimeUnit.SECONDS);
            }
            else {
                Constants.uuidGeneartionTask = exec.scheduleWithFixedDelay(
                        new UUIDGeneratorTask(cxt), 0, Constants.UUIDGenerationIntervalInSeconds, TimeUnit.SECONDS);
            }
        }
    }

    public static void mkBeacon(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        boolean bleEnabled = AppPreferencesHelper.isBluetoothEnabled(context);
        Log.e("ble", "mkbeacon " + bleEnabled);
        Log.e("blebug", "mkbeacon contactUUID " + Constants.contactUUID);
        if (bleEnabled) {
            AdvertiseSettings settings = new AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                    .setConnectable(true)
                    .build();

            Log.e("ble", "contact uuid " + Constants.contactUUID);
            byte[] contactUUID = ByteUtils.uuid2bytes(Constants.contactUUID);
            Log.e("ble", "converted uuid to bytes");
            AdvertiseData advertiseData = new AdvertiseData.Builder()
                    .setIncludeDeviceName(false)
                    .addServiceUuid(new ParcelUuid(Constants.BEACON_SERVICE_UUID))
                    .addServiceData(new ParcelUuid(Constants.BEACON_SERVICE_UUID), contactUUID)
                    .build();
            BluetoothLeAdvertiser bluetoothLeAdvertiser = Constants.blueAdapter.getBluetoothLeAdvertiser();
            Log.e("ble", "start advertising");
            bluetoothLeAdvertiser.startAdvertising(settings, advertiseData, BluetoothUtils.advertiseCallback);
        }
    }

    public static boolean checkBluetoothSupport(Activity av) {
        BluetoothManager mBluetoothManager = (BluetoothManager) av.getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            return false;
        }

        if (!av.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }

        return true;
    }

    public static boolean isBluetoothOn() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static void turnOnBluetooth(Activity av) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        av.startActivityForResult(enableBtIntent, 0);
    }
}
