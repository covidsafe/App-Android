package edu.uw.covidsafe;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.uw.covidsafe.comms.PullFromServerTask;
import edu.uw.covidsafe.comms.PullFromServerTaskDemo;
import edu.uw.covidsafe.utils.Constants;

import static com.microsoft.appcenter.utils.HandlerUtils.runOnUiThread;

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

        Constants.pullFromServerTask = new Timer();
        Constants.pullFromServerTask.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        if (Constants.DEBUG) {
//                            new PullFromServerTaskDemo(getApplicationContext(), getActivity(), view).execute();
//                        }
//                        else {
                            new PullFromServerTask(getApplicationContext(), null).execute();
//                        }
                    }
                });
            }
        }, 0, Constants.PullFromServerIntervalInMinutesInMilliseconds);

        Constants.logPurgerTask = exec.scheduleWithFixedDelay(new LogPurgerTask(getApplicationContext()), 0, Constants.LogPurgerIntervalInDays, TimeUnit.DAYS);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Constants.PullServiceRunning = false;
    }
}
