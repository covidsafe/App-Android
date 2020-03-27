package com.example.covidsafe.gps;

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

public class

GpsOpsAsyncTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private GpsRecord record;
    private Constants.GpsDatabaseOps op;

    public GpsOpsAsyncTask(Context context, Location loc, long ts) {
        this.context = context;
        this.record = new GpsRecord(ts, loc.getLatitude(), loc.getLongitude(), loc.getProvider());
        this.op = Constants.GpsDatabaseOps.Insert;
    }

    public GpsOpsAsyncTask(Context context, GpsRecord rec) {
        this.context = context;
        this.record = new GpsRecord(rec.getTs(), rec.getLat(), rec.getLongi(), rec.getProvider());
        this.op = Constants.GpsDatabaseOps.Insert;
    }

    public GpsOpsAsyncTask(Context context) {
        this.context = context;
        this.op = Constants.GpsDatabaseOps.ViewAll;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.e("ble","doinbackground gps "+this.op);
        GpsDbRecordRepository repo = new GpsDbRecordRepository(context);
        if (this.op == Constants.GpsDatabaseOps.Insert) {
            repo.insert(this.record);
            if (Constants.WRITE_TO_DISK) {
                Utils.gpsLogToFile(this.context, this.record);
            }
        }
        else if (this.op == Constants.GpsDatabaseOps.ViewAll) {
            List<GpsRecord> records = repo.getAllRecords();
            for (GpsRecord record : records) {
                Log.e("gps",record.toString());
            }

            //TODO: package up GPS records and send to cloud
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
