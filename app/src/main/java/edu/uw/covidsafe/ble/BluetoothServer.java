package edu.uw.covidsafe.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import java.util.List;

public class BluetoothServer {

    public BluetoothGattCallback gattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {

                    }
                    else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                    }
                }
                @Override
                public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {

                    }
                }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt,
                                                 BluetoothGattCharacteristic characteristic,
                                                 int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {

                    }
                }

                @Override
                public void onDescriptorRead (BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    Log.e("ble", "on descriptor read " + descriptor.getUuid().toString());
                    if (status == BluetoothGatt.GATT_SUCCESS) {

                    }
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

                }

                @Override
                public void onCharacteristicWrite (BluetoothGatt gatt,
                                                   BluetoothGattCharacteristic characteristic,
                                                   int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {

                    }
                }

                @Override
                public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {

                    }
                }
            };

    private BluetoothGattCharacteristic getGattService(List<BluetoothGattService> gattServices) {
        Log.e("ble",gattServices.size()+ " services");
//        for (BluetoothGattService gattService : gattServices) {
//            Log.e("ble", "service: "+gattService.getUuid().toString());
//        }
        for (BluetoothGattService gattService : gattServices) {
            if (gattService.getUuid().equals("")) {
                Log.e("ble","service "+gattService.getUuid().toString());
                for (BluetoothGattCharacteristic gattCharacteristic : gattService.getCharacteristics()) {
                    if (gattCharacteristic.getProperties() == BluetoothGattCharacteristic.PROPERTY_READ) {
                        Log.e("ble", "\tcharacteristic: " + gattCharacteristic.getUuid().toString());
                        return gattCharacteristic;
                    }
                }
            }
        }
        return null;
    }
}
