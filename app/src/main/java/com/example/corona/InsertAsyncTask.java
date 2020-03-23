package com.example.corona;

import android.content.Context;
import android.os.AsyncTask;

import androidx.fragment.app.FragmentActivity;

public class InsertAsyncTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private long lat;
    private long longi;
    private String myGuid;

    public InsertAsyncTask(Context activity, long lat, long longi) {
        this.myGuid = Utils.randomGUID();
        if (Constants.insertAsyncTaskRunning.isEmpty()) {
            Constants.insertAsyncTaskRunning = this.myGuid;
            this.context = activity;
            this.lat = lat;
            this.longi = longi;
        } else {
            this.cancel(true);
        }
    }

    public void onPostExecute(Void result) {
        if (Constants.insertAsyncTaskRunning.equals(this.myGuid)) {
            Constants.insertAsyncTaskRunning = "";
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (Constants.insertAsyncTaskRunning.equals(this.myGuid)) {
            DbRecordRepository repo = new DbRecordRepository(context);
            repo.insert(new DbRecord(System.currentTimeMillis(), this.lat, this.longi));
        }
        return null;
    }
}
