package edu.uw.covidsafe.comms;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.example.covidsafe.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.covidsafe.json.Area;
import edu.uw.covidsafe.ble.BleDbRecordRepository;
import edu.uw.covidsafe.ble.BleRecord;
import edu.uw.covidsafe.gps.GpsDbRecordRepository;
import edu.uw.covidsafe.gps.GpsRecord;
import edu.uw.covidsafe.json.AreaMatch;
import edu.uw.covidsafe.json.BlueToothSeed;
import edu.uw.covidsafe.json.BluetoothMatch;
import edu.uw.covidsafe.json.MatchMessage;
import edu.uw.covidsafe.json.MessageInfo;
import edu.uw.covidsafe.json.MessageListRequest;
import edu.uw.covidsafe.json.MessageListResponse;
import edu.uw.covidsafe.json.MessageRequest;
import edu.uw.covidsafe.json.MessageSizeRequest;
import edu.uw.covidsafe.json.MessageSizeResponse;
import edu.uw.covidsafe.ui.notif.NotifOpsAsyncTask;
import edu.uw.covidsafe.ui.notif.NotifRecord;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.CryptoUtils;
import edu.uw.covidsafe.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class PullFromServerTaskDemo implements Runnable {

    Context context;

    public PullFromServerTaskDemo(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        Log.e("uuid", "PULL FROM SERVER");

        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);

        //////////////////////////////////////////////////////////////////////////////////////////
        // send coarse -> finer grained gps locations, find size of seeds on server
        //////////////////////////////////////////////////////////////////////////////////////////
//        GpsDbRecordRepository gpsRepo = new GpsDbRecordRepository(context);
//        List<GpsRecord> gpsRecords = gpsRepo.getSortedRecords();
//        if (gpsRecords.size() == 0) {
//            Log.e("pull","no gps locations, returning");
//            Constants.PullServiceRunning = false;
//            return;
//        }
//        GpsRecord gpsRecord = gpsRecords.get(0);
        GpsRecord gpsRecord = new GpsRecord(System.currentTimeMillis(),47.625,-122.25,"");
        int currentGpsPrecision = Constants.MaximumGpsPrecision;

        int sizeOfPayload = 0;
//        long lastQueryTime = prefs.getLong(context.getString(R.string.time_of_last_query_pkey), 0L);
        long lastQueryTime = 0;
//        while (currentGpsPrecision < Constants.MaximumGpsPrecision) {
            double preciseLat = Utils.getCoarseGpsCoord(gpsRecord.getLat(), currentGpsPrecision);
            double preciseLong = Utils.getCoarseGpsCoord(gpsRecord.getLongi(), currentGpsPrecision);

            try {
                Log.e("NET ","HOW BIG "+currentGpsPrecision);
                sizeOfPayload = howBig(preciseLat, preciseLong,
                        currentGpsPrecision, lastQueryTime);
                Log.e("NET ","size of payload "+sizeOfPayload);
            }
            catch(Exception e) {
                Log.e("err",e.getMessage());
            }
//            if (sizeOfPayload > Constants.MaxPayloadSize) {
//                currentGpsPrecision += 1;
//            }
//            else {
//                break;
//            }
//        }

        if (sizeOfPayload == 0) {
            Constants.PullServiceRunning = false;
            return;
        }

        //////////////////////////////////////////////////////////////////////////////////////////
        // get list of UUIDs that intersect with our movements and what the server has sent us
        //////////////////////////////////////////////////////////////////////////////////////////
//        double preciseLat = Utils.getCoarseGpsCoord(gpsRecord.getLat(), currentGpsPrecision);
//        double preciseLong = Utils.getCoarseGpsCoord(gpsRecord.getLongi(), currentGpsPrecision);

        Log.e("NET ","GET MESSAGES "+sizeOfPayload);
        List<BluetoothMatch> bluetoothMatches = getMessages(preciseLat,preciseLong,
                currentGpsPrecision, lastQueryTime);
        if (bluetoothMatches == null || bluetoothMatches.size() == 0) {
            Constants.PullServiceRunning = false;
            return;
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
        List<Long> ts1 = new ArrayList<>();
        List<Long> ts2 = new ArrayList<>();
        List<Long> ts3 = new ArrayList<>();
        long now = System.currentTimeMillis();
//        ts1.add(now);
//        ts2.add(now+(60000*5));
//        ts3.add(now+(60000*10));
        ts1.add(now-(60000*10));
        ts2.add(now-(60000*5));
        ts3.add(now);
        scannedBleMap.put("fd1a694e-d9b0-46ad-3094-4a15ea500adf", ts1);
        scannedBleMap.put("c34c7402-cd86-91c6-bd9e-ccb6e1bee762",ts2);
        scannedBleMap.put("7ce58cef-11ae-a894-4518-bb44333670e0", ts3);

        //////////////////////////////////////////////////////////////////////////////////////////
        // go through receivedbluetooth matches
        //////////////////////////////////////////////////////////////////////////////////////////
        List<String> exposedMessages = new ArrayList<>();
        List<Long> contactStartTimes = new ArrayList<>();
        List<Long> contactEndTimes = new ArrayList<>();
        for (BluetoothMatch bluetoothMatch : bluetoothMatches) {
            for (BlueToothSeed seed : bluetoothMatch.seeds) {
                if (seed.seed.equals("1e5e15cc-d622-aa39-d81b-03c79e3857ef")) {
                    Log.e("demo","SEED "+seed.seed);
                    long[] exposedStatus = isExposed(seed.seed,
                            seed.sequenceStartTime,
                            scannedBleMap);
                    if (exposedStatus != null) {
                        exposedMessages.add(bluetoothMatch.userMessage);
                        contactStartTimes.add(exposedStatus[0]);
                        contactEndTimes.add(exposedStatus[1]);
                    }
                }
            }
        }
        notifyBulk(Constants.MessageType.Exposure, exposedMessages, contactStartTimes, contactEndTimes);
    }

    // sync blockig op
    public int howBig(double lat, double longi, int precision, long ts) throws JSONException {
        String messageSizeRequest = MessageSizeRequest.toHttpString(lat, longi, precision, ts);
        JSONObject jsonResp = NetworkHelper.sendRequest(messageSizeRequest, Request.Method.GET, null);
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
        if (response == null) {
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
        if (response == null) {
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

        for (int i = 0; i < matchMessages.length; i++) {
            Log.e("DEMO ", "area match size: " + matchMessages[i].areaMatches.length);
            Log.e("DEMO ", "bluetooth match size: " + matchMessages[i].bluetoothMatches.length);
        }

        /////////////////////////////////////////////////////////////////////////
        // (3) update last query time to server
        /////////////////////////////////////////////////////////////////////////
        ArrayList<Long> queryTimes = new ArrayList<Long>();
        for (MessageInfo messageInfo : messageListResponse.messageInfo) {
            queryTimes.add(messageInfo.MessageTimestamp);
        }
        Collections.sort(queryTimes, Collections.reverseOrder());

        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (queryTimes.size() > 0) {
            editor.putLong(context.getString(R.string.time_of_last_query_pkey), queryTimes.get(0));
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
                            Log.e("msg", "NARROWCAST USER MESSAGE " + areaMatch.user_message);
                            narrowCastMessages.add(areaMatch.user_message);
                            narrowCastMessageStartTimes.add(area.begin_time);
                            narrowCastMessageEndTimes.add(area.end_time);
                            break;
                        }
                    }
                }
            }
        }
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
        List<GpsRecord> gpsRecords = gpsRepo.getRecordsBetweenTimestamps(area.begin_time, area.end_time);

        for (GpsRecord record : gpsRecords) {
            float[] result = new float[3];
            Location.distanceBetween(record.getLat(), record.getLongi(), area.location.latitude, area.location.longitude, result);

            if ((result.length == 1 && result[0] < area.radius_meters) ||
                    (result.length == 2 && result[1] < area.radius_meters) ||
                    (result.length >= 3 && result[2] < area.radius_meters)) {
                return true;
            }
        }
        return false;
    }

    // we get a seed and timestamp from the server for each infected person
    // check if we intersect with the infected person
    public static long[] isExposed(String seed, long ts, HashMap<String,List<Long>> scannedBleMap) {
        // if timestamp is in the future, something is wrong, return
        if (ts > System.currentTimeMillis()) {
            return null;
        }
        // convert our BLE DB records into convenient data structures
        // place the UUIDs into a map for easy lookup and checking

        // determine how many UUIDs to generate from the seed
        // based on time when the seed was generated and now.
        int diffBetweenNowAndTsInMinutes = (int)((System.currentTimeMillis() - ts)/1000/60);
        int temp = diffBetweenNowAndTsInMinutes / Constants.UUIDGenerationIntervalInMinutes;
        int numSeedsToGenerate = Math.max(3,temp);
        // if we need to generate too many timestamps, something is wrong, return.
        int infectionWindowInMinutes = (Constants.InfectionWindowInDays*24*60);
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
        int uuidGenerationIntervalInMillliseconds = Constants.UUIDGenerationIntervalInMinutes*60000;

        for (String receivedUUID : receivedUUIDs) {
            if (scannedBleMap.keySet().contains(receivedUUID)) {
                for (Long localTs : scannedBleMap.get(receivedUUID)) {
                    // check that the timestamps were within the same UUID generation interval
//                    if (Math.abs(localTs - ts) < uuidGenerationIntervalInMillliseconds) {
                        // record the timestamp when the local scanner picked it up
                        matches.add(localTs);
//                    }
                }
            }
            ts += uuidGenerationIntervalInMillliseconds;
        }

        // calculate how many matches we need to say user is exposed for at least 10 minutes
        // return false if there simply not enough matches to determine this.
        // this is 2
        int numConsecutiveMatchesNeeded = Constants.CDCExposureTimeInMinutes/Constants.BluetoothScanIntervalInMinutes;
        if (matches.size() < numConsecutiveMatchesNeeded) {
            return null;
        }

        // take diff of timestamps when we had a UUID match
        List<Integer> diff = new ArrayList<Integer>();
        for (int i = 0; i < matches.size()-1; i++) {
            diff.add((int)(matches.get(i+1)-matches.get(i)));
        }

        // counter tracks how many occurrences of
        // uuidGenerationIntervalInMillliseconds we have in a row
        int streak = 0;

        //check that we have at least numConsecutiveMatchesNeeded
        //matches of uuidGenerationIntervalInMillliseconds
        List<Long> contactTimesStart = new ArrayList<>();
        List<Long> contactTimesEnd = new ArrayList<>();
        for (int i = 0; i < diff.size(); i++) {
            if (Math.abs(diff.get(i)-bluetoothScanIntervalInMilliseconds)
                    <= Constants.TimestampDeviationInMilliseconds) {
                streak += 1;
                // add contact time once for the streak
                if (streak == numConsecutiveMatchesNeeded) {
                    int idx = i-(streak-1);
                    contactTimesStart.add(matches.get(idx));
                }
            }
            else {
                // a streak just ended
                if (contactTimesEnd.size() != contactTimesStart.size()) {
                    contactTimesEnd.add(matches.get(streak));
                }
                streak = 0;
            }
        }
        if (contactTimesEnd.size() != contactTimesStart.size()) {
            contactTimesEnd.add(matches.get(streak));
        }

        if (contactTimesStart.size() == 0) {
            return null;
        }

        long maxContactTime = 0;
        int maxContactTimeIdx = -1;
        for (int i = 0; i < contactTimesStart.size(); i++) {
            if (contactTimesEnd.get(i)-contactTimesStart.get(i) > maxContactTime) {
                maxContactTime = contactTimesEnd.get(i)-contactTimesStart.get(i);
                maxContactTimeIdx = i;
            }
        }

        if (maxContactTimeIdx == -1) {
            return null;
        }

        return new long[]{contactTimesStart.get(maxContactTimeIdx),
                contactTimesEnd.get(maxContactTimeIdx)};
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
                Log.e("demo","notify bulk");
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
            }
            else {
                if (!msgs.isEmpty()) {
                    new NotifOpsAsyncTask(context, new NotifRecord(
                            contactTimesStart.get(i),
                            contactTimesEnd.get(i),
                            msgs.get(i),
                            messageType.ordinal(),
                            true)).execute();
                }
            }
        }
    }
}
