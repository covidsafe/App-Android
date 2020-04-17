package edu.uw.covidsafe.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.covidsafe.R;

import java.security.DigestException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import edu.uw.covidsafe.seed_uuid.SeedUUIDOpsAsyncTask;
import edu.uw.covidsafe.seed_uuid.SeedUUIDRecord;

public class RegenerateSeedUponReport extends AsyncTask<Void, Void, Void> {

    public Context context;

    public RegenerateSeedUponReport(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        Log.e("crypto","regenerateSeedUponReport");
        super.onPreExecute();
        Constants.uuidGeneartionTask.cancel(true);
        try {
            new SeedUUIDOpsAsyncTask(Constants.UUIDDatabaseOps.DeleteAll, context).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // disable the current uuid generation task
        // delete all stored seeds and uuids

        Log.e("crypto","done deleting");

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm aa");

        // generate a new random seed
        // generate seeds for a period of InfectionWindow
        // store all of these to disk
        int infectionWindowInMilliseconds = 1000*60*60*24*Constants.InfectionWindowInDays;
        int UUIDGenerationIntervalInMiliseconds = Constants.UUIDGenerationIntervalInMinutes*60*1000;

        int numSeedsToGenerate = infectionWindowInMilliseconds / UUIDGenerationIntervalInMiliseconds;

        String generatedSeed = "";
        String generatedUUID = null;
        // time from 14 days back
        long curTime = TimeUtils.getTime();
        long ts = curTime - UUIDGenerationIntervalInMiliseconds;

        String s1 = format.format(new Date(ts));
        String s2 = format.format(new Date(curTime));

        Log.e("crypto","time "+s1);
        Log.e("crypto","time "+s2);
        numSeedsToGenerate = 1000;
        long time = System.currentTimeMillis();
        byte[] seed = ByteUtils.uuid2bytes(UUID.randomUUID());
        for (int i = 0; i < numSeedsToGenerate-1; i++) {
            if (i%10==0) {
                Log.e("crypto ", i + "/" + numSeedsToGenerate);
                Log.e("time","timing "+i+"-"+(System.currentTimeMillis()-time));
            }
            SeedUUIDRecord record = null;
            try {
                record = CryptoUtils.generateSeedHelper(seed);
            } catch (DigestException e) {
                Log.e("err",e.getMessage());
            }
            record.setTs(ts);
            ts += UUIDGenerationIntervalInMiliseconds;
            new SeedUUIDOpsAsyncTask(context, record).execute();
        }

        SeedUUIDRecord record = null;
        try {
            record = CryptoUtils.generateSeedHelper(seed);
        } catch (DigestException e) {
            Log.e("err",e.getMessage());
        }
        record.setUUID("");
        record.setTs(ts);
        new SeedUUIDOpsAsyncTask(context, record).execute();

        Log.e("crypto","setting new preference time "+generatedSeed+","+ts);
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.most_recent_seed_pkey), record.getSeed());
        editor.putLong(context.getString(R.string.most_recent_seed_timestamp_pkey), curTime);
        editor.commit();
        Log.e("crypto","done setting new preference time");

        return null;
    }
}
