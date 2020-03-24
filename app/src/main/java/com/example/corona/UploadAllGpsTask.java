package com.example.corona;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UploadAllGpsTask implements Runnable {

    public Context cxt;

    public UploadAllGpsTask(Activity av, Context cxt) {
        this.cxt = cxt;
        TextView tv = (TextView)av.findViewById(R.id.lastSentTime);
        DateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm.ss aa");
        Date date = new Date(System.currentTimeMillis());
        tv.setText("Last sent "+fileDateFormat.format(date));
    }

    @Override
    public void run() {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm.ss aa");
        Date dd = new Date();
        Log.e("logme", "upload task "+dateFormat.format(dd));

//        long lastSent = Utils.readLastSentLog(cxt);
//        Date lastSentDate = new Date(lastSent);

        DateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date[] dates = FileOperations.readGpsFileList(cxt, true,"network");

        GpsRecords recordsToSend = new GpsRecords();
        for (Date date : dates) {
            try {
//                int daysBetween = Utils.daysBetween(lastSentDate, date);
//                Log.e("logme", "diff " + fileDateFormat.format(lastSentDate) + "," + fileDateFormat.format(date) + "," +
//                        daysBetween);

                String niceDate = fileDateFormat.format(date);
//                if (daysBetween > 0) {
                    ArrayList<GpsRecord> records = FileOperations.readGpsRecords(cxt, "network", niceDate);
                    if (records != null) {
                        recordsToSend.addAll(records);
                    }
//                }
//                else if (daysBetween == 0) {
//                    ArrayList<GpsRecord> records = FileOperations.readGpsRecords(cxt, niceDate);
//                    if (records != null) {
//                        for (GpsRecord record : records) {
//                            if (record.ts > lastSentDate.getTime()) {
//                                recordsToSend.add(record);
//                            }
//                        }
//                    }
//                }
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
                        if (Constants.LOG_TO_DISK) {
                            FileOperations.writeLastSentLog(cxt, lastTimestamp);
                        }
                        Utils.writeLastSentLog(cxt, lastTimestamp);
                        DateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Log.e("logme", "last sent "+fileDateFormat.format(new Date(lastTimestamp)));
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
