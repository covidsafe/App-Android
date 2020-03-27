package com.example.covidsafe.uuid;

import android.content.Context;
import android.location.Location;
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

public class UUIDOpsAsyncTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private UUIDRecord record;
    private Constants.UUIDDatabaseOps op;

    public UUIDOpsAsyncTask(Context context, UUID uuid) {
        this.context = context;
        this.record = new UUIDRecord(System.currentTimeMillis(), uuid.toString());
        this.op = Constants.UUIDDatabaseOps.Insert;
    }

    public UUIDOpsAsyncTask(Context context) {
        this.context = context;
        this.op = Constants.UUIDDatabaseOps.ViewAll;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.e("ble","doinbackground uuid "+this.op);
        UUIDDbRecordRepository repo = new UUIDDbRecordRepository(context);
        if (this.op == Constants.UUIDDatabaseOps.Insert) {
            repo.insert(this.record);
            if (Constants.WRITE_TO_DISK) {
                Utils.uuidLogToFile(this.context, this.record);
            }
        }
//        else if (this.op == Constants.UUIDDatabaseOps.ViewAll) {
//            List<UUIDRecord> records = repo.getAllRecords();
//            for (UUIDRecord record : records) {
//                Log.e("uuid",record.toString());
//            }
//        }
        return null;
    }
}
