package com.example.covidsafe.data;

import android.content.Context;
import android.os.AsyncTask;

import com.example.covidsafe.utils.Utils;

public class InsertBleAsyncTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private long lat;
    private long longi;
    private String myGuid;

    public InsertBleAsyncTask(Context activity, long lat, long longi) {
        this.myGuid = Utils.randomGUID();
        this.context = activity;
        this.lat = lat;
        this.longi = longi;
    }

    public void onPostExecute(Void result) {
    }

    @Override
    protected Void doInBackground(Void... params) {
        GpsDbRecordRepository repo = new GpsDbRecordRepository(context);
        repo.insert(new GpsDbRecord(System.currentTimeMillis(), this.lat, this.longi));
        return null;
    }
}
