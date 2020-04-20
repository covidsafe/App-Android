package edu.uw.covidsafe.gps;

import android.content.Context;
import android.os.AsyncTask;

import java.util.LinkedList;
import java.util.List;

import edu.uw.covidsafe.utils.CryptoUtils;

public class GetGpsRecords extends AsyncTask<Void, Void, Void> {

    Context context;

    public GetGpsRecords(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        GpsDbRecordRepository repo = new GpsDbRecordRepository(context);
        List<GpsRecord> records = repo.getAllRecords();

        List<String> lats = new LinkedList<>();
        List<String> lons = new LinkedList<>();
        for(GpsRecord record : records) {
            lats.add(record.getRawLat());
            lons.add(record.getRawLongi());
        }

        String[] decryptedLats = CryptoUtils.decryptBatch(context, lats);
        String[] decryptedLons = CryptoUtils.decryptBatch(context, lons);

        return null;
    }
}
