package edu.uw.covidsafe.seed_uuid;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

import java.util.List;
import java.util.UUID;

public class SeedUUIDOpsAsyncTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private SeedUUIDRecord record;
    private List<SeedUUIDRecord> records;
    private Constants.UUIDDatabaseOps op;
    public String msg="";

    public SeedUUIDOpsAsyncTask(Context context, SeedUUIDRecord record) {
        this.context = context;
        this.record = record;
        this.op = Constants.UUIDDatabaseOps.Insert;
    }

    public SeedUUIDOpsAsyncTask(Context context, List<SeedUUIDRecord> records) {
        this.context = context;
        this.records = records;
        this.op = Constants.UUIDDatabaseOps.BatchInsert;
    }

    public SeedUUIDOpsAsyncTask(Constants.UUIDDatabaseOps ops, Context context) {
        this.op = ops;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.e("uuid","doinbackground uuid "+this.op);
        SeedUUIDDbRecordRepository repo = new SeedUUIDDbRecordRepository(context);
        if (this.op == Constants.UUIDDatabaseOps.Insert) {
            Log.e("uuid","insert "+this.record.getRawTs() + " "+msg);
            repo.insert(this.record);
            if (Constants.WRITE_TO_DISK) {
                Utils.uuidLogToFile(this.context, this.record);
            }
        }
        else if (this.op == Constants.UUIDDatabaseOps.BatchInsert) {
            Log.e("uuid","batch insert "+records.size());
            int counter = 0;
            for (SeedUUIDRecord record : records) {
                Log.e("uuid","insert "+counter);
                counter++;
                repo.insert(record);
                if (Constants.WRITE_TO_DISK) {
                    Utils.uuidLogToFile(this.context, record);
                }
            }
        }
        else if (this.op == Constants.UUIDDatabaseOps.ViewAll) {
            List<SeedUUIDRecord> records = repo.getAllRecords();
            for (SeedUUIDRecord record : records) {
                Log.e("uuid",record.toString());
            }
        }
        else if (this.op == Constants.UUIDDatabaseOps.DeleteAll) {
            repo.deleteAll();
        }
        return null;
    }
}
