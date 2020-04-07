package edu.uw.covidsafe;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.uw.covidsafe.comms.PullFromServerTask;
import edu.uw.covidsafe.utils.Constants;

public class PullService extends IntentService {

    public PullService() {
        super("PullService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.e("state","pull service started");
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        // TODO unchange after demo
//        Constants.pullFromServerTask = exec.scheduleWithFixedDelay(new PullFromServerTask(getApplicationContext()), 0, Constants.PullFromServerIntervalInMinutes, TimeUnit.MINUTES);
        Constants.logPurgerTask = exec.scheduleWithFixedDelay(new LogPurgerTask(getApplicationContext()), 0, Constants.LogPurgerIntervalInDays, TimeUnit.DAYS);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Constants.PullServiceRunning = false;
    }
}
