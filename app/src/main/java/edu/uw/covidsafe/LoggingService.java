package edu.uw.covidsafe;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.covidsafe.R;

import edu.uw.covidsafe.ble.BluetoothServerHelper;
import edu.uw.covidsafe.ble.BluetoothUtils;
import edu.uw.covidsafe.gps.GpsUtils;
import edu.uw.covidsafe.seed_uuid.UUIDGeneratorTask;
import edu.uw.covidsafe.comms.PullFromServerTask;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LoggingService extends IntentService {

    public LoggingService() {
        super("LocationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("logme","handle intent");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        Bundle bundle = intent.getExtras();
        Log.e("ex","bundle status "+(bundle==null));

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        boolean bleEnabled = prefs.getBoolean(getApplicationContext().getString(R.string.ble_enabled_pkey), Constants.BLUETOOTH_ENABLED);
        boolean gpsEnabled = prefs.getBoolean(getApplicationContext().getString(R.string.gps_enabled_pkey), Constants.GPS_ENABLED);

        if (bleEnabled) {
            BluetoothUtils.startBle(getApplicationContext());
        }

        if (gpsEnabled) {
            GpsUtils.startGps(getApplicationContext());
        }

        Notification notification = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notif_message))
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(1,notification);

        //////////////////////////////////////////////////////////////////////////////////////////
//        try {
//            Thread.sleep(5000);
//            createNotificationChannel();
//            Intent fullScreenIntent = new Intent(this, MainActivity.class);
//            PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
//                    fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "123")
//                    .setSmallIcon(R.drawable.ic_launcher_background)
//                    .setContentTitle("CovidSafe Alert")
//                    .setContentText("fullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntent")
//                    .setPriority(NotificationCompat.PRIORITY_MAX)
//                    .setCategory(NotificationCompat.CATEGORY_CALL)
//                    .setFullScreenIntent(fullScreenPendingIntent, true)
//                    .setAutoCancel(false);
//            // Add the action button
////                .addAction(R.drawable.ic_launcher_foreground, ctx.getString(R.string.snooze),
////                        snoozePendingIntent);
//
//            Uri notification2 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Ringtone r = RingtoneManager.getRingtone(this, notification2);
//            r.play();
//            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//
//            // notificationId is a unique int for each notification that you must define
//            notificationManager.notify(13, builder.build());
//        }
//        catch(Exception e) {
//            Log.e("ble",e.getMessage());
//        }
        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ("name");
            String description = ("desc");
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("123", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //this call is not guaranteed by android system
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("logme", "service destroyed");
    }

}