package edu.uw.covidsafe;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import edu.uw.covidsafe.ble.BleDbRecordRepository;
import edu.uw.covidsafe.ble.BleRecord;
import edu.uw.covidsafe.gps.GpsDbRecordRepository;
import edu.uw.covidsafe.gps.GpsRecord;
import edu.uw.covidsafe.seed_uuid.SeedUUIDDbRecordRepository;
import edu.uw.covidsafe.symptoms.SymptomsDbRecordRepository;
import edu.uw.covidsafe.utils.Constants;
import io.reactivex.schedulers.Schedulers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.example.covidsafe.R;
import com.instacart.library.truetime.TrueTime;
import com.instacart.library.truetime.TrueTimeRx;

public class LogPurgerTask implements Runnable {

    Context context;

    public LogPurgerTask(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        Log.e("uuid", "PURGE LOGS");
        try {
            Log.e("truetime","truetime init");
            TrueTimeRx.build()
                    .initializeRx("time.apple.com")
                    .subscribeOn(Schedulers.io())
                    .subscribe(date -> {
                        Log.e("truetime", "TrueTime was initialized and we have a time: " + TrueTime.now());
                    }, throwable -> {
                        throwable.printStackTrace();
                    });
            Log.e("truetime","truetime build ");
        }
        catch(Exception e) {
            Log.e("truetime",e.getMessage());
        }

        try {
            SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
            int daysOfLogsToKeep = prefs.getInt(context.getString(R.string.infection_window_in_days_pkeys), Constants.DefaultDaysOfLogsToKeep);

            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, -daysOfLogsToKeep);
            long thresh = calendar.getTime().getTime();
            Log.e("ble", "THRESH " + thresh);

            BleDbRecordRepository bleRepo = new BleDbRecordRepository(context);
            GpsDbRecordRepository gpsRepo = new GpsDbRecordRepository(context);
            SymptomsDbRecordRepository symptomsRepo = new SymptomsDbRecordRepository(context);
            SeedUUIDDbRecordRepository seedUUIDRepo = new SeedUUIDDbRecordRepository(context);

            bleRepo.deleteEarlierThan(thresh);
            gpsRepo.deleteEarlierThan(thresh);
            symptomsRepo.deleteEarlierThan(thresh);
            seedUUIDRepo.deleteEarlierThan(thresh);
        }
        catch(Exception e) {
                Log.e("logpurger",e.getMessage());
        }
    }

    public void blePurgeTest(long thresh) {
        BleDbRecordRepository bleRepo = new BleDbRecordRepository(context);
        try {
            bleRepo.deleteAll();
            Thread.sleep(1000);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (int i = 14; i <= 29; i++) {
                Date d1 = sdf.parse("2020-03-" + i);
                bleRepo.insert(new BleRecord(UUID.randomUUID().toString(), d1.getTime(), 1));
            }

            Thread.sleep(1000);

            List<BleRecord> records = bleRepo.getAllRecords();
            int counter = 1;
            for (BleRecord rec : records) {
                Date d = new Date(rec.getTs());
                Log.e("ble", ">> " + counter + " " + rec.getTs() + "," + sdf.format(d));
                counter += 1;
            }

            bleRepo.deleteEarlierThan(thresh);

            Thread.sleep(1000);

            records = bleRepo.getAllRecords();
            counter = 1;
            for(BleRecord rec : records) {
                Date d = new Date(rec.getTs());
                Log.e("ble","** "+counter+" "+rec.getTs()+","+sdf.format(d));
                counter+=1;
            }
        }
        catch(Exception e) {
            Log.e("ble",e.getMessage());
        }
    }

    public void gpsPurgeTest(long thresh) {
        GpsDbRecordRepository gpsRepo = new GpsDbRecordRepository(context);
        try {
            gpsRepo.deleteAll();
            Thread.sleep(1000);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (int i = 14; i <= 29; i++) {
                Date d1 = sdf.parse("2020-03-" + i);
                gpsRepo.insert(new GpsRecord(d1.getTime(), 100, 100, "", context));
            }

            Thread.sleep(1000);

            List<GpsRecord> records = gpsRepo.getAllRecords();
            int counter = 1;
            for (GpsRecord rec : records) {
                Date d = new Date(rec.getTs());
                Log.e("ble", ">> " + counter + " " + rec.getTs() + "," + sdf.format(d));
                counter += 1;
            }

            gpsRepo.deleteEarlierThan(thresh);

            Thread.sleep(1000);

            records = gpsRepo.getAllRecords();
            counter = 1;
            for(GpsRecord rec : records) {
                Date d = new Date(rec.getTs());
                Log.e("ble","** "+counter+" "+rec.getTs()+","+sdf.format(d));
                counter+=1;
            }
        }
        catch(Exception e) {
            Log.e("ble",e.getMessage());
        }
    }
}
