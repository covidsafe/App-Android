package edu.uw.covidsafe.symptoms;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import edu.uw.covidsafe.utils.Constants;

public class SymptomsOpsAsyncTask extends AsyncTask<Void, Void, Void> {
    private SymptomsDbRecordRepository repo;
    private SymptomsRecord result;
    private Constants.SymptomsDatabaseOps op;

    public SymptomsOpsAsyncTask(Context context, SymptomsRecord result) {
        repo = new SymptomsDbRecordRepository(context);
        this.result = result;
        this.op = Constants.SymptomsDatabaseOps.Insert;
    }

    public SymptomsOpsAsyncTask(Constants.SymptomsDatabaseOps op, Context cxt) {
        repo = new SymptomsDbRecordRepository(cxt);
        this.op = op;
    }

    public SymptomsOpsAsyncTask(Constants.SymptomsDatabaseOps op, Context cxt, SymptomsRecord record) {
        repo = new SymptomsDbRecordRepository(cxt);
        this.op = op;
        this.result = record;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.e("ble","doinbackground symptoms "+this.op);
        if (this.op == Constants.SymptomsDatabaseOps.Insert) {
            repo.insert(this.result);
//            if (Constants.WRITE_TO_DISK) {
//                Utils.symptomsLogToFile(this.context, this.result);
//            }
        }
        else if (this.op == Constants.SymptomsDatabaseOps.DeleteAll) {
            repo.deleteAll();
        }
        else if (this.op == Constants.SymptomsDatabaseOps.Delete) {
            repo.delete(this.result.getTs());
        }
        return null;
    }
}
