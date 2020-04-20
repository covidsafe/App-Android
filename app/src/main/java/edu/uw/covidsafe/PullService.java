package edu.uw.covidsafe;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.covidsafe.R;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.uw.covidsafe.comms.PullFromServerTask;
import edu.uw.covidsafe.utils.Constants;

public class PullService extends IntentService {

    public PullService() {
        super("PullService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Notification notification = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notif_message))
                .setSmallIcon(R.drawable.logo_purple)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2,notification);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Constants.LoggingServiceRunning = true;
        Log.e("state","pull service started");
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);

        Constants.pullFromServerTaskTimer = new Timer();
        Constants.pullFromServerTaskTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
            if (!Constants.PullFromServerTaskRunning) {
                new PullFromServerTask(getApplicationContext(), null).execute();
            }
            }
        }, 0, Constants.PullFromServerIntervalInMilliseconds);

        if (Constants.logPurgerTask == null || Constants.logPurgerTask.isDone()) {
            Constants.logPurgerTask = exec.scheduleWithFixedDelay(new LogPurgerTask(getApplicationContext()),
                    0, Constants.LogPurgerIntervalInDays, TimeUnit.DAYS);
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Constants.PullServiceRunning = false;
    }
}
