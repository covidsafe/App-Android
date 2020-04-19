package edu.uw.covidsafe.ui;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Switch;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.example.covidsafe.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import edu.uw.covidsafe.preferences.AppPreferencesHelper;
import edu.uw.covidsafe.ui.settings.PermissionsRecyclerViewAdapter;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

public class PermissionLogic {
    public static void permissionLogic(int requestCode, String[] permissions, int[] grantResults, final Activity av) {
        int androidSDKVersion = Build.VERSION.SDK_INT;

        Log.e("logme","on request permission "+requestCode);
        for (int i = 0; i < grantResults.length; i++) {
            Log.e("logme","grant results "+permissions[i]+","+grantResults[i]);
        }

        int backgroundResult = ActivityCompat.checkSelfPermission(av, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        int fineResult = ActivityCompat.checkSelfPermission(av, Manifest.permission.ACCESS_FINE_LOCATION);

        SharedPreferences.Editor editor = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();

        // for Q, the only permission is GPS
        // for below Q, the permission can be for BLE (1) or GPS (2)
        if (androidSDKVersion >= Build.VERSION_CODES.Q) {
            if (backgroundResult == PackageManager.PERMISSION_DENIED) {
                boolean shouldAsk;
                shouldAsk = ActivityCompat.shouldShowRequestPermissionRationale(av, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                if (shouldAsk) {
                    makeRationaleDialog(av, requestCode, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                } else if (!shouldAsk) {
                    // preemptively add it
                    Constants.SuppressSwitchStateCheck = true;
                    AppPreferencesHelper.setGPSEnabled(av);
                    Log.e("perm", "gps preemptive set " +true);
                    makeOpenSettingsDialog(av, Manifest.permission.ACCESS_BACKGROUND_LOCATION, requestCode);
                }
            }
            else if (backgroundResult == PackageManager.PERMISSION_GRANTED) {
                AppPreferencesHelper.setGPSEnabled(av);
                Log.e("perms","gps enabled");
            }
        } else if (androidSDKVersion < Build.VERSION_CODES.Q) {
            if (fineResult == PackageManager.PERMISSION_DENIED) {
                boolean shouldAsk;
                shouldAsk = ActivityCompat.shouldShowRequestPermissionRationale(av, Manifest.permission.ACCESS_FINE_LOCATION);
                if (shouldAsk) {
                    makeRationaleDialog(av, requestCode, Manifest.permission.ACCESS_FINE_LOCATION);
                } else if (!shouldAsk) {
                    // preemptively add it
                    if (requestCode == 1) {
                        AppPreferencesHelper.setBluetoothEnabled(av);
                        Log.e("perm", "ble preemptive set " +true);
                    }
                    else if (requestCode == 2) {
                        AppPreferencesHelper.setGPSEnabled(av);
                        Log.e("perm", "gps preemptive set " +true);
                    }
                    Constants.SuppressSwitchStateCheck = true;
                    makeOpenSettingsDialog(av, Manifest.permission.ACCESS_FINE_LOCATION, requestCode);
                }
            }
            else if (fineResult == PackageManager.PERMISSION_GRANTED) {
                if (requestCode == 2) {
                    AppPreferencesHelper.setGPSEnabled(av);
                }
                else if (requestCode == 1) {
                    AppPreferencesHelper.setBluetoothEnabled(av);
                    Log.e("perms","ble enabled");
                }
                editor.commit();
            }
        }
    }

    public static void makeOpenSettingsDialog(Activity av, String perm, int requestCode) {
        SharedPreferences prefs = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String msg;
        if (requestCode == 1) {
            msg = av.getString(R.string.perm_ble_ask);
        }
        else {
            msg = av.getString(R.string.perm_gps_ask);
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(av)
                .setTitle("Permission denied")
                .setMessage(msg)
                .setNegativeButton(R.string.no,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (requestCode == 2) {
                            if (Constants.gpsSwitch != null) {
//                                Constants.gpsSwitch.setOnCheckedChangeListener (null);
                                Constants.gpsSwitch.setChecked (false);
//                                Constants.gpsSwitch.setOnCheckedChangeListener (PermUtil.listener);
                                Log.e("perms","gps set false");
                                AppPreferencesHelper.setGPSEnabled(av, false);
                            }
                        }
                        if (requestCode == 1) {
                            if (Constants.bleSwitch != null) {
//                                Constants.bleSwitch.setOnCheckedChangeListener (null);
                                Constants.bleSwitch.setChecked (false);
//                                Constants.bleSwitch.setOnCheckedChangeListener (PermUtil.listener);
                                Log.e("perms","ble set false");
                                AppPreferencesHelper.setBluetoothEnabled(av, false);
                            }
                        }
                    }})
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", av.getPackageName(), null);
                        intent.setData(uri);
                        av.startActivity(intent);
                    }
                })
                .setCancelable(false).create();
        dialog.show();
    }

    public static void makeRationaleDialog(Activity av, int requestCode, String perm) {
        SharedPreferences prefs = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        AlertDialog dialog = new MaterialAlertDialogBuilder(av)
                .setTitle("Permission denied")
                .setMessage(av.getString(R.string.perm_ble_rationale))
                .setNegativeButton(R.string.retry, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(av, new String[]{perm}, 2);
                    }
                })
                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (requestCode == 2) {
                            if (Constants.gpsSwitch != null) {
//                                Constants.gpsSwitch.setOnCheckedChangeListener (null);
                                Constants.gpsSwitch.setChecked (false);
//                                Constants.gpsSwitch.setOnCheckedChangeListener (PermUtil.listener);
                                AppPreferencesHelper.setGPSEnabled(av, false);
                            }
                        }
                        if (requestCode == 1) {
                            if (Constants.bleSwitch != null) {
//                                Constants.bleSwitch.setOnCheckedChangeListener (null);
                                Constants.bleSwitch.setChecked (false);
//                                Constants.bleSwitch.setOnCheckedChangeListener (PermUtil.listener);
                                AppPreferencesHelper.setBluetoothEnabled(av, false);
                            }
                        }
                    }
                })
                .setCancelable(false).create();
        dialog.show();
    }
}
