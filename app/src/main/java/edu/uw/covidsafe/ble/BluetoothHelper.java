package edu.uw.covidsafe.ble;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Messenger;
import android.os.ParcelUuid;
import android.util.Log;

import edu.uw.covidsafe.utils.ByteUtils;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BluetoothHelper implements Runnable {

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
    static Messenger messenger;

    public BluetoothHelper(Context cxt, Messenger messenger) {
        this.cxt = cxt;
        this.messenger = messenger;
    }

    @Override
    public void run() {
        Log.e("ble","mytask-run ");

        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);

        ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(Constants.serviceUUID))
                .build();
        List<ScanFilter> filters = new LinkedList<ScanFilter>();
        filters.add(filter);

        Log.e("ble","INIT SCAN "+(messenger==null));
        Constants.blueAdapter.getBluetoothLeScanner().startScan(filters, builder.build(), mLeScanCallback);
    }

    public static ScanCallback mLeScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, final ScanResult result) {
                    super.onScanResult(callbackType, result);
                    Map<ParcelUuid, byte[]> map = result.getScanRecord().getServiceData();
                    byte[] data = map.get(new ParcelUuid(Constants.serviceUUID));
                    Log.e("ble","onscanresult "+(data==null));
                    if (data != null && data.length == 16) {
                        String contactUuid = ByteUtils.byte2string(data);
//                        Log.e("uuid","CONTACT "+contactUuid);
                        if (!Constants.scannedUUIDs.contains(contactUuid)) {
                            String[] elts = contactUuid.split("-");
                            Utils.sendDataToUI(messenger, "ble", elts[elts.length - 1]);
                            Constants.scannedUUIDs.add(contactUuid);
                            Constants.scannedUUIDsRSSIs.put(contactUuid, result.getRssi());
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
