package edu.uw.covidsafe.gps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;
import java.util.List;

public class

GpsOpsAsyncTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private GpsRecord record;
    private Constants.GpsDatabaseOps op;
    long ts;

    public GpsOpsAsyncTask(Context context, Location loc, long ts) {
        Log.e("gps", "gps ops async task constructor");
        this.context = context;
        this.record = new GpsRecord(ts, loc.getLatitude(), loc.getLongitude(), loc.getProvider(), context);
        this.op = Constants.GpsDatabaseOps.Insert;
    }

    public GpsOpsAsyncTask(GpsRecord record, Context cxt) {
        this.record = record;
        op = Constants.GpsDatabaseOps.Insert;
        this.context = cxt;
    }

    public GpsOpsAsyncTask(Constants.GpsDatabaseOps op, Context cxt) {
        this.op = op;
        this.context = cxt;
    }

    public GpsOpsAsyncTask(Context cxt, Constants.GpsDatabaseOps op, long ts) {
        this.op = op;
        this.context = cxt;
        this.ts = ts;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.e("gps","doinbackground gps "+this.op);
        GpsDbRecordRepository repo = new GpsDbRecordRepository(context);
        if (this.op == Constants.GpsDatabaseOps.Insert) {
            Geocoder gc = new Geocoder(context);
            if (gc.isPresent()) {
                try {
                    List<Address> addresses = gc.getFromLocation(this.record.getLat(context), this.record.getLongi(context), 1);
                    this.record.setAddress(addresses.get(0).getAddressLine(0), context);
                }
                catch(Exception e) {
                    Log.e("err","gc "+e.getMessage());
                }
            }
            repo.insert(this.record);
        }
        else if (this.op == Constants.GpsDatabaseOps.ViewAll) {
            List<GpsRecord> records = repo.getAllRecords();
            for (GpsRecord record : records) {
                Log.e("gps",record.toString());
            }
        }
        else if (this.op == Constants.GpsDatabaseOps.Delete) {
            repo.delete(ts);
        }
        else if (this.op == Constants.GpsDatabaseOps.DeleteAll) {
            repo.deleteAll();
        }
        return null;
    }
}
