package com.example.corona;

import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
            lastSentDate = new Date(lastSent);
        }

        DateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] files = FileOperations.readfilelist(cxt);
        for (String s : files) {
            if (lastSentDate!=null) {
                try {
                    Date curDate = fileDateFormat.parse(s);
                    Log.e("logme", "diff " + fileDateFormat.format(lastSentDate) + "," + fileDateFormat.format(curDate) + "," +
                            Utils.compareDates(lastSentDate, curDate));
                } catch (Exception e) {
                    Log.e("logme", e.getMessage());
                }
            }
            else {

            }
        }
    }
}
