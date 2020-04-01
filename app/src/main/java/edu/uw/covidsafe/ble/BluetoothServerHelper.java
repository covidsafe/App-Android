package edu.uw.covidsafe.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Messenger;
import android.util.Log;

import java.util.List;

import edu.uw.covidsafe.utils.ByteUtils;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

public class BluetoothServerHelper {

    static Context cxt;
    static Messenger messenger;

    public static void createServer(Context cxt, Messenger messenger) {
        BluetoothServerHelper.cxt = cxt;
        BluetoothServerHelper.messenger = messenger;
        Log.e("bleserver","createserver");
        BluetoothManager bluetoothManager =
                (BluetoothManager) cxt.getSystemService(Context.BLUETOOTH_SERVICE);
        Constants.gattServer = bluetoothManager.openGattServer(cxt, gattServerCallback);
        Log.e("bleserver","is server null "+(Constants.gattServer==null));
        if (Constants.gattServer != null) {
            BluetoothGattService service = new BluetoothGattService(Constants.GATT_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);

            BluetoothGattCharacteristic charac1 = new BluetoothGattCharacteristic(Constants.BROADCAST_CHARACTERISTIC_UUID,
                    BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ);

            BluetoothGattCharacteristic charac2 = new BluetoothGattCharacteristic(Constants.RECEIVER_CHARACTERISTIC_UUID,
                    BluetoothGattCharacteristic.PROPERTY_WRITE, BluetoothGattCharacteristic.PERMISSION_WRITE);

            service.addCharacteristic(charac1);
            service.addCharacteristic(charac2);
            Constants.gattServer.addService(service);
        }
    }

    public static void stopServer() {
        if (Constants.gattServer != null) {
            Constants.gattServer.close();
        }
    }

    public static BluetoothGattServerCallback gattServerCallback =
        new BluetoothGattServerCallback() {
            @Override
            public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
                Log.e("bleserver","read request "+characteristic.getUuid().toString());
                if (characteristic.getUuid().equals(Constants.BROADCAST_CHARACTERISTIC_UUID)) {
                    Constants.gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0,
                            ByteUtils.uuid2bytes(Constants.contactUUID));
                }
            }

            @Override
            public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
                Log.e("bleserver","write request "+characteristic.getUuid().toString());

                if (value != null) {
                    if (value.length == 16) {
                        String contactUuid = ByteUtils.byte2string(value);
                        if (!Constants.writtenUUIDs.contains(contactUuid)) {
                            String[] elts = contactUuid.split("-");
                            Utils.sendDataToUI(messenger, "ble", elts[elts.length - 1]);
                            Constants.writtenUUIDs.add(contactUuid);
                            int rssi = 0;
                            Utils.bleLogToDatabase(cxt, contactUuid, rssi);
                        }
                    }
                    else {
                        for(Byte b : value) {
                            Log.e("bleserver",b+"");
                        }
                    }
                }
            }

            @Override
            public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
                super.onConnectionStateChange(device, status, newState);
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.e("bleserver","connected");
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.e("bleserver","disconnected");
                }
            }

            @Override
            public void onServiceAdded(int status, BluetoothGattService service) {
                super.onServiceAdded(status, service);
                Log.e("bleserver","service added "+service.getUuid());
                List<BluetoothGattCharacteristic> characs=service.getCharacteristics();
                for (BluetoothGattCharacteristic charac:characs){
                    Log.e("bleserver","charac "+charac.getUuid());
                    byte[] bb = charac.getValue();
                    for(Byte b : bb) {
                        Log.e("bleserver",b+"");
                    }
                }
            }
        };
}
