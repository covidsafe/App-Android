package edu.uw.covidsafe.ui.notif;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;
import com.google.gson.JsonObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.List;

public class NotifOpsAsyncTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private NotifRecord record;
    private Constants.NotifDatabaseOps op;

    public NotifOpsAsyncTask(Context context, NotifRecord rec) {
        this.context = context;
        this.record = new NotifRecord(rec.getTs_start(), rec.getTs_end(), rec.getMsg(), rec.getMsgType());
        this.op = Constants.NotifDatabaseOps.Insert;
    }

    public NotifOpsAsyncTask(Context context) {
        this.context = context;
        this.op = Constants.NotifDatabaseOps.ViewAll;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.e("ble","doinbackground notif "+this.op);
        NotifDbRecordRepository repo = new NotifDbRecordRepository(context);
        if (this.op == Constants.NotifDatabaseOps.Insert) {
            repo.insert(this.record);
            if (Constants.WRITE_TO_DISK) {
                Utils.notifLogToFile(this.context, this.record);
            }
        }
        else if (this.op == Constants.NotifDatabaseOps.ViewAll) {
            List<NotifRecord> records = repo.getAllRecords();
            for (NotifRecord record : records) {
                Log.e("notif",record.toString());
            }
        }
        return null;
    }
}
