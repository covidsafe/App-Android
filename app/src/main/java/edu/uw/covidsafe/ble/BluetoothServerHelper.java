package edu.uw.covidsafe.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import edu.uw.covidsafe.utils.ByteUtils;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.TimeUtils;
import edu.uw.covidsafe.utils.Utils;

public class BluetoothServerHelper {

    static Context cxt;

    public static void createServer(Context cxt) {
        BluetoothServerHelper.cxt = cxt;
        Constants.writtenUUIDs = new HashSet<>();
        Log.e("bleserver","createserver");
        BluetoothManager bluetoothManager =
                (BluetoothManager) cxt.getSystemService(Context.BLUETOOTH_SERVICE);
        if (Constants.gattServer == null) {
            Constants.gattServer = bluetoothManager.openGattServer(cxt, gattServerCallback);
            Log.e("bleserver", "is server null " + (Constants.gattServer == null));
            if (Constants.gattServer != null) {
                BluetoothGattService service = new BluetoothGattService(Constants.GATT_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);

                int permission = BluetoothGattCharacteristic.PERMISSION_WRITE | BluetoothGattCharacteristic.PERMISSION_READ;
                int property = BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_READ;

                BluetoothGattCharacteristic charac1 = new BluetoothGattCharacteristic(Constants.CHARACTERISTIC_UUID,
                        property, permission);

                service.addCharacteristic(charac1);
                Constants.gattServer.addService(service);
            }
        }
    }

    public static void stopServer() {
        if (Constants.gattServer != null) {
            Constants.gattServer.close();
            Constants.gattServer = null;
        }
    }

    public static BluetoothGattServerCallback gattServerCallback =
        new BluetoothGattServerCallback() {
            @Override
            public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
                Log.e("bleserver","read request "+characteristic.getUuid().toString());
                if (characteristic.getUuid().equals(Constants.CHARACTERISTIC_UUID)) {
                    Log.e("bleserver","going to send "+Constants.contactUUID);
                    byte[] contactUuidBytes = ByteUtils.uuid2bytes(Constants.contactUUID);
                    Log.e("bleserver","converted to bytes "+contactUuidBytes.length);
                    boolean status = Constants.gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0,
                            contactUuidBytes);
                    Log.e("bleserver","status "+status);
                }
                Log.e("bleserver","finished read");
            }

            @Override
            public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
                Log.e("bleserver","write request "+characteristic.getUuid().toString());
                Log.e("bleserver","preparedwrite "+preparedWrite);
                Log.e("bleserver","responseNeeded "+responseNeeded);

                if (responseNeeded) {
                    Constants.gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null);
                }

                if (value != null) {
                    Log.e("bleserver","data len "+value.length);
                    if (value.length == 16 || value.length == 17) {
                        byte[] uuidByte = Arrays.copyOfRange(value,0,16);
                        String contactUuid = ByteUtils.byte2UUIDstring(uuidByte);
                        Log.e("bleserver","contactuuid "+contactUuid);
                        int rssi = 0;
                        String[] elts = contactUuid.split("-");

                        // byte[-128,127] => int[0,255] => rssi[-255,0]
                        if (value.length == 17) {
                            rssi = -Utils.byteConvert(value[16]);
                            Log.e("bleserver","received an rssi value of "+rssi);
                        }
                        else {
                            Log.e("bleserver","rssi value not received");
                        }
                        Log.e("bleserver","rssi "+rssi+","+device.getAddress());

                        if (!Constants.writtenUUIDs.contains(contactUuid) &&
                            rssi > Constants.rssiCutoff) {
                            Constants.writtenUUIDs.add(contactUuid);
                            Utils.bleLogToDatabase(cxt, contactUuid, rssi, TimeUtils.getTime());
                        }
                    }
                    else {
                        for(Byte b : value) {
                            Log.e("bleserver",b+"");
                        }
                    }
                }
                else {
                    Log.e("bleserver","write data is null");
                }
                Log.e("bleserver","finished write");
            }

            @Override
            public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
                super.onConnectionStateChange(device, status, newState);
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.e("bleserver","connected "+device.getAddress());
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.e("bleserver","disconnected");
                }
            }

            @Override
            public void onServiceAdded(int status, BluetoothGattService service) {
                super.onServiceAdded(status, service);
                if (service != null) {
                    Log.e("bleserver", "service added " + service.getUuid());
                    List<BluetoothGattCharacteristic> characs = service.getCharacteristics();
                    if (characs != null) {
                        for (BluetoothGattCharacteristic charac : characs) {
                            Log.e("bleserver", "charac " + charac.getUuid());
                            if (charac != null) {
                                byte[] bb = charac.getValue();
                                if (bb != null) {
                                    for (Byte b : bb) {
                                        Log.e("bleserver", b + "");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        };
}
