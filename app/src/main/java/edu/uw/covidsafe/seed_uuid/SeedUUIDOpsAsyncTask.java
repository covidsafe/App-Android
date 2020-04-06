package edu.uw.covidsafe.seed_uuid;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

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

    @Override
    protected Void doInBackground(Void... params) {
        Log.e("ble","doinbackground uuid "+this.op);
        SeedUUIDDbRecordRepository repo = new SeedUUIDDbRecordRepository(context);
        if (this.op == Constants.UUIDDatabaseOps.Insert) {
            repo.insert(this.record);
            if (Constants.WRITE_TO_DISK) {
                Utils.uuidLogToFile(this.context, this.record);
            }
        }
//        else if (this.op == Constants.UUIDDatabaseOps.ViewAll) {
//            List<UUIDRecord> records = repo.getAllRecords();
//            for (UUIDRecord record : records) {
//                Log.e("uuid",record.toString());
//            }
//        }
        return null;
    }
}
