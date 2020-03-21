package com.example.corona;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Utils {

    public static void mkSnack(Activity av, View v, String msg) {
        final Snackbar snackBar = Snackbar.make(v, msg, Snackbar.LENGTH_LONG);

        snackBar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        });

        snackBar.show();
    }

    public static boolean hasPermissions(Context context) {
        Log.e("results", "check for permission");
        if (context != null && Constants.permissions != null) {
            for (String permission : Constants.permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("results", "return false on " + permission);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Display the error message
     */
    public static void displayError(@NonNull final Exception exception) {
        Log.e("err",exception.toString());
    }

    public static String time() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm.ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getLogName() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date)+".txt";
    }
}
