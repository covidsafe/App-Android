package edu.uw.covidsafe.utils;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import edu.uw.covidsafe.GrpcServiceListener;

public class ServiceUtils {
    public static void scheduleLookupJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, GrpcServiceListener.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(1 * 1000);
        builder.setOverrideDeadline(10* 1000);
        JobScheduler jobScheduler = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            jobScheduler = context.getSystemService(JobScheduler.class);
        }
        jobScheduler.schedule(builder.build());
    }
}
