package com.example.covidsafe.symptoms;

import android.content.Context;
import android.bluetooth.le.ScanResult;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.covidsafe.utils.Constants;
import com.example.covidsafe.utils.Utils;
import com.google.gson.JsonObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.List;
import java.util.UUID;

public class SymptomsOpsAsyncTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private SymptomsRecord result;
    private Constants.SymptomsDatabaseOps op;

    public SymptomsOpsAsyncTask(Context context, SymptomsRecord result) {
        this.context = context;
        this.result = result;
        this.op = Constants.SymptomsDatabaseOps.Insert;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.e("ble","doinbackground symptoms "+this.op);
        SymptomsDbRecordRepository repo = new SymptomsDbRecordRepository(context);
        if (this.op == Constants.SymptomsDatabaseOps.Insert) {
            repo.insert(this.result);
            if (Constants.WRITE_TO_DISK) {
                Utils.symptomsLogToFile(this.context, this.result);
            }
        }
        return null;
    }
}
