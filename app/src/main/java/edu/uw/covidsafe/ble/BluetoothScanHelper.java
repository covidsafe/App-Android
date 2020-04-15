package edu.uw.covidsafe.ble;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

import edu.uw.covidsafe.utils.ByteUtils;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BluetoothScanHelper implements Runnable {

    public static AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.e("ble", "BLE advertisement added successfully "+settingsInEffect.toString());
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.e("ble", "Failed to add BLE advertisement, reason: " + errorCode);
        }
    };

    static Context cxt;

    public BluetoothScanHelper(Context cxt) {
        this.cxt = cxt;
    }

    @Override
    public void run() {
        Log.e("ble","mytask-run ");

        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);

        ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(Constants.BEACON_SERVICE_UUID))
                .build();
        List<ScanFilter> filters = new LinkedList<ScanFilter>();
        filters.add(filter);

        Constants.scannedUUIDs = new HashSet<String>();
        Constants.scannedUUIDsRSSIs = new HashMap<String,Integer>();

        Constants.blueAdapter.getBluetoothLeScanner().startScan(filters, builder.build(), mLeScanCallback);
    }

    public static ScanCallback mLeScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, final ScanResult result) {
                    super.onScanResult(callbackType, result);
                    Map<ParcelUuid, byte[]> map = result.getScanRecord().getServiceData();
                    byte[] data = map.get(new ParcelUuid(Constants.BEACON_SERVICE_UUID));
//                    Log.e("ble","onscanresult "+(data==null));
                    if (data != null && data.length == 16) {
                        String contactUuid = ByteUtils.byte2UUIDstring(data);
//                        Log.e("uuid","CONTACT "+contactUuid);
                        int rssi = result.getRssi();
                        if (Constants.scannedUUIDs != null &&
                            !Constants.scannedUUIDs.contains(contactUuid) &&
                            rssi >= Constants.rssiCutoff) {
//                            String[] elts = contactUuid.split("-");
                            Constants.scannedUUIDs.add(contactUuid);
                            Constants.scannedUUIDsRSSIs.put(contactUuid, rssi);
                        }
                    }
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    Log.e("ble", "error onscanfailed "+errorCode);
                }
            };
}
