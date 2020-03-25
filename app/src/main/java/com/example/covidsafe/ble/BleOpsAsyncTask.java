package com.example.covidsafe.ble;

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

public class BleOpsAsyncTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private BleRecord result;
    private Constants.BleDatabaseOps op;

    public BleOpsAsyncTask(Context activity, String id) {
        this.context = activity;
        this.result = new BleRecord(id, System.currentTimeMillis(), false, false);
        this.op = Constants.BleDatabaseOps.Insert;
    }

    public BleOpsAsyncTask(Context context, BleRecord result) {
        this.context = context;
        this.result = result;
        this.op = Constants.BleDatabaseOps.Insert;
    }

    public BleOpsAsyncTask(Context context) {
        this.context = context;
        this.op = Constants.BleDatabaseOps.ViewAll;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.e("ble","doinbackground ble "+this.op);
        BleDbRecordRepository repo = new BleDbRecordRepository(context);
        if (this.op == Constants.BleDatabaseOps.Insert) {
            repo.insert(this.result);
            if (Constants.WRITE_TO_DISK) {
                Utils.bleLogToFile(this.context, this.result);
            }
        }
        else if (this.op == Constants.BleDatabaseOps.ViewAll) {
            List<BleRecord> records = repo.getAllRecords();
            for (BleRecord record : records) {
                Log.e("ble",record.toString());
            }

            if (records.size() > 0) {
                if (records.size() > 10) {
                    records = records.subList(0, 10);
                }
                sendRecords(Utils.ble2json(records));
            }
        }
        return null;
    }

    public void sendRecords(final JsonObject obj) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //TODO change url and also in network_security_config.xml
                    InetSocketAddress addr = new InetSocketAddress("128.208.4.83",5000);
                    URL url = new URL("http://"+addr.toString()+"/companies");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
//                    os.writeBytes(URLEncoder.encode(records.toJson().toString(), "UTF-8"));
                    os.writeBytes(obj.toString());

                    os.flush();
                    os.close();

                    int resp = conn.getResponseCode();
                    if (resp != 200) {
                        Toast.makeText(context,"Failed to send records. Please try again.", Toast.LENGTH_LONG);
                    }
                    Log.e("STATUS", String.valueOf(resp));
                    Log.e("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    Log.e("logme",e.getMessage());
                }
            }
        });

        thread.start();
    }
}
