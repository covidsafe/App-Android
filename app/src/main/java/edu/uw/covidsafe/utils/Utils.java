package edu.uw.covidsafe.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import edu.uw.covidsafe.LoggingService;
import com.example.covidsafe.R;

import edu.uw.covidsafe.PullService;
import edu.uw.covidsafe.ble.BleOpsAsyncTask;
import edu.uw.covidsafe.ble.BleRecord;
import edu.uw.covidsafe.ble.BluetoothScanHelper;
import edu.uw.covidsafe.ble.BluetoothServerHelper;
import edu.uw.covidsafe.ble.BluetoothUtils;
import edu.uw.covidsafe.gps.GpsOpsAsyncTask;
import edu.uw.covidsafe.gps.GpsRecord;
import edu.uw.covidsafe.gps.GpsUtils;
import edu.uw.covidsafe.symptoms.SymptomsRecord;
import edu.uw.covidsafe.seed_uuid.SeedUUIDRecord;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.ui.notif.NotifRecord;

public class Utils {

    public static int gpsLines = 0;
    public static int bleLines = 0;
    public static TextView gpsResults;
    public static TextView bleResults;
    public static TextView bleBeaconId;

    public static void haltLoggingService(Activity av, View view) {
        if (Constants.LoggingServiceRunning && view != null) {
            Utils.mkSnack(av, view, "Logging is now turned off.");
        }

        Constants.LoggingServiceRunning = false;
        Log.e("logme", "stop service");
        av.stopService(new Intent(av, LoggingService.class));

        GpsUtils.haltGps();

        BluetoothUtils.haltBle(av);

        SharedPreferences prefs = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(av.getString(R.string.gps_enabled_pkey), false);
        editor.putBoolean(av.getString(R.string.ble_enabled_pkey), false);
        editor.commit();
    }

    public static void updateSwitchStates(Activity av) {
        Log.e("state","update switch states");
        SharedPreferences prefs = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (Constants.notifSwitch != null) {
            boolean hasNotifPerms = NotificationManagerCompat.from(av).areNotificationsEnabled();
//            boolean perm = prefs.getBoolean(av.getString(R.string.notifs_enabled_pkey), Constants.NOTIFS_ENABLED);
//            Log.e("perm","notif get is "+perm);
//            Constants.notifSwitch.setChecked(perm);
            if (!hasNotifPerms) {
//                Constants.notifSwitch.setOnCheckedChangeListener (null);
                Constants.notifSwitch.setChecked (false);
//                Constants.notifSwitch.setOnCheckedChangeListener (PermUtil.listener);

                editor.putBoolean(av.getString(R.string.notifs_enabled_pkey), false);
                editor.commit();
            }
        }
        if (Constants.gpsSwitch != null) {
            boolean hasGpsPerms = Utils.hasGpsPermissions(av);
            Log.e("perm","gps get "+hasGpsPerms);
//            editor.putBoolean(av.getString(R.string.gps_enabled_pkey),hasGpsPerms);
            if (!hasGpsPerms) {
//                Constants.gpsSwitch.setOnCheckedChangeListener (null);
                Constants.gpsSwitch.setChecked (false);
//                Constants.gpsSwitch.setOnCheckedChangeListener (PermUtil.listener);

                editor.putBoolean(av.getString(R.string.gps_enabled_pkey), false);
                editor.commit();
            }
        }
        if (Constants.bleSwitch != null) {
            boolean hasBlePerms = Utils.hasBlePermissions(av);
            Log.e("perm","ble get "+hasBlePerms);
//            editor.putBoolean(av.getString(R.string.ble_enabled_pkey),hasBlePerms);
            if (!hasBlePerms) {
//                Constants.bleSwitch.setOnCheckedChangeListener (null);
                Constants.bleSwitch.setChecked (false);
//                Constants.bleSwitch.setOnCheckedChangeListener (PermUtil.listener);

                editor.putBoolean(av.getString(R.string.ble_enabled_pkey), false);
                editor.commit();
            }
        }
    }

    public static void markDiagnosisSubmitted(Activity av) {
        SharedPreferences.Editor editor = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putLong(av.getString(R.string.diagnosis_pkey), System.currentTimeMillis());
        editor.commit();
    }

    public static boolean isDiagnosisSubmitted(Activity av) {
        SharedPreferences prefs = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        if (prefs.getLong(av.getString(R.string.diagnosis_pkey), 0L) == 0) {
            return false;
        }
        return true;
    }

    public static void sendNotification(Context mContext, String title, String message) {
        SharedPreferences prefs = mContext.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mContext.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();
        if (prefs.getBoolean(mContext.getString(R.string.notifs_enabled_pkey), Constants.NOTIFS_ENABLED)) {
            Log.e("notif","notif");
            NotificationManager mNotificationManager;

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(mContext.getApplicationContext(), Constants.NOTIFICATION_CHANNEL);
            Intent ii = new Intent(mContext.getApplicationContext(), MainActivity.class);
            ii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, ii, 0);

            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
            bigText.bigText(message);
            bigText.setBigContentTitle(title);
//            bigText.setSummaryText(message);

            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setSmallIcon(R.drawable.warning2);
            mBuilder.setContentTitle(title);
            mBuilder.setContentText(message);
            mBuilder.setPriority(Notification.PRIORITY_MAX);
            mBuilder.setStyle(bigText);
//        mBuilder.setBadgeIconType(R.drawable.logo2);

            mNotificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = "Your_channel_id";
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_HIGH);
                mNotificationManager.createNotificationChannel(channel);
                channel.setShowBadge(true);
                mBuilder.setChannelId(channelId);
            }

            int notifID = prefs.getInt(mContext.getString(R.string.notif_id_pkey),0);
            mNotificationManager.notify(notifID, mBuilder.build());
            editor.putInt(mContext.getString(R.string.notif_id_pkey),notifID+1);
            editor.commit();
        }
    }

    public static void mkSnack(Activity av, View v, String msg) {
        av.runOnUiThread(new Runnable() {
            public void run() {
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
        }});
    }

    public static void gpsLogToFile(Context cxt, GpsRecord rec) {
        FileOperations.append(rec.toString(),
                cxt, Constants.gpsDirName, Utils.getGpsLogName());
    }

    public static void notifLogToFile(Context cxt, NotifRecord rec) {
        FileOperations.append(rec.toString(),
                cxt, Constants.notifDirName, Utils.getNotifLogName());
    }

    public static void gpsLogToDatabase(Context cxt, Location loc) {
        new GpsOpsAsyncTask(cxt, loc, System.currentTimeMillis()).execute();
    }

    public static void bleLogToFile(Context cxt, BleRecord rec) {
        FileOperations.append(rec.toString(),
                cxt, Constants.bleDirName, Utils.getBleLogName());
    }

    public static void symptomsLogToFile(Context cxt, SymptomsRecord rec) {
        FileOperations.append(rec.toString(),
                cxt, Constants.symptomsDirName, Utils.getSymptomsLogName());
    }

    public static void bleLogToDatabase(Context cxt, String id, int rssi) {
        new BleOpsAsyncTask(cxt, id, rssi).execute();
    }

    public static void uuidLogToFile(Context cxt, SeedUUIDRecord rec) {
        Log.e("uuid","uuid log to file");
        FileOperations.append(rec.toString(),
                cxt, Constants.uuidDirName, Utils.getUuidLogName());
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

    public static void startLoggingService(Activity av) {
        Constants.LoggingServiceRunning = true;
        Utils.createNotificationChannel(av);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            av.startForegroundService(new Intent(av, LoggingService.class));
        } else {
            av.startService(new Intent(av, LoggingService.class));
        }
    }

    public static void startPullService(Activity av) {
        Constants.PullServiceRunning = true;
        Utils.createNotificationChannel(av);
        Intent intent = new Intent(av, PullService.class);
        av.startService(intent);
    }

    public static double getCoarseGpsCoord(double d, int precision) {
        double shift = (1 << 16); //16 is some number that 1 << 32 > 180 and bigger than maximum precision value that we are using
        return (getCoarseGpsCoordHelper(d + shift, precision) - shift);
    }

    public static double getCoarseGpsCoordHelper(double d, int precision) {
//        Log.e("ERR ",d+","+precision);
        long bits = Double.doubleToLongBits(d);
//        Log.e("ERR ",d+","+precision);

        long negative = bits & (1L << 63);
        int exponent = (int)((bits >> 52) & 0x7ffL);
        long mantissa = bits & 0xfffffffffffffL;

        int mantissaLog = 52;
        if (exponent == 0) {
            mantissaLog = (int)log(mantissa, 2);
        }
        else {
            mantissa = mantissa | (1L<<52);
        }

        int precisionShift = mantissaLog + exponent - 1075;

        int maskLength = Math.min(precision + precisionShift, 52);

        mantissa = mantissa >> (52 - maskLength);
        mantissa = mantissa << (52 - maskLength);

        if (mantissa == 0)
        {
            exponent = 0;
        }
        long result = negative |
                ((long)(exponent & 0x7ffL) << 52) |
                (mantissa & 0xfffffffffffffL);

        return Double.longBitsToDouble(result);
    }

    public static int log(long x, int base) {
        return (int) (Math.log(x) / Math.log(base));
    }

    public static String[] getBlePermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            String[] out = new String[Constants.blePermissions.length+Constants.gpsPermissions.length];
            int counter = 0;
            for (int i = 0 ; i < Constants.blePermissions.length; i++) {
                out[counter++] = Constants.blePermissions[i];
            }
            for (int i = 0 ; i < Constants.gpsPermissions.length; i++) {
                out[counter++] = Constants.gpsPermissions[i];
            }
            return out;
        }
        else {
            return Constants.blePermissions;
        }
    }

    public static boolean hasBlePermissions(Context context) {
        Log.e("results", "check for ble permission");
        if (context != null && Constants.blePermissions != null) {
            for (String permission : Constants.blePermissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("results", "return false on " + permission);
                    return false;
                }
            }
        }
        // for the lower APIs, you need location permissions to do bluetooth scanning
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.e("results","lower api, check for gps also");
            return hasGpsPermissions(context);
        }
        return true;
    }

    public static boolean hasGpsPermissions(Context context) {
        Log.e("results", "check for gps permission");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (context != null && Constants.gpsPermissions != null) {
                for (String permission : Constants.gpsPermissions) {
                    int result = ActivityCompat.checkSelfPermission(context, permission);
                    Log.e("logme", "perm " + result);
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Log.e("results", "return false on " + permission);
                        return false;
                    }
                }
            }
        }
        else {
            if (context != null && Constants.gpsPermissionsLite != null) {
                for (String permission : Constants.gpsPermissionsLite) {
                    int result = ActivityCompat.checkSelfPermission(context, permission);
                    Log.e("logme", "perm " + result);
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Log.e("results", "return false on " + permission);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static void openPhone (Activity av, String phoneNumber) {
        Intent launchBrowser = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phoneNumber));
        av.startActivity(launchBrowser);
    }

    public static void linkify(TextView tv, String str) {
        Spannable s = (Spannable) Html.fromHtml(str);
        for (URLSpan u : s.getSpans(0, s.length(), URLSpan.class)) {
            s.setSpan(new UnderlineSpan() {
                public void updateDrawState(TextPaint tp) {
                    tp.setUnderlineText(false);
                }
            }, s.getSpanStart(u), s.getSpanEnd(u), 0);
        }
        tv.setText(s);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static String time() {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm.ss aa");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static void createNotificationChannel(Context cxt) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

    public static String getNotifLogName() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date)+".txt";
    }

    public static String getBleLogName() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date)+".txt";
    }

    public static String getSymptomsLogName() {
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

    public static boolean canSubmitSymptoms(Context av, int submitThresh) {
        SharedPreferences prefs = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        long subDate = prefs.getLong(av.getString(R.string.symptom_submission_date_pkey), 0L);
        if (subDate == 0) {
            return true;
        }
        Date dd = new Date(subDate);
        return compareDates(dd, submitThresh);
    }

    public static String getLastSymptomReportDate(Context av) {
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm aa");
        SharedPreferences prefs = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        long subDate = prefs.getLong(av.getString(R.string.symptom_submission_date_pkey), 0L);
        if (subDate == 0) {
            return "";
        }
        return "Last submitted: "+dateFormat.format(subDate);
    }

    public static void updateSymptomSubmitTime(Activity av) {
        SharedPreferences.Editor prefs = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();
        prefs.putLong(av.getString(R.string.symptom_submission_date_pkey), System.currentTimeMillis());
        prefs.commit();
    }

    public static boolean compareDates(Date d1, int submitThresh) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date d2 = new Date();

            int diff = Utils.daysBetween(d2, d1);
            Log.e("logme", "days betweeen " + diff);

            return diff >= submitThresh;
        }
        catch(Exception e) {
            Log.e("logme",e.getMessage());
        }
        return false;
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

    public static int byteConvert(byte i) {
        if (i > 0) {
            return i;
        }
        else {
            return i&0xff;
        }
    }
}
