package com.example.covidsafe.ui;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Messenger;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.example.covidsafe.R;
import com.example.covidsafe.BackgroundService;
import com.example.covidsafe.utils.Constants;
import com.example.covidsafe.utils.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class PermissionLogic {
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void permissionLogic(int requestCode, String[] permissions, int[] grantResults, final Activity av) {
        int androidSDKVersion = Build.VERSION.SDK_INT;

        Log.e("logme","on request permission "+requestCode);
        for (int i = 0; i < grantResults.length; i++) {
            Log.e("logme","grant results "+permissions[i]+","+grantResults[i]);
        }

        if (androidSDKVersion >= Build.VERSION_CODES.Q && ActivityCompat.checkSelfPermission(av, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_DENIED) {
            boolean shouldAsk;
            shouldAsk = ActivityCompat.shouldShowRequestPermissionRationale(av, Manifest.permission.ACCESS_BACKGROUND_LOCATION);

            Log.e("logme","does not have permissions for tracking "+shouldAsk);
            if (requestCode == 1 && shouldAsk) {
                AlertDialog dialog = new MaterialAlertDialogBuilder(av)
                        .setTitle("Permission denied")
                        .setMessage(av.getString(R.string.perm_ble_rationale))
                        .setNegativeButton(R.string.retry, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                                    ActivityCompat.requestPermissions(av, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 2);
                                }
                                else {
                                    ActivityCompat.requestPermissions(av, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 2);
                                }
                            }
                        })
                        .setPositiveButton(R.string.sure, null)
                        .setCancelable(false).create();
                dialog.show();
            }
            else if (!shouldAsk){
                AlertDialog dialog = new MaterialAlertDialogBuilder(av)
                        .setTitle("Permission denied")
                        .setMessage(av.getString(R.string.perm_ble_ask))
                        .setNegativeButton(R.string.no, null)
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
        }
        else if (androidSDKVersion < Build.VERSION_CODES.Q && ActivityCompat.checkSelfPermission(av, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            boolean shouldAsk;
            shouldAsk = ActivityCompat.shouldShowRequestPermissionRationale(av, Manifest.permission.ACCESS_FINE_LOCATION);

            if (requestCode == 1 && shouldAsk) {
                AlertDialog dialog = new MaterialAlertDialogBuilder(av)
                        .setTitle("Permission denied")
                        .setMessage(av.getString(R.string.perm_ble_rationale))
                        .setNegativeButton(R.string.retry, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(av, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                            }
                        })
                        .setPositiveButton(R.string.sure, null)
                        .setCancelable(false).create();
                dialog.show();
            }
            else if (!shouldAsk){
                AlertDialog dialog = new MaterialAlertDialogBuilder(av)
                        .setTitle("Permission denied")
                        .setMessage(av.getString(R.string.perm_ble_ask))
                        .setNegativeButton(R.string.no, null)
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
        }
//        else if (ActivityCompat.checkSelfPermission(av, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_DENIED) {
//            if (Constants.BLUETOOTH_ENABLED) {
//                boolean shouldAsk = ActivityCompat.shouldShowRequestPermissionRationale(av, Manifest.permission.BLUETOOTH_ADMIN);
//                Log.e("logme", "does not have permissions for bluetooth " + shouldAsk);
//                if (requestCode == 1 && shouldAsk) {
//                    AlertDialog dialog = new MaterialAlertDialogBuilder(av)
//                            .setTitle("Permission denied")
//                            .setMessage(av.getString(R.string.perm_ble_rationale))
//                            .setNegativeButton(R.string.retry, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    ActivityCompat.requestPermissions(av, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 2);
//                                }
//                            })
//                            .setPositiveButton(R.string.sure, null)
//                            .setCancelable(false).create();
//                    dialog.show();
//                } else if (!shouldAsk) {
//                    AlertDialog dialog = new MaterialAlertDialogBuilder(av)
//                            .setTitle("Permission denied")
//                            .setMessage(av.getString(R.string.perm_ble_ask))
//                            .setNegativeButton(R.string.no, null)
//                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                    Uri uri = Uri.fromParts("package", av.getPackageName(), null);
//                                    intent.setData(uri);
//                                    av.startActivity(intent);
//                                }
//                            })
//                            .setCancelable(false).create();
//                    dialog.show();
//                }
//            }
//        }
//        else if (ActivityCompat.checkSelfPermission(av, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_DENIED) {
//            if (Constants.BLUETOOTH_ENABLED) {
//                boolean shouldAsk = ActivityCompat.shouldShowRequestPermissionRationale(av, Manifest.permission.BLUETOOTH);
//                Log.e("logme", "does not have permissions for bluetooth " + shouldAsk);
//                if (requestCode == 1 && shouldAsk) {
//                    AlertDialog dialog = new MaterialAlertDialogBuilder(av)
//                            .setTitle("Permission denied")
//                            .setMessage(av.getString(R.string.perm_ble_rationale))
//                            .setNegativeButton(R.string.retry, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    ActivityCompat.requestPermissions(av, new String[]{Manifest.permission.BLUETOOTH}, 2);
//                                }
//                            })
//                            .setPositiveButton(R.string.sure, null)
//                            .setCancelable(false).create();
//                    dialog.show();
//                } else if (!shouldAsk) {
//                    AlertDialog dialog = new MaterialAlertDialogBuilder(av)
//                            .setTitle("Permission denied")
//                            .setMessage(av.getString(R.string.perm_ble_ask))
//                            .setNegativeButton(R.string.no, null)
//                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                    Uri uri = Uri.fromParts("package", av.getPackageName(), null);
//                                    intent.setData(uri);
//                                    av.startActivity(intent);
//                                }
//                            })
//                            .setCancelable(false).create();
//                    dialog.show();
//                }
//            }
//        }
        else {
            Log.e("logme","has permissions ");
            Log.e("logme","ble status "+(Constants.blueAdapter==null)+","+Constants.blueAdapter.isEnabled());
            if (Constants.BLUETOOTH_ENABLED &&
                (Constants.blueAdapter == null || !Constants.blueAdapter.isEnabled())) {
                Log.e("aa","BLE");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                av.startActivityForResult(enableBtIntent, 0);
            }
            else if (Constants.startingToTrack) {
                Log.e("logme","starting to track");
                Utils.startBackgroundService(av);
            }
        }
    }
}
