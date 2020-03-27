package com.example.covidsafe.ble;

import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.os.Messenger;
import android.os.ParcelUuid;
import android.util.Log;

import com.example.covidsafe.utils.ByteUtils;
import com.example.covidsafe.utils.Constants;
import com.example.covidsafe.utils.Utils;

import java.util.UUID;

public class UUIDGeneratorTask implements Runnable {

    Messenger messenger;
    Context context;

    public UUIDGeneratorTask(Messenger messenger, Context context) {
        this.messenger = messenger;
        this.context = context;
    }

    @Override
    public void run() {
        Log.e("ble","generate uuid");
        Constants.contactUUID = UUID.randomUUID();
        Utils.sendDataToUI(messenger, "uuid",Constants.contactUUID.toString());
        Utils.uuidLogToDatabase(context);

        if (Constants.blueAdapter != null && Constants.blueAdapter.getBluetoothLeAdvertiser() != null) {
            Constants.blueAdapter.getBluetoothLeAdvertiser().stopAdvertising(BluetoothHelper.advertiseCallback);
            BluetoothUtils.mkBeacon();
        }
    }
}
