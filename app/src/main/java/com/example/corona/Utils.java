package com.example.corona;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import java.util.List;

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

    public static void gpsLog(Context cxt, Location loc) {
        FileOperations.append(System.currentTimeMillis() + "," + loc.getLongitude() + "," + loc.getLongitude(),
                cxt, Constants.gpsDirName, Utils.getLogName());
    }

    public static boolean locationInBlacklist(Context cxt, Location loc) {
        if (Constants.blacklist == null) {
            Constants.blacklist = FileOperations.readBlacklist(cxt);
        }

        for (BlacklistRecord record : Constants.blacklist) {
            Location loc1 = new Location("");
            loc1.setLatitude(record.lat);
            loc1.setLongitude(record.longi);

            float distanceInMeters = loc1.distanceTo(loc);
            if (distanceInMeters < Constants.DistanceThresholdInMeters) {
                return true;
            }
        }
        return false;
    }

    public static String formatDate(String s) {
        s = s.substring(0,s.length()-4);
        String[] ss = s.split("-");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(s);
            SimpleDateFormat human = new SimpleDateFormat("E, dd MMMM yyyy");
            return human.format(date);
        }
        catch(Exception e) {
            Log.e("test",e.getMessage());
        }
        return "";
    }

    public static double[] address2gps(Context cxt, String addr) {
        Geocoder geocoder = new Geocoder(cxt);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(addr, 1);
            Log.e("logme","ADDRESS 2 GPS");
            double latitude = addresses.get(0).getLatitude();
            double longitude = addresses.get(0).getLongitude();
            return new double[] {latitude,longitude};
        }
        catch(Exception e) {
            Log.e("logme", e.getMessage());
        }
        return null;
    }

    public static String convertDate(String s) {
        SimpleDateFormat d1 = new SimpleDateFormat("E, dd MMMM yyyy");
        SimpleDateFormat d2 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return d2.format(d1.parse(s));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return "";
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
        DateFormat dateFormat = new SimpleDateFormat("hh:mm.ss aa");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getLogName() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date)+".txt";
    }

    public static String getFormRecordName() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static Date getLastSubmitTime(Context cxt) {
        String ss = FileOperations.readSubmitLog(cxt);
        if (ss.isEmpty()) {
            return null;
        }
        Log.e("logme","last line "+ss);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            return dateFormat.parse(ss);
        }
        catch(Exception e) {
            Log.e("logme",e.getMessage());
        }
        return null;
    }

    public static boolean compareDates(Date d1) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date d2 = new Date();

            int diff = Utils.daysBetween(d2, d1);
            Log.e("logme", "days betweeen " + diff);

            return diff >= Constants.SubmitThresh;
        }
        catch(Exception e) {
            Log.e("logme",e.getMessage());
        }
       return false;
    }

    public static int daysBetween(Date d1, Date d2) {
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }
}
