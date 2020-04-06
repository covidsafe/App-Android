package edu.uw.covidsafe.comms;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import edu.uw.covidsafe.gps.GpsDbRecordRepository;
import edu.uw.covidsafe.gps.GpsRecord;
import edu.uw.covidsafe.json.SelfReportRequest;
import edu.uw.covidsafe.seed_uuid.SeedUUIDDbRecordRepository;
import edu.uw.covidsafe.seed_uuid.SeedUUIDRecord;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

import java.util.List;

public class SendInfectedUserData extends AsyncTask<Void, Void, Void> {

    Activity av;
    Context context;
    View view;

    public SendInfectedUserData(Context context, Activity av, View view) {
        this.context = context;
        this.av = av;
        this.view = view;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... params) {
        SeedUUIDDbRecordRepository seedUUIDRepo = new SeedUUIDDbRecordRepository(context);
        // getSeedAtBeginningOfInfectionWindow
        //infectionWindowInMinutes = 20160 . if infection window is 14 days and uuid generation time is 5 minutes
        //seedIndexAtBeginningOfInfectionWindow = 4032
        int infectionWindowInMilliseconds = 1000*60*60*24*Constants.InfectionWindowInDays;
        int UUIDGenerationIntervalInMiliseconds = Constants.UUIDGenerationIntervalInMinutes*60*1000;

        // find timestamp 14 days ago
        long timestampAtBeginningOfInfectionWindow = System.currentTimeMillis() - infectionWindowInMilliseconds;
        // find 6 minutes past 14 days ago
        long timestampDeviation = timestampAtBeginningOfInfectionWindow +
                UUIDGenerationIntervalInMiliseconds +
                Constants.InfectionWindowIntervalDeviationInMilliseconds;

        SeedUUIDRecord generatedRecord = seedUUIDRepo.getRecordBetween(
                timestampAtBeginningOfInfectionWindow,
                timestampDeviation);

        // if user has less than 14 days of records, just get the earliest record
        SeedUUIDRecord recordToSend = generatedRecord;
        if (generatedRecord == null) {
            List<SeedUUIDRecord> allRecords = seedUUIDRepo.getAllSortedRecords();
            recordToSend = allRecords.get(0);
        }

        //get most recent GPS entry as coarse location and send it
        GpsDbRecordRepository gpsRepo = new GpsDbRecordRepository(context);
        List<GpsRecord> sortedGpsRecords = gpsRepo.getSortedRecords();

        double lat = 0;
        double longi = 0;

        if (sortedGpsRecords.size() == 0) {
//            Location loc = GpsUtils.getLastLocation();
//            lat = loc.getLatitude();
//            longi = loc.getLongitude();
            if (Utils.hasGpsPermissions(context)) {
                mkSnack(av, view, "We need location services enabled to send your traces. Please enable location services permission.");
            }
        }
        else {
            GpsRecord gpsRecord = sortedGpsRecords.get(0);
            lat = gpsRecord.getLat();
            longi = gpsRecord.getLongi();
            Log.e("ERR ", gpsRecord.getLat() + "," + gpsRecord.getLongi());
        }

        int gpsResolution = Constants.MaximumGpsPrecision;
        try {
            // ask healthies to generate from ts_start till now (when infected publishes data)
            long ts_start = recordToSend.ts;
            long ts_end = System.currentTimeMillis();
            sendRequest(recordToSend.seed,
                    ts_start, ts_end,
                    getCoarseGpsCoord(lat, gpsResolution),
                    getCoarseGpsCoord(longi, gpsResolution),
                    gpsResolution);
        }
        catch(Exception e) {
            Log.e("err",e.getMessage());
        }

        return null;
    }

    public static void mkSnack(Activity av, View v, String msg) {
        final Snackbar snackBar = Snackbar.make(v, msg, Snackbar.LENGTH_LONG);

        snackBar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        });

//        SpannableStringBuilder builder = new SpannableStringBuilder();
//        builder.append("My message ").append(" ");
//        builder.setSpan(new ImageSpan(MainActivity.this, R.drawable.ic_launcher), builder.length() - 1, builder.length(), 0);
//        builder.append(" next message");
//        Snackbar.make(parent view, builder, Snackbar.LENGTH_LONG).show();]

        View snackbarView = snackBar.getView();
//        TextView textView = (TextView)snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
//        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
//        textView.setCompoundDrawablePadding(av.getResources().getDimensionPixelOffset(R.dimen.ic));

        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setMaxLines(5);

        snackBar.show();
    }

    public int log(long x, int base) {
        return (int) (Math.log(x) / Math.log(base));
    }

    public double getCoarseGpsCoord(double d, int precision) {
//        Log.e("ERR ",d+","+precision);
        long bits = Double.doubleToLongBits(d);
//        Log.e("ERR ",d+","+precision);

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
            return;
        }

        String announceRequest = SelfReportRequest.toHttpString();
        JSONObject resp = NetworkHelper.sendRequest(announceRequest, Request.Method.PUT, announceRequestObj);

        edu.uw.covidsafe.json.Status status = null;
        try {
            status = edu.uw.covidsafe.json.Status.parse(resp);
        }
        catch(Exception e) {
            Log.e("err",e.getMessage());
        }

        if (resp != null) {
            mkSnack(av, view, "Your trace data has been submitted.");
        }
        else {
            mkSnack(av, view, "There was an error with submitting your traces. Please try again later.");
        }
    }

    public void testDatabase() {
        SeedUUIDDbRecordRepository seedUUIDRepo = new SeedUUIDDbRecordRepository(context);
        seedUUIDRepo.deleteAll();
        long[] tss = new long[]{1585724400000L,1585638000000L,1585551600000L,1585465200000L,1585378800000L,1585292400000L,1585206000000L,1585119600000L,1585033200000L,1584946800000L,1584860400000L,1584774000000L,1584687600000L,1584601200000L,1584514800000L,1584428400000L,};
        for (Long l : tss) {
            seedUUIDRepo.insert(new SeedUUIDRecord(l, "",""));
        }

        SeedUUIDRecord generatedRecord = seedUUIDRepo.getRecordBetween(
                1585551600000L,
                1585638000000L);

        SeedUUIDRecord generatedRecord2 = seedUUIDRepo.getRecordBetween(
                1585810800000L,
                1585897200000L);
    }
}
