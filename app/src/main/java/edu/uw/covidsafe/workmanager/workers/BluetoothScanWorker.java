package edu.uw.covidsafe.workmanager.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class BluetoothScanWorker extends Worker {

    private Context context;
    private WorkerParameters workerParameters;
    private Data.Builder resultData;
    public static final String STATUS = "status";

    public BluetoothScanWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.workerParameters = workerParams;
        resultData = new Data.Builder();
    }

    @NonNull
    @Override
    public Result doWork() {
        return null;
    }
}
