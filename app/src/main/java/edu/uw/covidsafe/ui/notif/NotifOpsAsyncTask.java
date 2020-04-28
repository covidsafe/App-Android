package edu.uw.covidsafe.ui.notif;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;
import java.util.List;

public class NotifOpsAsyncTask extends AsyncTask<Void, Void, Void> {
    private NotifDbRecordRepository repo;
    private NotifRecord record;
    private Constants.NotifDatabaseOps op;

    public NotifOpsAsyncTask(Context context, NotifRecord rec) {
         repo = new NotifDbRecordRepository(context);
        this.record = new NotifRecord(rec.getTs_start(), rec.getTs_end(), rec.getMsg(), rec.getMsgType(), rec.current);
        this.op = Constants.NotifDatabaseOps.Insert;
    }

    public NotifOpsAsyncTask(Context context, Constants.NotifDatabaseOps op) {
        repo = new NotifDbRecordRepository(context);
        this.op = op;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.e("notif","doinbackground notif "+this.op);
        if (this.op == Constants.NotifDatabaseOps.Insert) {
            repo.insert(this.record);
//            if (Constants.WRITE_TO_DISK) {
//                Utils.notifLogToFile(this.context, this.record);
//            }
        }
        else if (this.op == Constants.NotifDatabaseOps.ViewAll) {
            List<NotifRecord> records = repo.getAllRecords();
            for (NotifRecord record : records) {
                Log.e("notif",record.toString());
            }
        }
        else if (this.op == Constants.NotifDatabaseOps.DeleteAll) {
            Log.e("notif","delete all");
            repo.deleteAll();
        }
        return null;
    }
}
