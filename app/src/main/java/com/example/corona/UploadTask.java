package com.example.corona;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UploadTask implements Runnable {

    public Context cxt;

    public UploadTask(Context cxt) {
        this.cxt = cxt;
    }

    @Override
    public void run() {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm.ss aa");
        Date dd = new Date();
        Log.e("logme", "upload task "+dateFormat.format(dd));

        Date lastSentDate = null;
        String lastSent = FileOperations.readLastSentLog(cxt);
        if (!lastSent.isEmpty()) {
            lastSentDate = new Date(Long.parseLong(lastSent));
        }
        else {
            lastSentDate = new Date(0000000000000L);
        }

        DateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date[] dates = FileOperations.readfilelist(cxt, true);

        GpsRecords recordsToSend = new GpsRecords();
        for (Date date : dates) {
            try {
                int daysBetween = Utils.daysBetween(lastSentDate, date);
                Log.e("logme", "diff " + fileDateFormat.format(lastSentDate) + "," + fileDateFormat.format(date) + "," +
                        daysBetween);

                String niceDate = fileDateFormat.format(date);
                if (daysBetween > 0) {
                    ArrayList<GpsRecord> records = FileOperations.readGpsRecords(cxt, niceDate);
                    if (records != null) {
                        recordsToSend.addAll(records);
                    }
                }
                else if (daysBetween == 0) {
                    ArrayList<GpsRecord> records = FileOperations.readGpsRecords(cxt, niceDate);
                    if (records != null) {
                        for (GpsRecord record : records) {
                            if (record.ts > lastSentDate.getTime()) {
                                recordsToSend.add(record);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("logme", e.getMessage());
            }
        }

        if (recordsToSend.records.size() > 0) {
            sendRecords(recordsToSend);
        }
    }

    public void sendRecords(final GpsRecords records) {
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
                    os.writeBytes(records.toJson().toString());

                    os.flush();
                    os.close();

                    int resp = conn.getResponseCode();
                    if (resp == 200) {
                        long lastTimestamp = records.records.getLast().ts;
                        FileOperations.writeLastSentLog(cxt, lastTimestamp);
                        DateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Log.e("logme", "last sent "+fileDateFormat.format(new Date(lastTimestamp)));
                    }
                    Log.e("STATUS", String.valueOf(resp));
                    Log.e("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
