package com.example.corona;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class BluetoothHelper implements Runnable {

    static Context cxt;
    static Messenger messenger;

    public BluetoothHelper(Context cxt, Messenger messenger) {
        this.cxt = cxt;
        this.messenger = messenger;
    }

    @Override
    public void run() {
        Log.e("ble","mytask-run ");
        Constants.blueAdapter.getBluetoothLeScanner().startScan(mLeScanCallback);
    }

    static ScanCallback mLeScanCallback =
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
                        messenger.send(msg);
                    } catch (RemoteException e) {
                        Log.i("error", "error");
                    }

//                    if (result.getDevice().getName()!=null) {
//                        Log.e("ble","onscanresult "+result.getDevice().getName());
//                        Constants.device = result.getDevice();
//                        Log.e("ble", "got device " + Constants.device.getName().toString());
//                        Log.e("ble", "got device address " + Constants.device.getAddress());
//                        Constants.blueAdapter.getBluetoothLeScanner().stopScan(this);
//                    }
                    Utils.bleLog(cxt, result);
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    Log.e("ble", "error onscanfailed "+errorCode);
                }
            };
}
