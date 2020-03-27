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

    public UUIDGeneratorTask(Messenger messenger) {
        this.messenger = messenger;
    }

    @Override
    public void run() {
        Log.e("ble","gneerate uuid");
        Constants.contactUUID = UUID.randomUUID();
        Utils.sendDataToUI(messenger, "uuid",Constants.contactUUID.toString());

        if (Constants.blueAdapter != null && Constants.blueAdapter.getBluetoothLeAdvertiser() != null) {
            Constants.blueAdapter.getBluetoothLeAdvertiser().stopAdvertising(BluetoothHelper.advertiseCallback);
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
            Constants.blueAdapter.getBluetoothLeAdvertiser().startAdvertising(settings, advertiseData, BluetoothHelper.advertiseCallback);
        }
    }
}
