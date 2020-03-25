package com.example.covidsafe.ble;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.example.covidsafe.utils.Constants;
import com.example.covidsafe.utils.Utils;

import java.util.LinkedList;
import java.util.List;

public class BluetoothHelper implements Runnable {

    public static AdvertiseCallback callback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.d("ble", "BLE advertisement added successfully");
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
//        builder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
//        builder.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
//        builder.setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT);
//        builder.setReportDelay(1000);

        List<ScanFilter> filters = new LinkedList<ScanFilter>();

        Constants.blueAdapter.getBluetoothLeScanner().startScan(filters, builder.build(), mLeScanCallback);
    }

    public static ScanCallback mLeScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, final ScanResult result) {
                    super.onScanResult(callbackType, result);
//                    Log.e("ble",result+"");

                    Bundle bb = new Bundle();
                    bb.putString("ble", result.getDevice().getAddress()+","+result.getRssi());
                    Message msg = new Message();
                    msg.setData(bb);
                    try {
//                        Log.e("test","sending");
                        Log.e("ex","messenger is "+(messenger==null));
                        Log.e("ex","msg is "+(msg==null));
                        messenger.send(msg);
                    } catch (RemoteException e) {
                        Log.i("error", "error");
                    }

                    if (result.getDevice().getName()!=null) {
//                        Log.e("ble","onscanresult "+result.getDevice().getName());
//                        Constants.device = result.getDevice();
//                        Log.e("ble", "got device " + Constants.device.getName().toString());
//                        Log.e("ble", "got device address " + Constants.device.getAddress());
//                        Constants.blueAdapter.getBluetoothLeScanner().stopScan(this);
                        Utils.bleLogToDatabase(cxt,result);
                    }
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    Log.e("ble", "error onscanfailed "+errorCode);
                }
            };
}
