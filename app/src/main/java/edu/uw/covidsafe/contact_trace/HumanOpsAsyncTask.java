package edu.uw.covidsafe.contact_trace;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import edu.uw.covidsafe.utils.Constants;

public class HumanOpsAsyncTask extends AsyncTask<Void, Void, Void> {
    private HumanDbRecordRepository repo;
    private HumanRecord result;
    private Constants.HumanDatabaseOps op;

    public HumanOpsAsyncTask(Context cxt, String phone, String name, String imageUri, String email) {
        repo = new HumanDbRecordRepository(cxt);
        this.result = new HumanRecord(phone, name, imageUri, email);
        this.op = Constants.HumanDatabaseOps.Insert;
    }

    public HumanOpsAsyncTask(Context cxt, Constants.HumanDatabaseOps op) {
        repo = new HumanDbRecordRepository(cxt);
        this.op = op;
    }

    public HumanOpsAsyncTask(Context cxt, Constants.HumanDatabaseOps op, HumanRecord record) {
        repo = new HumanDbRecordRepository(cxt);
        this.op = op;
        this.result = record;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.e("human","doinbackground human "+this.op);
        if (this.op == Constants.HumanDatabaseOps.Insert) {
            repo.insert(this.result);
        }
        else if (this.op == Constants.HumanDatabaseOps.Delete) {
            repo.delete(this.result.getName());
        }
        else if (this.op == Constants.HumanDatabaseOps.DeleteAll) {
            repo.deleteAll();
        }
        return null;
    }
}
