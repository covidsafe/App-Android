package edu.uw.covidsafe.ble;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import edu.uw.covidsafe.utils.Constants;

public class BleOpsAsyncTask extends AsyncTask<Void, Void, Void> {
    private BleDbRecordRepository repo;
    private BleRecord result;
    private Constants.BleDatabaseOps op;

    public BleOpsAsyncTask(Context cxt, String id, int rssi, long ts, int model) {
        repo = new BleDbRecordRepository(cxt);
        this.result = new BleRecord(id, ts, rssi, model);
        this.op = Constants.BleDatabaseOps.Insert;
    }

    public BleOpsAsyncTask(Context cxt, Constants.BleDatabaseOps op) {
        repo = new BleDbRecordRepository(cxt);
        this.op = op;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.e("ble","doinbackground ble "+this.op);
        if (this.op == Constants.BleDatabaseOps.Insert) {
            repo.insert(this.result);
//            if (Constants.WRITE_TO_DISK) {
//                Utils.bleLogToFile(this.context, this.result);
//            }
        }
        else if (this.op == Constants.BleDatabaseOps.ViewAll) {
            List<BleRecord> records = repo.getAllRecords();
            for (BleRecord record : records) {
                Log.e("ble",record.toString());
            }
        }
        else if (this.op == Constants.BleDatabaseOps.DeleteAll) {
            repo.deleteAll();
        }
        return null;
    }
}
