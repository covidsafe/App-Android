package edu.uw.covidsafe.ble;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import edu.uw.covidsafe.utils.ByteUtils;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.TimeUtils;
import edu.uw.covidsafe.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BluetoothScanHelper implements Runnable {

    static Context cxt;

    public BluetoothScanHelper(Context cxt) {
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

        Constants.blueAdapter.getBluetoothLeScanner().startScan(filters, builder.build(), mLeScanCallback);

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
                    Map<ParcelUuid, byte[]> map = result.getScanRecord().getServiceData();
                    byte[] data = map.get(new ParcelUuid(Constants.BEACON_SERVICE_UUID));
//                    Log.e("ble","onscanresult "+(data==null));
                    if (data != null && data.length == 16) {
                        String contactUuid = ByteUtils.byte2UUIDstring(data);
//                        Log.e("uuid","CONTACT "+contactUuid);
                        int rssi = result.getRssi();
                        if (Constants.scannedUUIDs != null &&
                            rssi >= Constants.rssiCutoff) {
                            if (!Constants.scannedUUIDs.contains(contactUuid)) {
                                Log.e("blebug", "found contact uuid " + contactUuid+","+format.format(new Date(TimeUtils.getTime())));
//                            String[] elts = contactUuid.split("-");
                                Constants.scannedUUIDs.add(contactUuid);
                                Constants.scannedUUIDsRSSIs.put(contactUuid, rssi);
                                Constants.scannedUUIDsTimes.put(contactUuid, TimeUtils.getTime());
                            }
                            else {
//                                Log.e("bledebug", "already saw uuid " + contactUuid);
                            }
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
