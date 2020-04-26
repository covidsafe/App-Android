package edu.uw.covidsafe;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class CovidSafeApplication extends Application {

    public static final String LOGGING_SERVICE_CHANNEL_ID = "loggingServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel loggingServiceChannel = new NotificationChannel(LOGGING_SERVICE_CHANNEL_ID,
                    "LoggingServiceChannel", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(loggingServiceChannel);
            }
        }
    }
}
