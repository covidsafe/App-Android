package edu.uw.covidsafe.ui.settings;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;

import com.example.covidsafe.R;

import edu.uw.covidsafe.ble.BluetoothUtils;
import edu.uw.covidsafe.gps.GpsUtils;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

public class PermUtils {
    public static void gpsSwitchLogic(Activity av) {
            SharedPreferences prefs = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();

        boolean hasGps = Utils.hasGpsPermissions(av);
        if (hasGps) {
            editor.putBoolean(av.getString(R.string.gps_enabled_pkey), true);
            editor.commit();
            if (!Constants.LoggingServiceRunning) {
                Utils.startLoggingService(av);
                GpsUtils.startGps(av);
            }
            else {
                GpsUtils.startGps(av);
            }
        }
        else {
            Log.e("state","REQUEST GPS PERMS");
            ActivityCompat.requestPermissions(av, Constants.gpsPermissions, 2);
        }
    }

    public static void bleSwitchLogic(Activity av) {
        SharedPreferences prefs = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();

        BluetoothManager bluetoothManager =
                (BluetoothManager) av.getSystemService(Context.BLUETOOTH_SERVICE);
        Constants.blueAdapter = bluetoothManager.getAdapter();

        boolean hasBle = Utils.hasBlePermissions(av);

        if (hasBle && BluetoothUtils.isBluetoothOn(av)) {
            editor.putBoolean(av.getString(R.string.ble_enabled_pkey), true);
            editor.commit();
            if (!Constants.LoggingServiceRunning) {
                Utils.startLoggingService(av);
                BluetoothUtils.startBle(av);
            }
            else {
                BluetoothUtils.startBle(av);
            }
        }
        else {
            if (Constants.blueAdapter != null && !Constants.blueAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                av.startActivityForResult(enableBtIntent, 0);
            }
            if (!Utils.hasBlePermissions(av)) {
                Log.e("results","no ble permission, let's request");
                ActivityCompat.requestPermissions(av, Utils.getBlePermissions(), 1);
            }
        }
    }
}
