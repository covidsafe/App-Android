package edu.uw.covidsafe.comms;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Messenger;
import android.util.Log;

import com.android.volley.Request;
import com.example.covidsafe.R;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.covidsafe.json.Area;
import edu.uw.covidsafe.ble.BleDbRecordRepository;
import edu.uw.covidsafe.ble.BleRecord;
import edu.uw.covidsafe.gps.GpsDbRecordRepository;
import edu.uw.covidsafe.gps.GpsRecord;
import edu.uw.covidsafe.json.MatchMessage;
import edu.uw.covidsafe.json.MessageInfo;
import edu.uw.covidsafe.json.MessageListRequest;
import edu.uw.covidsafe.json.MessageListResponse;
import edu.uw.covidsafe.json.MessageRequest;
import edu.uw.covidsafe.json.MessageSizeRequest;
import edu.uw.covidsafe.json.MessageSizeResponse;
import edu.uw.covidsafe.seed_uuid.SeedUUIDRecord;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.CryptoUtils;
import edu.uw.covidsafe.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PullFromServerTask implements Runnable {

    Messenger messenger;
    Context context;
    HashMap<String,Long> scannedBleMap;

    public PullFromServerTask(Messenger messenger, Context context) {
        this.messenger = messenger;
        this.context = context;
        this.scannedBleMap = new HashMap<>();
    }

    @Override
    public void run() {
        Log.e("uuid", "PULL FROM SERVER");

        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);

        //////////////////////////////////////////////////////////////////////////////////////////
        // send coarse -> finer grained gps locations, find size of seeds on server
        //////////////////////////////////////////////////////////////////////////////////////////
        GpsDbRecordRepository gpsRepo = new GpsDbRecordRepository(context);
        List<GpsRecord> gpsRecords = gpsRepo.getSortedRecords();
        if (gpsRecords.size() == 0) {
            return;
        }
        GpsRecord gpsRecord = gpsRecords.get(0);

        int currentGpsPrecision = Constants.MinimumGpsPrecision;

        int sizeOfPayload = 0;
        long lastQueryTime = prefs.getLong(context.getString(R.string.time_of_last_query_pkey), 0L);
        while (currentGpsPrecision < Constants.MaximumGpsPrecision) {
            double preciseLat = Utils.getCoarseGpsCoord(gpsRecord.getLat(), currentGpsPrecision);
            double preciseLong = Utils.getCoarseGpsCoord(gpsRecord.getLongi(), currentGpsPrecision);

            try {
                sizeOfPayload = howBig(preciseLat, preciseLong,
                        currentGpsPrecision, lastQueryTime);
            }
            catch(Exception e) {
                Log.e("err",e.getMessage());
            }
            if (sizeOfPayload > Constants.MaxPayloadSize) {
                currentGpsPrecision += 1;
            }
            else {
                break;
            }
        }

        if (sizeOfPayload == 0) {
            return;
        }

        //////////////////////////////////////////////////////////////////////////////////////////
        // get list of UUIDs that intersect with our movements and what the server has sent us
        //////////////////////////////////////////////////////////////////////////////////////////
        double preciseLat = Utils.getCoarseGpsCoord(gpsRecord.getLat(), currentGpsPrecision);
        double preciseLong = Utils.getCoarseGpsCoord(gpsRecord.getLongi(), currentGpsPrecision);

        List<SeedUUIDRecord> seedUUIDRecords = getMessages(preciseLat,preciseLong,
                currentGpsPrecision, lastQueryTime);
        if (seedUUIDRecords == null) {
            return;
        }

        // TODO: set intersection between ble IDs and received IDs, don't do a linear for loop
        // check that the set intersection will work.
        BleDbRecordRepository repo = new BleDbRecordRepository(context);
        List<BleRecord> bleRecords = repo.getAllRecords();
        for (BleRecord bleRecord : bleRecords) {
            Log.e("ble",bleRecord.toString());
            scannedBleMap.put(bleRecord.getUuid(), bleRecord.getTs());
        }

        for (SeedUUIDRecord seedUUIDRecord : seedUUIDRecords) {
            boolean exposedStatus = isExposed(seedUUIDRecord.seed, seedUUIDRecord.ts);
            if (exposedStatus) {
                notifyUserOfExposure();
                break;
            }
        }

        //////////////////////////////////////////////////////////////////////////////////////////
        // narrowcasting
        //////////////////////////////////////////////////////////////////////////////////////////

        List<String> announcements = announce(preciseLat,preciseLong,lastQueryTime);
        if (announcements.size() > 0) {
            makeAnnouncement(announcements);
        }
    }

    // sync blockig op
    public int howBig(double lat, double longi, int precision, long ts) throws JSONException {
        String messageSizeRequest = MessageSizeRequest.toHttpString(lat, longi, precision, ts);
        JSONObject jsonResp = NetworkHelper.sendRequest(messageSizeRequest, Request.Method.GET, null);
        MessageSizeResponse messageSizeResponse = MessageSizeResponse.parse(jsonResp);
        return messageSizeResponse.sizeOfQueryResponse;
    }

    public List<SeedUUIDRecord> getMessages(double lat, double longi, int precision, long lastQueryTime) {
        // return list of seeds and timestamps
        // check if the areas returned in these messages match our GPS timestamps

        /////////////////////////////////////////////////////////////////////////
        // (1) send MessageListRequest to get query IDs and timestamps
        String messageListRequest = MessageListRequest.toHttpString(lat, longi, precision, lastQueryTime);
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

        /////////////////////////////////////////////////////////////////////////
        // (2) make a request for the queries using the IDs
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
        response = NetworkHelper.sendRequest(messageRequest, Request.Method.POST, messageRequestObj);
        if (response == null) {
            return null;
        }

        MatchMessage matchMessage = null;
        try {
            matchMessage = MatchMessage.parse(response);
        }
        catch (Exception e) {
            Log.e("err",e.getMessage());
            return null;
        }
        if (matchMessage == null) {
            return null;
        }
        /////////////////////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////////////////////
        // (3) update last query time to server
        ArrayList<Long> queryTimes = new ArrayList<Long>();
        for (MessageInfo messageInfo : messageListResponse.messageInfo) {
            queryTimes.add(messageInfo.MessageTimestamp.toLong());
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
        // (4) check for area intersection
        List<SeedUUIDRecord> filteredRecords = new ArrayList<SeedUUIDRecord>();
        if (matchMessage.area_matches.length > 0) {
            Area[] areas = matchMessage.area_matches[0].areas;
            List<SeedUUIDRecord> receivedRecords = new ArrayList<SeedUUIDRecord>();
            int counter = 0;
            for (Area area : areas) {
                GpsDbRecordRepository gpsRepo = new GpsDbRecordRepository(context);
                List<GpsRecord> gpsRecords = gpsRepo.getRecordsBetweenTimestamps(area.begin_time.toLong(), area.end_time.toLong());

                for (GpsRecord record : gpsRecords) {
                    float[] result = new float[3];
                    Location.distanceBetween(record.getLat(), record.getLongi(), area.location.lattitude, area.location.longitude, result);

                    if ((result.length == 1 && result[0] < area.radius_meters) ||
                            (result.length == 2 && result[1] < area.radius_meters) ||
                            (result.length >= 3 && result[2] < area.radius_meters)) {
                        filteredRecords.add(receivedRecords.get(counter));
                    }
                }
                counter += 1;
            }
        }
        /////////////////////////////////////////////////////////////////////////

        return filteredRecords;
    }

    public List<String> announce(double lat, double longi, long ts) {
        JsonObject obj = new JsonObject();
        obj.addProperty("lat",lat);
        obj.addProperty("longi",longi);
        obj.addProperty("ts",ts);

        ///////////////////////////////////////////////////
        List<Area> areas = new ArrayList<Area>();
        List<String> messages = new ArrayList<String>();
        ///////////////////////////////////////////////////

        int counter = 0;
        List<String> filteredMessages = new ArrayList<String>();
        for (Area area : areas) {
            GpsDbRecordRepository gpsRepo = new GpsDbRecordRepository(context);
            List<GpsRecord> gpsRecords = gpsRepo.getRecordsBetweenTimestamps(area.begin_time.toLong(), area.end_time.toLong());

            for (GpsRecord record : gpsRecords) {
                float[] result = new float[3];
                Location.distanceBetween(record.getLat(), record.getLongi(), area.location.lattitude, area.location.longitude, result);

                if ((result.length == 1 && result[0] < area.radius_meters) ||
                    (result.length == 2 && result[1] < area.radius_meters) ||
                    (result.length >= 3 && result[2] < area.radius_meters)) {
                    filteredMessages.add(messages.get(counter));
                }
            }
            counter += 1;
        }
        return filteredMessages;
    }

    // we get a seed and timestamp from the server for each infected person
    // check if we intersect with the infected person
    public boolean isExposed(String seed, long ts) {
        // if timestamp is in the future, something is wrong, return
        if (ts > System.currentTimeMillis()) {
            return false;
        }
        // convert our BLE DB records into convenient data structures
        // place the UUIDs into a map for easy lookup and checking

        // determine how many UUIDs to generate from the seed
        // based on time when the seed was generated and now.
        int diffBetweenNowAndTsInMinutes = (int)((System.currentTimeMillis() - ts)/1000/60);
        int numSeedsToGenerate = diffBetweenNowAndTsInMinutes / Constants.UUIDGenerationIntervalInMinutes;

        // if we need to generate too many timestamps, something is wrong, return.
        int infectionWindowInMinutes = (Constants.InfectionWindowInDays*24*60);
        int maxSeedsToGenerate = infectionWindowInMinutes / Constants.UUIDGenerationIntervalInMinutes;
        if (numSeedsToGenerate > maxSeedsToGenerate) {
            return false;
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
                if (Math.abs(scannedBleMap.get(receivedUUID) - ts) < bluetoothScanIntervalInMilliseconds) {
                    matches.add(ts);
                }
            }
            ts += uuidGenerationIntervalInMillliseconds;
        }

        // calculate how many matches we need to say user is exposed for at least 10 minutes
        // return false if there simply not enough matches to determine this.
        int numConsecutiveMatchesNeeded = Constants.CDCExposureTimeInMinutes/Constants.BluetoothScanIntervalInMinutes;
        if (matches.size() < numConsecutiveMatchesNeeded) {
            return false;
        }

        // take diff of timestamps when we had a UUID match
        List<Integer> diff = new ArrayList<Integer>();
        for (int i = 0; i < matches.size()-1; i++) {
            diff.add((int)(matches.get(i+1)-matches.get(i)));
        }

        // counter tracks how many occurrences of
        // uuidGenerationIntervalInMillliseconds we have in a row
        // maxcounter is the 'max streak'
        int streak = 0;
        int maxStreak = 0;

        //check that we have at least numConsecutiveMatchesNeeded
        //matches of uuidGenerationIntervalInMillliseconds
        for (int i = 0; i < diff.size(); i++) {
            if (Math.abs(diff.get(i)-bluetoothScanIntervalInMilliseconds)
                < Constants.TimestampDeviationInMilliseconds) {
                streak += 1;
                if (streak > maxStreak) {
                    maxStreak = streak;
                }
            }
            else {
                streak = 0;
            }
        }

        if (maxStreak >= (numConsecutiveMatchesNeeded-1)) {
            return true;
        }
        return false;
    }

    public void notifyUserOfExposure() {
//        King County COVID-19 call center: 206-477-3977. Open daily from 8 a.m. to 7 p.m
//        Washington State COVID-19 call center: 800-525-0127
//        https://scanpublichealth.org/faq
    }

    public void makeAnnouncement(List<String> announcements) {
        // PSA
    }
}
