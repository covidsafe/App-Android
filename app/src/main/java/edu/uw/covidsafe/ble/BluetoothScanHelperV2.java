package edu.uw.covidsafe.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.ByteUtils;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.TimeUtils;

public class BluetoothScanHelperV2 implements Runnable {

    static Context cxt;

    public BluetoothScanHelperV2(Context cxt) {
        this.cxt = cxt;
    }

    @Override
    public void run() {
        Log.e("blebug","bluetooth scan helper");

        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);

        ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(Constants.BEACON_SERVICE_UUID))
                .build();
        List<ScanFilter> filters = new LinkedList<ScanFilter>();
        filters.add(filter);

        Constants.scannedUUIDs = new HashSet<String>();
        Constants.scannedUUIDsRSSIs = new HashMap<String,Integer>();
        Constants.scannedUUIDsTimes = new HashMap<String,Long>();

        BluetoothManager bluetoothManager =
                (BluetoothManager) cxt.getSystemService(Context.BLUETOOTH_SERVICE);
        if (Constants.blueAdapter != null && bluetoothManager != null && BluetoothUtils.isBluetoothOn() &&
                Constants.blueAdapter.getBluetoothLeScanner() != null) {
            Constants.blueAdapter.getBluetoothLeScanner().startScan(filters, builder.build(), mLeScanCallback);
        }

        try {
            Thread.sleep(Constants.BluetoothScanPeriodInSeconds*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e("blebug", "STOPPED SCANNING");
        BluetoothUtils.finishScan(cxt);
    }

    static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm.ss");
    public static ScanCallback mLeScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, final ScanResult result) {
                    super.onScanResult(callbackType, result);
                    work(result.getDevice(), result.getRssi());
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    Log.e("ble", "error onscanfailed "+errorCode);
                }
            };

    static HashMap<String, Integer> devices = new HashMap<>();

    public static void work(BluetoothDevice device, int rssi) {
        if (!Constants.bleDeviceBlacklist.contains(device.getAddress()) &&
                BluetoothUtils.rssiThresholdCheck(rssi, Constants.deviceID)) {
            Constants.bleDeviceBlacklist.add(device.getAddress());
            devices.put(device.getAddress(), rssi);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                device.connectGatt(cxt, false, gattCallback, BluetoothDevice.TRANSPORT_LE);
            }
            else {
                device.connectGatt(cxt, false, gattCallback);
            }
        }
    }

    public static BluetoothGattCallback gattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState) {
                    Log.e("ble","onconnectionstatechange");
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.e("ble","connected");
                        boolean disc = gatt.discoverServices();
                    }
                }

                @Override
                public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        Log.e("ble","services discovered");
                        List<BluetoothGattService>services=gatt.getServices();
                        for(BluetoothGattService service:services) {
                            Log.e("ble",service.getUuid().toString());
                            if (service.getUuid().equals(Constants.GATT_SERVICE_UUID)) {
                                List<BluetoothGattCharacteristic> characs = service.getCharacteristics();
                                for(BluetoothGattCharacteristic charac:characs) {
                                    if (charac.getUuid().equals(Constants.CHARACTERISTIC_UUID)) {
                                        Log.e("ble","going to write char "+Constants.contactUUID.toString());
                                        int rssi = devices.get(gatt.getDevice().getAddress());
                                        byte[] data = ByteUtils.uuid2bytes(Constants.contactUUID,
                                                (byte)rssi,
                                                (byte)Constants.deviceID);
                                        charac.setValue(data);
                                        boolean b = gatt.writeCharacteristic(charac);
                                    }
                                }
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic charac, int status) {
                    super.onCharacteristicRead(gatt, charac, status);
                    Log.e("ble","char read "+charac.getUuid().toString());
                    if (charac.getUuid().equals(Constants.CHARACTERISTIC_UUID)) {
                        byte[] bb = charac.getValue();
                        if (bb != null) {
                            final String contactUUID = ByteUtils.byte2UUIDstring(bb);
                            Log.e("ble", "** read this contact ID: " + contactUUID);
                        } else {
                            Log.e("ble", "** bb is null");
                        }
                    }
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic charac, int status) {
                    super.onCharacteristicWrite(gatt, charac, status);
                    Log.e("ble","char write "+charac.getUuid().toString());
                    if (charac.getUuid().equals(Constants.CHARACTERISTIC_UUID)) {
                        Log.e("ble", "** setting value "+status);
                    }
                    devices.remove(gatt.getDevice().getAddress());
                    gatt.disconnect();
                }
            };
}
