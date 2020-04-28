package edu.uw.covidsafe.contact_trace;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.TimeUtils;
import edu.uw.covidsafe.utils.Utils;
import java.util.List;

public class HumanOpsAsyncTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private HumanRecord result;
    private Constants.HumanDatabaseOps op;

    public HumanOpsAsyncTask(Context activity, String phone, String name, String imageUri) {
        this.context = activity;
        this.result = new HumanRecord(phone, name, imageUri);
        this.op = Constants.HumanDatabaseOps.Insert;
    }

    public HumanOpsAsyncTask(Context activity, Constants.HumanDatabaseOps op) {
        this.context = activity;
        this.op = op;
    }

    public HumanOpsAsyncTask(Context activity, Constants.HumanDatabaseOps op, HumanRecord record) {
        this.context = activity;
        this.op = op;
        this.result = record;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.e("human","doinbackground human "+this.op);
        HumanDbRecordRepository repo = new HumanDbRecordRepository(context);
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
