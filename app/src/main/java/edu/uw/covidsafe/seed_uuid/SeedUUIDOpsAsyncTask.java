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
    private Constants.UUIDDatabaseOps op;

    public SeedUUIDOpsAsyncTask(Context context, SeedUUIDRecord record) {
        this.context = context;
        this.record = record;
        this.op = Constants.UUIDDatabaseOps.Insert;
    }

    public SeedUUIDOpsAsyncTask(Constants.UUIDDatabaseOps ops) {
        this.op = ops;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.e("ble","doinbackground uuid "+this.op);
        SeedUUIDDbRecordRepository repo = new SeedUUIDDbRecordRepository(context);
        if (this.op == Constants.UUIDDatabaseOps.Insert) {
            repo.insert(this.record);
            if (Constants.WRITE_TO_DISK) {
                Utils.uuidLogToFile(this.context, this.record);
            }
            List<SeedUUIDRecord> records = repo.getAllRecords();
            SeedUUIDRecord out = records.get(0);
            Log.e("adf",out.toString());
        }
        else if (this.op == Constants.UUIDDatabaseOps.ViewAll) {
            List<SeedUUIDRecord> records = repo.getAllRecords();
            for (SeedUUIDRecord record : records) {
                Log.e("uuid",record.toString());
            }
        }
        return null;
    }
}
