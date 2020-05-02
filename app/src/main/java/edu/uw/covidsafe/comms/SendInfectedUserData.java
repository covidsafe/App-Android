package edu.uw.covidsafe.comms;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.example.covidsafe.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import edu.uw.covidsafe.gps.GpsDbRecordRepository;
import edu.uw.covidsafe.gps.GpsRecord;
import edu.uw.covidsafe.gps.GpsUtils;
import edu.uw.covidsafe.json.SelfReportRequest;
import edu.uw.covidsafe.seed_uuid.SeedUUIDDbRecordRepository;
import edu.uw.covidsafe.seed_uuid.SeedUUIDRecord;
import edu.uw.covidsafe.ui.health.DiagnosisFragment;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.RegenerateSeedUponReport;
import edu.uw.covidsafe.utils.TimeUtils;
import edu.uw.covidsafe.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.List;

public class SendInfectedUserData extends AsyncTask<Void, Void, Void> {

    Activity av;
    Context context;
    View view;
    boolean status = false;

    public SendInfectedUserData(Context context, Activity av, View view) {
        this.context = context;
        this.av = av;
        this.view = view;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.e("sendbug","status "+status);
        if (status) {
            new RegenerateSeedUponReport(context).execute();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        SeedUUIDDbRecordRepository seedUUIDRepo = new SeedUUIDDbRecordRepository(context);
        List<SeedUUIDRecord> allRecords = seedUUIDRepo.getAllSortedRecords();
        Log.e("sendbug","record size "+allRecords.size());
        // getSeedAtBeginningOfInfectionWindow
        //infectionWindowInMinutes = 20160 . if infection window is 14 days and uuid generation time is 5 minutes
        //seedIndexAtBeginningOfInfectionWindow = 4032

        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        int infectionWindowInDays = 0;
        if (Constants.DEBUG) {
            infectionWindowInDays = prefs.getInt(context.getString(R.string.infection_window_in_days_pkeys), Constants.DefaultInfectionWindowInDaysDebug);
        }
        else {
            infectionWindowInDays = prefs.getInt(context.getString(R.string.infection_window_in_days_pkeys), Constants.DefaultInfectionWindowInDays);
        }

        int infectionWindowInMilliseconds = 1000*60*60*24*infectionWindowInDays;
        int UUIDGenerationIntervalInMiliseconds = Constants.UUIDGenerationIntervalInMinutes*60*1000;
        if (Constants.DEBUG) {
            UUIDGenerationIntervalInMiliseconds = Constants.UUIDGenerationIntervalInSecondsDebug*1000;
        }

        // find timestamp 14 days ago
        long timestampAtBeginningOfInfectionWindow = TimeUtils.getTime() - infectionWindowInMilliseconds -
                Constants.InfectionWindowIntervalDeviationInMilliseconds;

        Log.e("seeds","infectionWindowInMilliseconds "+infectionWindowInMilliseconds);
        Log.e("seeds","UUIDGenerationIntervalInMiliseconds "+UUIDGenerationIntervalInMiliseconds);
        Log.e("seeds","timestampAtBeginningOfInfectionWindow "+timestampAtBeginningOfInfectionWindow);

        // find 6 minutes past 14 days ago
        long timestampDeviation = timestampAtBeginningOfInfectionWindow +
                UUIDGenerationIntervalInMiliseconds +
                Constants.InfectionWindowIntervalDeviationInMilliseconds;

        Log.e("demo","get records between "+timestampAtBeginningOfInfectionWindow+","+timestampDeviation);
        SeedUUIDRecord generatedRecord = seedUUIDRepo.getRecordBetween(
                timestampAtBeginningOfInfectionWindow,
                timestampDeviation);

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm.ss");
        Log.e("demo",format.format(timestampAtBeginningOfInfectionWindow));
        Log.e("demo",format.format(timestampDeviation));
        if (generatedRecord!=null) {
            Log.e("demo", "gen seed between: " + generatedRecord.getSeed(context));
            Log.e("demo", "gen timestamp between: " + format.format(generatedRecord.getRawTs()));
        }
        else {
            Log.e("demo","gen record is null");
        }
        Log.e("demo","first seed is "+allRecords.get(allRecords.size()-1).getSeed(context));
        Log.e("demo","first seed timestamp is "+format.format(allRecords.get(allRecords.size()-1).getRawTs()));

//        for (SeedUUIDRecord seed : allRecords) {
//            Log.e("seeds",format.format(seed.getRawTs()) + " ---- "+seed.getSeed(context));
//        }

        // if user has less than 14 days of records, just get the earliest record
        SeedUUIDRecord recordToSend = generatedRecord;
        if (generatedRecord == null) {
            Log.e("demo","get zeroth seed");
            if (allRecords.size() == 0) {
                Utils.mkSnack(av, view, av.getString(R.string.gen_error));
                status = false;
                return null;
            }
            recordToSend = allRecords.get(allRecords.size()-1);
            Log.e("demo","allrecords size "+allRecords.size());
            Log.e("demo","zeroth seed is "+recordToSend.getSeed(context));
        }

        //get most recent GPS entry as coarse location and send it
        GpsDbRecordRepository gpsRepo = new GpsDbRecordRepository(context);
        List<GpsRecord> sortedGpsRecords = gpsRepo.getSortedRecordsSync();

        double lat = 0;
        double longi = 0;

        if (sortedGpsRecords.size() == 0) {
            Log.e("sendbug","need to get last location");
            if (!Utils.hasGpsPermissions(context)) {
                Utils.mkSnack(av, view, context.getString(R.string.turn_loc_on));
                status = false;
                return null;
            }
            else {
                Location loc = GpsUtils.getLastLocation(context);
                if (loc == null) {
                    Utils.mkSnack(av, view, context.getString(R.string.turn_loc_on));
                    status = false;
                    return null;
                }
                lat = loc.getLatitude();
                longi = loc.getLongitude();
            }
        }
        else {
            Log.e("sendbug","getting last gps record");
            GpsRecord gpsRecord = sortedGpsRecords.get(0);
            lat = gpsRecord.getLat(context);
            longi = gpsRecord.getLongi(context);
            Log.e("ERR ", gpsRecord.getLat(context) + "," + gpsRecord.getLongi(context));
        }

        int gpsResolution = Constants.MaximumGpsPrecision;
        try {
            // ask healthies to generate from ts_start till now (when infected publishes data)
            long ts_start = recordToSend.ts;
            long ts_end = TimeUtils.getTime();

            String seed = recordToSend.getSeed(context);
            double coarseLat = getCoarseGpsCoord(lat, gpsResolution);
            double coarseLon = getCoarseGpsCoord(longi, gpsResolution);
            Log.e("sendbug","seed "+seed);
            Log.e("sendbug","ts_start "+ts_start);
            Log.e("sendbug","ts_end "+ts_end);
            Log.e("sendbug","coarseLat "+coarseLat);
            Log.e("sendbug","coarseLon "+coarseLon);
            Log.e("sendbug","preciseLat "+lat);
            Log.e("sendbug","preciseLon "+longi);
            sendRequest(seed,
                    ts_start, ts_end,
                    coarseLat,coarseLon,
                    gpsResolution);

            Log.e("sendbug","trace data submitted");
        }
        catch(Exception e) {
            Log.e("err",e.getMessage());
        }

        return null;
    }

    public int log(long x, int base) {
        return (int) (Math.log(x) / Math.log(base));
    }

    public double getCoarseGpsCoord(double d, int precision) {
        long bits = Double.doubleToLongBits(d);

        long negative = bits & (1L << 63);
        int exponent = (int)((bits >> 52) & 0x7ffL);
        long mantissa = bits & 0xfffffffffffffL;

        int mantissaLog = 52;
        if (exponent == 0) {
            mantissaLog = (int)log(mantissa, 2);
        }
        else {
            mantissa = mantissa | (1L<<52);
        }

        int precisionShift = mantissaLog + exponent - 1075;

        int maskLength = Math.min(precision + precisionShift, 52);

        mantissa = mantissa >> (52 - maskLength);
        mantissa = mantissa << (52 - maskLength);

        if (mantissa == 0)
        {
            exponent = 0;
        }
        long result = negative |
                ((long)(exponent & 0x7ffL) << 52) |
                (mantissa & 0xfffffffffffffL);

        return Double.longBitsToDouble(result);
    }

    public void sendRequest(String seed, long ts_start, long ts_end, double lat, double longi, int precision) {
        if (ts_start > ts_end) {
            ts_end = ts_start;
        }

        JSONObject announceRequestObj = null;
        try {
             announceRequestObj =
                     SelfReportRequest.toJson(new String[]{seed},
                             new long[]{ts_start}, new long[]{ts_end}, lat, longi, precision);
        }
        catch(Exception e) {
            Log.e("err",e.getMessage());
        }
        if (announceRequestObj == null) {
            status = false;
            return;
        }

        String selfReportRequest = SelfReportRequest.toHttpString();
        Log.e("net", "send request to "+selfReportRequest);
        try {
            Log.e("net", "payload " + announceRequestObj.toString(2));
        }
        catch(Exception e) {
            Log.e("net",e.getMessage());
            status = false;
        }

        // if status code == 200, this will return at worst an empty json object
        // if resp is null, the status code != 200
        JSONObject resp = NetworkHelper.sendRequest(selfReportRequest, Request.Method.PUT, announceRequestObj);
        try {
            if (resp == null || (resp.has("statusCode") && resp.getInt("statusCode") != 200)) {
                Utils.mkSnack(av, view, context.getString(R.string.error_submitting_data));
                status = false;
                return;
            }
            else {
                Utils.mkSnack(av, view, context.getString(R.string.report_has_been_submitted));

                DiagnosisFragment.updateSubmissionView(av, context, view, true);
                status = true;
                return;
            }
        }
        catch(Exception e) {
            Log.e("status",e.getMessage());
            status = false;
            return;
        }
    }

    public void testDatabase() {
        SeedUUIDDbRecordRepository seedUUIDRepo = new SeedUUIDDbRecordRepository(context);
        seedUUIDRepo.deleteAll();
        long[] tss = new long[]{1585724400000L,1585638000000L,1585551600000L,1585465200000L,1585378800000L,1585292400000L,1585206000000L,1585119600000L,1585033200000L,1584946800000L,1584860400000L,1584774000000L,1584687600000L,1584601200000L,1584514800000L,1584428400000L,};
        for (Long l : tss) {
            seedUUIDRepo.insert(new SeedUUIDRecord(l, "","", context));
        }

        SeedUUIDRecord generatedRecord = seedUUIDRepo.getRecordBetween(
                1585551600000L,
                1585638000000L);

        SeedUUIDRecord generatedRecord2 = seedUUIDRepo.getRecordBetween(
                1585810800000L,
                1585897200000L);
    }
}
