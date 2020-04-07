package edu.uw.covidsafe.ui.notif;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import edu.uw.covidsafe.seed_uuid.SeedUUIDDbRecordRepository;
import edu.uw.covidsafe.seed_uuid.SeedUUIDRecord;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

public class GetHistoryAsyncTask extends AsyncTask<Void, Void, Void> {
    private Context context;

    public GetHistoryAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
//        Log.e("ble","doinbackground uuid "+this.op);
//        NotifDbRecordRepository repo = new NotifDbRecordRepository(context);
//        repo.getAllRecords()
        return null;
    }
}
