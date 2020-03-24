package com.example.corona.data;

import android.content.Context;
import android.os.AsyncTask;

import com.example.corona.utils.Utils;

public class InsertGpsAsyncTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private long lat;
    private long longi;
    private String myGuid;

    public InsertGpsAsyncTask(Context activity, long lat, long longi) {
        this.myGuid = Utils.randomGUID();
        this.context = activity;
        this.lat = lat;
        this.longi = longi;
    }

    public void onPostExecute(Void result) {
    }

    @Override
    protected Void doInBackground(Void... params) {
        DbRecordRepository repo = new DbRecordRepository(context);
        repo.insert(new DbRecord(System.currentTimeMillis(), this.lat, this.longi));
        return null;
    }
}
