package edu.uw.covidsafe.gps;

import android.content.Context;
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

    public GpsOpsAsyncTask(Context context, Location loc, long ts) {
        this.context = context;
        this.record = new GpsRecord(ts, loc.getLatitude(), loc.getLongitude(), loc.getProvider());
        this.op = Constants.GpsDatabaseOps.Insert;
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
        }
        return null;
    }
}
