package com.example.covidsafe.utils;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.example.covidsafe.BackgroundService;
import com.example.covidsafe.R;
import com.example.covidsafe.ble.BleOpsAsyncTask;
import com.example.covidsafe.ble.BleRecord;
import com.example.covidsafe.gps.GpsOpsAsyncTask;
import com.example.covidsafe.gps.GpsRecord;
import com.example.covidsafe.uuid.UUIDOpsAsyncTask;
import com.example.covidsafe.uuid.UUIDRecord;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import unused.BlacklistRecord;

public class Utils {

    public static int gpsLines = 0;
    public static int bleLines = 0;
    public static TextView gpsResults;
    public static TextView bleResults;
    public static TextView bleBeaconId;

    public static Handler serviceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle reply = msg.getData();
            String out1 = reply.getString("gps");
            String out2 = reply.getString("ble");
            String out3 = reply.getString("uuid");
            if (out1!=null) {
                if (gpsLines > 20) {
                    String ss = gpsResults.getText().toString();
                    int ii = ss.indexOf("\n");
                    String oo = ss.substring(ii+1,ss.length()) + out1+"\n";
                    gpsResults.setText(oo);
                }
                else {
                    gpsResults.append(out1 + "\n");
                }
                gpsLines+=1;
            }
            if (out2!=null) {
                if (bleLines > 20) {
                    String ss = bleResults.getText().toString();
                    int ii = ss.indexOf("\n");
                    String oo = ss.substring(ii+1,ss.length()) + out2+"\n";
                    bleResults.setText(oo);
                }
                else {
                    bleResults.append(out2 + "\n");
                }
                bleLines+=1;
            }
            if (out3 != null) {
                bleBeaconId.setText(out3);
            }
        }
    };

    public static String randomGUID() {
        return UUID.randomUUID().toString();
    }

    public static void mkSnack(Activity av, View v, String msg) {
        final Snackbar snackBar = Snackbar.make(v, msg, Snackbar.LENGTH_LONG);

        snackBar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        });

        View snackbarView = snackBar.getView();
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setMaxLines(5);

        snackBar.show();
    }

    public static void gpsLogToFile(Context cxt, GpsRecord rec) {
        FileOperations.append(rec.toString(),
                cxt, Constants.gpsDirName, Utils.getGpsLogName());
    }

    public static void gpsLogToDatabase(Context cxt, Location loc) {
        new GpsOpsAsyncTask(cxt, loc, System.currentTimeMillis()).execute();
    }

    public static void bleLogToFile(Context cxt, BleRecord rec) {
        FileOperations.append(rec.toString(),
                cxt, Constants.bleDirName, Utils.getBleLogName());
    }

    public static void bleLogToDatabase(Context cxt, String id) {
        new BleOpsAsyncTask(cxt, id).execute();
    }

    public static void uuidLogToDatabase(Context cxt) {
        new UUIDOpsAsyncTask(cxt, Constants.contactUUID).execute();
    }

    public static void uuidLogToFile(Context cxt, UUIDRecord rec) {
        FileOperations.append(rec.toString(),
                cxt, Constants.uuidDirName, Utils.getUuidLogName());
    }

    public static JsonObject gps2json(List<GpsRecord> records) {
        JsonArray arr = new JsonArray();
        for (GpsRecord rec : records) {
            arr.add(rec.toJson());
        }
        JsonObject obj = new JsonObject();
        obj.add("data", arr);
        return obj;
    }

    public static JsonObject ble2json(List<BleRecord> records) {
        JsonArray arr = new JsonArray();
        for (BleRecord rec : records) {
            arr.add(rec.toJson());
        }
        JsonObject obj = new JsonObject();
        obj.add("data", arr);
        return obj;
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

    public static void startBackgroundService(Activity av) {
        Utils.bleResults.setText("");
        Utils.gpsResults.setText("");

        Utils.createNotificationChannel(av);

        Intent intent = new Intent(av, BackgroundService.class);
        intent.putExtra("messenger", new Messenger(Utils.serviceHandler));
        av.startService(intent);

        Constants.tracking = true;
        Constants.startingToTrack = false;

        Button trackButton = (Button)av.findViewById(R.id.trackButton);
        trackButton.setText("Stop tracking");
        trackButton.setBackgroundResource(R.drawable.stopbutton);
    }

    public static boolean permCheck(Activity av) {
        return gpsCheck(av) && bleCheck(av);
    }

    public static boolean gpsCheck(Activity av) {
        boolean hasPerms = Utils.hasGpsPermissions(av);
        if ((hasPerms && Constants.GPS_ENABLED) ||
            (!Constants.GPS_ENABLED && (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || hasPerms))) {
            return true;
        }
        return false;
    }

    public static boolean bleCheck(Activity av) {
        boolean hasPerms = Utils.hasBlePermissions(av);
        if (hasPerms &&
                (Constants.BLUETOOTH_ENABLED && Constants.blueAdapter != null && Constants.blueAdapter.isEnabled()) ||
                (!Constants.BLUETOOTH_ENABLED)) {
            return true;
        }
        return false;
    }

    public static void sendDataToUI(Messenger messenger, String tag, String log) {
        Bundle bb = new Bundle();
        bb.putString(tag, log);
        Message msg = new Message();
        msg.setData(bb);
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            Log.i("error", "error");
        }
    }

    public static boolean hasBlePermissions(Context context) {
        Log.e("results", "check for permission");
        if (context != null) {
            if (Constants.BLUETOOTH_ENABLED && Constants.blePermissions != null) {
                for (String permission : Constants.blePermissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        Log.e("results", "return false on " + permission);
                        return false;
                    }
                }
            }
            if (Constants.miscPermissions != null) {
                for (String permission : Constants.miscPermissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        Log.e("results", "return false on " + permission);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean hasGpsPermissions(Context context) {
        Log.e("results", "check for permission");
        if (context != null) {
            if ((Constants.GPS_ENABLED  || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && Constants.gpsPermissions != null) {
                for (String permission : Constants.gpsPermissions) {
                    int result = ActivityCompat.checkSelfPermission(context, permission);
                    Log.e("logme","perm "+result);
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Log.e("results", "return false on " + permission);
                        return false;
                    }
                }
            }
            if (Constants.miscPermissions != null) {
                for (String permission : Constants.miscPermissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        Log.e("results", "return false on " + permission);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static String time() {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm.ss aa");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static void createNotificationChannel(Context cxt) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL,
                    "Example",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = cxt.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);

        }
    }

    public static String getGpsLogName() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date)+".txt";
    }

    public static String getBleLogName() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date)+".txt";
    }

    public static String getUuidLogName() {
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

    public static int compareDates(Date d1, Date d2) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            int diff = Utils.daysBetween(d2, d1);
            Log.e("logme", "days betweeen " + diff);

            return diff;
        }
        catch(Exception e) {
            Log.e("logme",e.getMessage());
        }
        return -1;
    }

    public static int daysBetween(Date d1, Date d2) {
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    public static void clearPreferences(Context cxt) {
        SharedPreferences.Editor sharedPref = cxt.getSharedPreferences(
                Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();
        sharedPref.remove(Constants.lastSentName);
        sharedPref.commit();
    }

    public static void writeLastSentLog(Context cxt, long ts) {
        SharedPreferences.Editor sharedPref = cxt.getSharedPreferences(
                Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();
        sharedPref.putString(Constants.lastSentName, CryptoUtils.encryptTimestamp(cxt, ts));
        sharedPref.commit();
    }

    public static long readLastSentLog(Context cxt) {
        SharedPreferences sharedPref = cxt.getSharedPreferences(
                Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        return CryptoUtils.decryptTimestamp(cxt, sharedPref.getString(Constants.lastSentName, ""));
    }
}
