package edu.uw.covidsafe.comms;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.example.covidsafe.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.covidsafe.gps.GpsUtils;
import edu.uw.covidsafe.json.Area;
import edu.uw.covidsafe.ble.BleDbRecordRepository;
import edu.uw.covidsafe.ble.BleRecord;
import edu.uw.covidsafe.gps.GpsDbRecordRepository;
import edu.uw.covidsafe.gps.GpsRecord;
import edu.uw.covidsafe.json.AreaMatch;
import edu.uw.covidsafe.json.BlueToothSeed;
import edu.uw.covidsafe.json.BluetoothMatch;
import edu.uw.covidsafe.json.MatchMessage;
import edu.uw.covidsafe.json.MessageListRequest;
import edu.uw.covidsafe.json.MessageListResponse;
import edu.uw.covidsafe.json.MessageRequest;
import edu.uw.covidsafe.json.MessageSizeRequest;
import edu.uw.covidsafe.json.MessageSizeResponse;
import edu.uw.covidsafe.ui.notif.NotifOpsAsyncTask;
import edu.uw.covidsafe.ui.notif.NotifRecord;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.CryptoUtils;
import edu.uw.covidsafe.utils.TimeUtils;
import edu.uw.covidsafe.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PullFromServerTask extends AsyncTask<Void, Void, Void> {

    Context context;
    Activity av;
    View view;

    public PullFromServerTask(Context context, Activity av, View view) {
        Constants.PullFromServerTaskRunning = true;
        this.context = context;
        this.av = av;
        this.view = view;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.e("refresh","post execute");
        super.onPostExecute(aVoid);
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        long ts = TimeUtils.getTime();
        editor.putLong(context.getString(R.string.last_refresh_date_pkey), ts);
        editor.commit();

        if (view != null) {
            SwipeRefreshLayout swipeLayout = view.findViewById(R.id.swiperefresh);
            swipeLayout.setRefreshing(false);
            ImageView refresh = view.findViewById(R.id.refresh);
            refresh.clearAnimation();
            TextView lastUpdated = view.findViewById(R.id.lastUpdated);
            SimpleDateFormat format = new SimpleDateFormat("h:mm a");
            lastUpdated.setText(context.getString(R.string.last_updated_text)+": " + format.format(new Date(ts)));
            lastUpdated.setVisibility(View.VISIBLE);
        }
        Constants.PullFromServerTaskRunning = false;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.e("pull", "PULL FROM SERVER");
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);

        //////////////////////////////////////////////////////////////////////////////////////////
        // send coarse -> finer grained gps locations, find size of seeds on server
        //////////////////////////////////////////////////////////////////////////////////////////
        GpsDbRecordRepository gpsRepo = new GpsDbRecordRepository(context);
        List<GpsRecord> gpsRecords = gpsRepo.getSortedRecordsSync();

        double lat = 0;
        double lon = 0;
        if (gpsRecords.size() == 0) {
            if (Utils.hasGpsPermissions(context)) {
                Location loc = GpsUtils.getLastLocation(context);
                if (loc != null) {
                    lat = loc.getLatitude();
                    lon = loc.getLongitude();
                }
            }

            if (lat == 0 && lon == 0) {
                Log.e("pull", "no gps locations, returning");
                return null;
            }
        }
        else {
            GpsRecord gpsRecord = gpsRecords.get(0);
            lat = gpsRecord.getLat(context);
            lon = gpsRecord.getLongi(context);
        }

        int currentGpsPrecision = Constants.MinimumGpsPrecision;

        int sizeOfPayload = 0;
        long lastQueryTime = prefs.getLong(context.getString(R.string.time_of_last_query_pkey), 0L);
        if (lastQueryTime == 0) {
            lastQueryTime = TimeUtils.getTime();
        }
        while (currentGpsPrecision <= Constants.MaximumGpsPrecision) {
            double preciseLat = Utils.getCoarseGpsCoord(lat, currentGpsPrecision);
            double preciseLong = Utils.getCoarseGpsCoord(lon, currentGpsPrecision);

            try {
                Log.e("pull ","HOW BIG "+currentGpsPrecision);
                sizeOfPayload = howBig(preciseLat, preciseLong,
                        currentGpsPrecision, lastQueryTime);
                if (sizeOfPayload < 0) {
                    // something wrong with the request
                    break;
                }
                Log.e("pull ","size of payload "+sizeOfPayload);
            }
            catch(Exception e) {
                Log.e("err",e.getMessage());
            }
            if (sizeOfPayload > Constants.MaxPayloadSize && currentGpsPrecision != Constants.MaximumGpsPrecision) {
                currentGpsPrecision += 1;
            }
            else {
                break;
            }
        }

        if (sizeOfPayload <= 0 || sizeOfPayload > Constants.MaxPayloadSize) {
            // potentially too many messages, set the last query time to now.
            // retry at another time
            lastQueryTime = TimeUtils.getTime();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(context.getString(R.string.time_of_last_query_pkey), lastQueryTime);
            editor.commit();

            return null;
        }

        //////////////////////////////////////////////////////////////////////////////////////////
        // get list of UUIDs that intersect with our movements and what the server has sent us
        //////////////////////////////////////////////////////////////////////////////////////////
        double preciseLat = Utils.getCoarseGpsCoord(lat, currentGpsPrecision);
        double preciseLong = Utils.getCoarseGpsCoord(lon, currentGpsPrecision);

        Log.e("pull ","GET MESSAGES "+sizeOfPayload);
        List<BluetoothMatch> bluetoothMatches = getMessages(preciseLat,preciseLong,
                currentGpsPrecision, lastQueryTime);
        if (bluetoothMatches == null || bluetoothMatches.size() == 0) {
            return null;
        }

        //////////////////////////////////////////////////////////////////////////////////////////
        // cache our own scanned bluetooth IDs
        //////////////////////////////////////////////////////////////////////////////////////////
        // TODO: set intersection between ble IDs and received IDs, don't do a linear for loop
        // check that the set intersection will work.
        BleDbRecordRepository repo = new BleDbRecordRepository(context);
        List<BleRecord> bleRecords = repo.getAllRecords();
        HashMap<String,List<Long>> scannedBleMap = new HashMap<>();
        for (BleRecord bleRecord : bleRecords) {
            Log.e("ble",bleRecord.toString());
            if (!scannedBleMap.containsKey(bleRecord.getUuid())) {
                scannedBleMap.put(bleRecord.getUuid(), new LinkedList<Long>());
            }
            scannedBleMap.get(bleRecord.getUuid()).add(bleRecord.getTs());
        }

        //////////////////////////////////////////////////////////////////////////////////////////
        // go through receivedbluetooth matches
        //////////////////////////////////////////////////////////////////////////////////////////
        Set<String> seenSeeds = new HashSet<>();
        Map<String,Long> startTimes = new HashMap<>();
        Map<String,Long> endTimes = new HashMap<>();
        Map<String,String> userMessage = new HashMap<>();

        for (BluetoothMatch bluetoothMatch : bluetoothMatches) {
            for (BlueToothSeed seed : bluetoothMatch.seeds) {
                if (!seenSeeds.contains(seed.seed)) {
                    seenSeeds.add(seed.seed);
                    startTimes.put(seed.seed, seed.sequenceStartTime);
                    endTimes.put(seed.seed,seed.sequenceEndTime);
                    userMessage.put(seed.seed, bluetoothMatch.userMessage);
                }
                else {
                    if (seed.sequenceEndTime > endTimes.get(seed.seed)) {
                        startTimes.put(seed.seed, seed.sequenceStartTime);
                        endTimes.put(seed.seed,seed.sequenceEndTime);
                        userMessage.put(seed.seed, bluetoothMatch.userMessage);
                    }
                }
            }
        }

        List<String> exposedMessages = new ArrayList<>();
        List<Long> contactStartTimes = new ArrayList<>();
        List<Long> contactEndTimes = new ArrayList<>();
        for (String seed : seenSeeds) {
            Log.e("pull","SEED "+seed);
            if (seed.equals("c2db5cac-9875-4ad7-acc7-ead49c76d1ec")) {
                Log.e("pull","got seed");
            }
            long[] exposedStatus = isExposed(seed,
                    startTimes.get(seed),
                    endTimes.get(seed),
                    scannedBleMap);
            if (exposedStatus != null) {
                seenSeeds.add(seed);
                exposedMessages.add(userMessage.get(seed));
                contactStartTimes.add(exposedStatus[0]);
                contactEndTimes.add(exposedStatus[1]);
            }
        }

        notifyBulk(Constants.MessageType.Exposure, exposedMessages, contactStartTimes, contactEndTimes);
        return null;
    }

    // sync blockig op
    public int howBig(double lat, double longi, int precision, long ts) throws JSONException {
        String messageSizeRequest = MessageSizeRequest.toHttpString(lat, longi, precision, ts);
        JSONObject jsonResp = NetworkHelper.sendRequest(messageSizeRequest, Request.Method.HEAD, null);
        if (jsonResp == null || (jsonResp.has("statusCode") && jsonResp.getInt("statusCode") != 200)) {
            return -1;
        }
        MessageSizeResponse messageSizeResponse = MessageSizeResponse.parse(jsonResp);
        return messageSizeResponse.sizeOfQueryResponse;
    }

    public List<BluetoothMatch> getMessages(double lat, double longi, int precision, long lastQueryTime) {
        // return list of seeds and timestamps
        // check if the areas returned in these messages match our GPS timestamps

        /////////////////////////////////////////////////////////////////////////
        // (1) send MessageListRequest to get query IDs and timestamps
        /////////////////////////////////////////////////////////////////////////
        String messageListRequest = MessageListRequest.toHttpString(lat, longi, precision, lastQueryTime);
        Log.e("NET ","SEND MESSAGE LIST REQUEST ");
        JSONObject response = NetworkHelper.sendRequest(messageListRequest, Request.Method.GET,null);
        try {
            if (response == null || (response.has("statusCode") && response.getInt("statusCode") != 200)) {
                return null;
            }
        }
        catch(Exception e) {
            Log.e("err",e.getMessage());
            return null;
        }

        MessageListResponse messageListResponse = null;
        try {
            messageListResponse = MessageListResponse.parse(response);
        }
        catch (Exception e) {
            Log.e("err",e.getMessage());
            return null;
        }

        /////////////////////////////////////////////////////////////////////////
        // (2) make a request for the queries using the IDs
        /////////////////////////////////////////////////////////////////////////
        JSONObject messageRequestObj = null;
        try {
            messageRequestObj = MessageRequest.toJson(messageListResponse.messageInfo);
        }
        catch(Exception e) {
            Log.e("err",e.getMessage());
            return null;
        }
        if (messageRequestObj == null) {
            return null;
        }

        String messageRequest = MessageRequest.toHttpString();
        Log.e("NET ","MESSAGE REQUEST num of messages: "+messageListResponse.messageInfo.length);
        Log.e("NET ","MESSAGE REQUEST payload: "+messageRequestObj.toString());
        response = NetworkHelper.sendRequest(messageRequest, Request.Method.POST, messageRequestObj);
        try {
            if (response == null || (response.has("statusCode") && response.getInt("statusCode") != 200)) {
                return null;
            }
        }
        catch(Exception e) {
            Log.e("err",e.getMessage());
            return null;
        }

        MatchMessage[] matchMessages = null;
        try {
            JSONArray matchMessagesArr = response.getJSONArray("results");
            matchMessages = new MatchMessage[matchMessagesArr.length()];
            for (int i = 0; i < matchMessagesArr.length(); i++) {
                matchMessages[i] = MatchMessage.parse(matchMessagesArr.getJSONObject(i));
            }
        }
        catch (Exception e) {
            Log.e("err",e.getMessage());
            return null;
        }
        if (matchMessages == null || matchMessages.length == 0) {
            return null;
        }

        /////////////////////////////////////////////////////////////////////////
        // (3) update last query time to server
        /////////////////////////////////////////////////////////////////////////
//        ArrayList<Long> queryTimes = new ArrayList<Long>();
//        for (MessageInfo messageInfo : messageListResponse.messageInfo) {
//            queryTimes.add(messageInfo.MessageTimestamp);
//        }
//        Collections.sort(queryTimes, Collections.reverseOrder());

        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (messageListResponse.maxResponseTimestamp > 0) {
            editor.putLong(context.getString(R.string.time_of_last_query_pkey), messageListResponse.maxResponseTimestamp);
            editor.commit();
        }
        /////////////////////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////////////////////
        // (4) narrowcast messages: check for area intersection and record matched messages
        /////////////////////////////////////////////////////////////////////////
        List<String> narrowCastMessages = new ArrayList<String>();
        List<Long> narrowCastMessageStartTimes = new ArrayList<>();
        List<Long> narrowCastMessageEndTimes = new ArrayList<>();
        for (int i = 0; i < matchMessages.length; i++) {
            if (matchMessages[i].areaMatches != null) {
                for (AreaMatch areaMatch : matchMessages[i].areaMatches) {
                    Area[] areas = areaMatch.areas;
                    for (Area area : areas) {
                        if (intersect(area)) {
                            Log.e("msg", "NARROWCAST USER MESSAGE " + areaMatch.userMessage);
                            narrowCastMessages.add(areaMatch.userMessage);
                            narrowCastMessageStartTimes.add(area.beginTime);
                            narrowCastMessageEndTimes.add(area.endTime);
                            break;
                        }
                    }
                }
            }
        }
        Log.e("msg","notify bulk narrowcast "+narrowCastMessages.size());
        notifyBulk(Constants.MessageType.NarrowCast, narrowCastMessages,narrowCastMessageStartTimes,narrowCastMessageEndTimes);
        /////////////////////////////////////////////////////////////////////////

        List<BluetoothMatch> bluetoothMatches = new ArrayList<>();
        for (int i = 0; i < matchMessages.length; i++) {
            for (BluetoothMatch match : matchMessages[i].bluetoothMatches) {
                bluetoothMatches.add(match);
            }
        }

        return bluetoothMatches;
    }

    public boolean intersect(Area area) {
        GpsDbRecordRepository gpsRepo = new GpsDbRecordRepository(context);
        List<GpsRecord> gpsRecords = gpsRepo.getRecordsBetweenTimestamps(area.beginTime, area.endTime);
        if (gpsRecords.size() == 0) {
            if (!Utils.hasGpsPermissions(context)) {
                if (view != null) {
                    mkSnack(av, view, context.getString(R.string.turn_loc_on2));
                }
                return false;
            }
            else {
                Location loc = GpsUtils.getLastLocation(context);
                if (loc == null) {
                    return false;
                }
                gpsRecords.add(new GpsRecord(0,loc.getLatitude(),loc.getLongitude(),"", context));
            }
        }

        for (GpsRecord record : gpsRecords) {
            float[] result = new float[3];
            Location.distanceBetween(record.getLat(context), record.getLongi(context), area.location.latitude, area.location.longitude, result);

            if ((result.length == 1 && result[0] < area.radiusMeters) ||
                (result.length == 2 && result[1] < area.radiusMeters) ||
                (result.length >= 3 && result[2] < area.radiusMeters)) {
                return true;
            }
        }
        return false;
    }

    // we get a seed and timestamp from the server for each infected person
    // check if we intersect with the infected person
    public static long[] isExposed(String seed, long start_ts, long end_ts, HashMap<String,List<Long>> scannedBleMap) {
        // if timestamp is in the future, something is wrong, return
        if (start_ts > TimeUtils.getTime() || end_ts > TimeUtils.getTime() || end_ts < start_ts) {
            return null;
        }
        // convert our BLE DB records into convenient data structures
        // place the UUIDs into a map for easy lookup and checking

        // determine how many UUIDs to generate from the seed
        // based on time when the seed was generated and now.
        int diffBetweenNowAndTsInMinutes = 0;

        if (end_ts == 0 || end_ts < 0 || end_ts == start_ts) {
            diffBetweenNowAndTsInMinutes = (int) ((TimeUtils.getTime() - start_ts) / 1000 / 60);
        }
        else {
            diffBetweenNowAndTsInMinutes = (int) ((end_ts - start_ts) / 1000 / 60);
        }
        int numSeedsToGenerate = diffBetweenNowAndTsInMinutes / Constants.UUIDGenerationIntervalInMinutes;

        // if we need to generate too many timestamps, something is wrong, return.
        int infectionWindowInMinutes = (Constants.DefaultInfectionWindowInDays *24*60);
        int maxSeedsToGenerate = infectionWindowInMinutes / Constants.UUIDGenerationIntervalInMinutes;
        if (numSeedsToGenerate > maxSeedsToGenerate) {
            return null;
        }
        // generate all the seeds
        List<String> receivedUUIDs = CryptoUtils.chainGenerateUUIDFromSeed(seed, numSeedsToGenerate);

        // iterate through received IDs and see if they are in list of IDs which we have scanned
        // if so, check if those IDs had timestamps within some interval...
        // this interval has to be BluetoothScanIntervalInMinutes
        // user A can broadcast an ID at timestamp t
        // user B may only wake up to scan the ID after BluetoothScanIntervalInMinutes
        List<Long> matches = new ArrayList<>();
        int bluetoothScanIntervalInMilliseconds = Constants.BluetoothScanIntervalInMinutes*60000;
//        int uuidGenerationIntervalInMillliseconds = Constants.UUIDGenerationIntervalInMinutes*60000;

        for (String receivedUUID : receivedUUIDs) {
            if (scannedBleMap.keySet().contains(receivedUUID)) {
                for (Long localTs : scannedBleMap.get(receivedUUID)) {
                    // check that the timestamps were within the same UUID generation interval
//                    if (Math.abs(localTs - start_ts) < uuidGenerationIntervalInMillliseconds) {
                        // record the timestamp when the local scanner picked it up
                        matches.add(localTs);
//                    }
                }
            }
//            start_ts += uuidGenerationIntervalInMillliseconds;
        }

        // calculate how many matches we need to say user is exposed for at least 10 minutes
        // return false if there simply not enough matches to determine this.
        // this is 2
        int numConsecutiveMatchesNeeded = (Constants.CDCExposureTimeInMinutes/Constants.BluetoothScanIntervalInMinutes);
        if (matches.size() < numConsecutiveMatchesNeeded) {
            return null;
        }
        else {
            return new long[]{start_ts,end_ts};
        }

//        // take diff of timestamps when we had a UUID match
//        List<Integer> diff = new ArrayList<Integer>();
//        for (int i = 0; i < matches.size()-1; i++) {
//            int d = (int)(matches.get(i+1)-matches.get(i));
//            if (d < 0) {
//                // something is wrong, return
//                return null;
//            }
//            diff.add(d);
//        }
//
//        // counter tracks how many occurrences of
//        // uuidGenerationIntervalInMillliseconds we have in a row
//        int streak = 0;
//
//        //check that we have at least numConsecutiveMatchesNeeded
//        //matches of uuidGenerationIntervalInMillliseconds
//        List<Long> contactTimesStart = new ArrayList<>();
//        List<Long> contactTimesEnd = new ArrayList<>();
//        for (int i = 0; i < diff.size(); i++) {
//            if (Math.abs(diff.get(i)-bluetoothScanIntervalInMilliseconds)
//                <= Constants.TimestampDeviationInMilliseconds) {
//                streak += 1;
//                // add contact time once for the streak
//                if (streak == numConsecutiveMatchesNeeded) {
//                    int idx = i-(streak-1);
//                    contactTimesStart.add(matches.get(idx));
//                }
//            }
//            else {
//                // a streak just ended
//                if (contactTimesEnd.size() != contactTimesStart.size()) {
//                    contactTimesEnd.add(matches.get(streak));
//                }
//                streak = 0;
//            }
//        }
//        if (contactTimesEnd.size() != contactTimesStart.size()) {
//            contactTimesEnd.add(matches.get(streak));
//        }
//
//        if (contactTimesStart.size() == 0) {
//            return null;
//        }
//
//        long maxContactTime = 0;
//        int maxContactTimeIdx = -1;
//        for (int i = 0; i < contactTimesStart.size(); i++) {
//            if (contactTimesEnd.get(i)-contactTimesStart.get(i) > maxContactTime) {
//                maxContactTime = contactTimesEnd.get(i)-contactTimesStart.get(i);
//                maxContactTimeIdx = i;
//            }
//        }
//
//        if (maxContactTimeIdx == -1) {
//            return null;
//        }
//
//        return new long[]{contactTimesStart.get(maxContactTimeIdx),
//                        contactTimesEnd.get(maxContactTimeIdx)};
    }

//    public void notifyUserOfExposure(String msg) {
//        King County COVID-19 call center: 206-477-3977. Open daily from 8 a.m. to 7 p.m
//        Washington State COVID-19 call center: 800-525-0127
//        https://scanpublichealth.org/faq
//        Log.e("msg", "BLUETOOTH USER MESSAGE "+msg);
//    }

    public void notifyBulk(Constants.MessageType messageType,
                           List<String> msgs,
                           List<Long> contactTimesStart,
                           List<Long> contactTimesEnd) {
        // PSA
        for (int i = 0; i < msgs.size(); i++) {
            // add notification to DB
            if (messageType == Constants.MessageType.Exposure) {
                String msg = msgs.get(i);
                if (msg.isEmpty()) {
                    msg = context.getString(R.string.default_exposed_notif);
                }
                new NotifOpsAsyncTask(context, new NotifRecord(
                        contactTimesStart.get(i),
                        contactTimesEnd.get(i),
                        msg,
                        messageType.ordinal(),
                        true)).execute();
                Utils.sendNotification(context, context.getString(R.string.exposed),msg, R.drawable.warning2);
            }
            else {
                if (!msgs.isEmpty()) {
                    new NotifOpsAsyncTask(context, new NotifRecord(
                            contactTimesStart.get(i),
                            contactTimesEnd.get(i),
                            msgs.get(i),
                            messageType.ordinal(),
                            true)).execute();
                    Utils.sendNotification(context, context.getString(R.string.announcement_txt), msgs.get(i), R.drawable.ic_info_outline_black_24dp);
                }
            }
        }
    }

    public static void mkSnack(Activity av, View v, String msg) {
        if (av != null) {
            av.runOnUiThread(new Runnable() {
                public void run() {
                    SpannableStringBuilder builder = new SpannableStringBuilder();
                    builder.append(msg);
                    Snackbar snackBar = Snackbar.make(v, builder, Snackbar.LENGTH_LONG);

                    snackBar.setAction(av.getString(R.string.dismiss_text), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackBar.dismiss();
                        }
                    });

                    View snackbarView = snackBar.getView();
                    TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                    textView.setMaxLines(5);

                    snackBar.show();
                }
            });
        }
    }
}
