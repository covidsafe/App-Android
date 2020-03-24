package com.example.corona;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.corona.Constants;

public class BluetoothHelper implements Runnable {
    @Override
    public void run() {
        Log.e("ble","mytask-run ");
        Constants.blueAdapter.getBluetoothLeScanner().startScan(mLeScanCallback);
    }

    ScanCallback mLeScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, final ScanResult result) {
                    super.onScanResult(callbackType, result);
                    if (result.getDevice().getName()!=null) {
                        Log.e("ble","onscanresult "+result.getDevice().getName());
                        Constants.device = result.getDevice();
                        Log.e("ble", "got device " + Constants.device.getName().toString());
                        Log.e("ble", "got device address " + Constants.device.getAddress());
                        Constants.blueAdapter.getBluetoothLeScanner().stopScan(this);
                    }
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    Log.e("ble", "error onscanfailed "+errorCode);
                }
            };
}
