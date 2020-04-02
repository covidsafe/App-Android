package edu.uw.covidsafe.comms;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import edu.uw.covidsafe.gps.GpsDbRecordRepository;
import edu.uw.covidsafe.gps.GpsRecord;
import edu.uw.covidsafe.seed_uuid.SeedUUIDDbRecordRepository;
import edu.uw.covidsafe.seed_uuid.SeedUUIDRecord;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

import java.util.List;

public class SendInfectedUserData extends AsyncTask<Void, Void, Void> {

    Context context;
    String fname;
    String lname;
    long dob;

    public SendInfectedUserData(Context context, String fname, String lname, long dob) {
        this.context = context;
        this.fname = fname;
        this.lname = lname;
        this.dob = dob;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
//        CommunicationConfig config = new CommunicationConfig(NetworkConstant.HOSTNAME, NetworkConstant.PORT, "TestServer");
//        QueryBuilder queryBuilder = new QueryBuilder(config);
//        queryBuilder.sendInfectedLogsOfUser(this.bleRecords, this.gpsRecords);
//        queryBuilder.sendInfectedUserData(this.fname, this.lname, this.dob);
    }

    @Override
    protected Void doInBackground(Void... params) {
        SeedUUIDDbRecordRepository seedUUIDRepo = new SeedUUIDDbRecordRepository(context);

        // getSeedAtBeginningOfInfectionWindow
        //4032 if infection window is 14 days and uuid generation time is 5 minutes
        int infectionWindowInMinutes = 60*24* Constants.InfectionWindowInDays;
        int seedIndexAtBeginningOfInfectionWindow = Constants.InfectionWindowInDays/Constants.UUIDGenerationIntervalInMinutes;

        List<SeedUUIDRecord> generatedRecords = seedUUIDRepo.getAllSortedRecords();

        // send either seed at beginning of infection window
        // or the earliest seed recorded, whichever is less
        int seedIndex = Math.min(generatedRecords.size(), seedIndexAtBeginningOfInfectionWindow);

        // get the seed and timestamp
        SeedUUIDRecord recordToSend = generatedRecords.get(seedIndex);

        //get most recent GPS entry as coarse location and send it
        GpsDbRecordRepository gpsRepo = new GpsDbRecordRepository(context);
        GpsRecord gpsRecord = gpsRepo.getSortedRecords().get(0);

        sendRequest(recordToSend.seed, recordToSend.ts,
                Utils.getCoarseGpsCoord(gpsRecord.getLat()),
                Utils.getCoarseGpsCoord(gpsRecord.getLongi()));

        return null;
    }

    public void sendRequest(String seed, long ts, double lat, double longi) {
        //TODO, send the seed and timestamp to the server
        JsonObject obj = new JsonObject();
        obj.addProperty("seed", seed);
        obj.addProperty("ts",ts);
        obj.addProperty("lat",lat);
        obj.addProperty("longi",longi);

    }
}
