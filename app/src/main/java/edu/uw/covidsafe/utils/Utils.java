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
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;
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

    public static Handler serviceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            Bundle reply = msg.getData();
//            String out1 = reply.getString("gps");
//            String out2 = reply.getString("ble");
//            String out3 = reply.getString("uuid");
//            if (out1!=null) {
//                if (gpsLines > 20) {
//                    String ss = gpsResults.getText().toString();
//                    int ii = ss.indexOf("\n");
//                    String oo = ss.substring(ii+1,ss.length()) + out1+"\n";
//                    gpsResults.setText(oo);
//                }
//                else {
//                    gpsResults.append(out1 + "\n");
//                }
//                gpsLines+=1;
//            }
//            if (out2!=null) {
//                if (bleLines > 20) {
//                    String ss = bleResults.getText().toString();
//                    int ii = ss.indexOf("\n");
//                    String oo = ss.substring(ii+1,ss.length()) + out2+"\n";
//                    bleResults.setText(oo);
//                }
//                else {
//                    bleResults.append(out2 + "\n");
//                }
//                bleLines+=1;
//            }
//            if (out3 != null) {
//                bleBeaconId.setText(out3);
//            }
        }
    };

    public static void haltLoggingService(Activity av, View view) {
        if (Constants.LoggingServiceRunning) {
            Utils.mkSnack(av, view, "Logging is now turned off.");
        }

        Constants.LoggingServiceRunning = false;
        Log.e("logme", "stop service");
        av.stopService(new Intent(av, LoggingService.class));

        GpsUtils.haltGps();

        BluetoothUtils.haltBle(av);

        SharedPreferences prefs = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(av.getString(R.string.broadcasting_enabled_pkey), false);
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

    public static String randomGUID() {
        return UUID.randomUUID().toString();
    }

    public static void notif2(Context mContext) {
        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext.getApplicationContext(), "notify_001");
        Intent ii = new Intent(mContext.getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("CovidSafe");
        bigText.setBigContentTitle("You may have been exposed");
        bigText.setSummaryText("You may have been exposed");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.logo2);
        mBuilder.setContentTitle("Your Title");
        mBuilder.setContentText("Your text");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);
//        mBuilder.setBadgeIconType(R.drawable.logo2);

        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            channel.setShowBadge(true);
            channel.setAllowBubbles(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void notif(Context context) {
        try {
//            createNotificationChannel(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel serviceChannel = new NotificationChannel(
                        "mychannel",
                        "Example",
                        NotificationManager.IMPORTANCE_HIGH
                );
//                serviceChannel.enableVibration(true);
//                serviceChannel.enableLights(true);
                serviceChannel.setDescription("covidsafe channel");
//                NotificationManager manager = context.getSystemService(
//                        NotificationManager.class);
//                manager.createNotificationChannel(serviceChannel);
                // TODO this call needs API min 23/26??? investigate
                NotificationManager manager = context.getSystemService(
                        NotificationManager.class);
                manager.createNotificationChannel(serviceChannel);

                Notification notif = new NotificationCompat.Builder(context, "mychannel")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("CovidSafe Alert")
                        .setContentTitle("You've been exposed")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
                        .build();
                manager.notify(1,notif);
            }
//            Intent fullScreenIntent = new Intent(context, MainActivity.class);
//            PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
//                    fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "123")
//                    .setSmallIcon(R.drawable.ic_launcher_background)
//                    .setContentTitle("CovidSafe Alert")
//                    .setContentText("fullScreenPendingIntent")
//                    .setPriority(NotificationCompat.PRIORITY_MAX)
//                    .setCategory(NotificationCompat.CATEGORY_CALL)
//                    .setFullScreenIntent(fullScreenPendingIntent, true)
//                    .setAutoCancel(false);
            // Add the action button
//                .addAction(R.drawable.ic_launcher_foreground, ctx.getString(R.string.snooze),
//                        snoozePendingIntent);

//            Uri notification2 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Ringtone r = RingtoneManager.getRingtone(this, notification2);
//            r.play();
//            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // notificationId is a unique int for each notification that you must define
//            notificationManager.notify(13, builder.build());
        }
        catch(Exception e) {
            Log.e("ble",e.getMessage());
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

//    public static void uuidLogToDatabase(Context cxt, String seed, UUID uuid) {
//        new SeedUUIDOpsAsyncTask(cxt, seed, uuid).execute();
//    }

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

    public static void startLoggingService(Activity av) {
        Constants.LoggingServiceRunning = true;
        Utils.createNotificationChannel(av);

        av.startService(new Intent(av, LoggingService.class));

        SharedPreferences prefs = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(av.getString(R.string.broadcasting_enabled_pkey), true);
        editor.commit();
    }

    public static void startPullService(Activity av) {
        Constants.PullServiceRunning = true;
        Utils.createNotificationChannel(av);
        Intent intent = new Intent(av, PullService.class);
        av.startService(intent);
    }

    public static double getCoarseGpsCoord(double d, int precision) {
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

    public static BitSet convert(long value) {
        BitSet bits = new BitSet();
        int index = 0;
        while (value != 0L) {
            if (value % 2L != 0) {
                bits.set(index);
            }
            ++index;
            value = value >>> 1;
        }
        return bits;
    }

    public static long convert(BitSet bits) {
        long value = 0L;
        for (int i = 0; i < bits.length(); ++i) {
            value += bits.get(i) ? (1L << i) : 0L;
        }
        return value;
    }
//
//    public static void sendDataToUI(Messenger messenger, String tag, String log) {
//        Bundle bb = new Bundle();
//        bb.putString(tag, log);
//        Message msg = new Message();
//        msg.setData(bb);
//        try {
//            messenger.send(msg);
//        } catch (RemoteException e) {
//            Log.i("error", "error");
//        }
//    }

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

    public static void goToUrl (Activity av, String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
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

//    public static Date getLastSubmitTime(Context cxt) {
//        String ss = FileOperations.readSubmitLog(cxt);
//        if (ss.isEmpty()) {
//            return null;
//        }
//        Log.e("logme","last line "+ss);
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        Date date = null;
//        try {
//            return dateFormat.parse(ss);
//        }
//        catch(Exception e) {
//            Log.e("logme",e.getMessage());
//        }
//        return null;
//    }

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

    public static int byteConvert(byte i) {
        if (i > 0) {
            return i;
        }
        else {
            return i&0xff;
        }
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
