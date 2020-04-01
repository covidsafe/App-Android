package edu.uw.covidsafe.uuid;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

import java.util.UUID;

public class UUIDOpsAsyncTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private UUIDRecord record;
    private Constants.UUIDDatabaseOps op;

    public UUIDOpsAsyncTask(Context context, UUID uuid) {
        this.context = context;
        this.record = new UUIDRecord(System.currentTimeMillis(), uuid.toString());
        this.op = Constants.UUIDDatabaseOps.Insert;
    }

    public UUIDOpsAsyncTask(Context context) {
        this.context = context;
        this.op = Constants.UUIDDatabaseOps.ViewAll;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.e("ble","doinbackground uuid "+this.op);
        UUIDDbRecordRepository repo = new UUIDDbRecordRepository(context);
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
