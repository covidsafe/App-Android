package edu.uw.covidsafe.comms;

import android.content.Context;
import android.os.AsyncTask;

import edu.uw.covidsafe.seed_uuid.SeedUUIDDbRecordRepository;
import edu.uw.covidsafe.seed_uuid.SeedUUIDRecord;
import edu.uw.covidsafe.utils.Constants;

import java.util.List;

public class SendInfectedUserData extends AsyncTask<Void, Void, Void> {

    List<SeedUUIDRecord> seedUUIDRecords;
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
        int seedIndex = Constants.InfectionWindowInDays/Constants.UUIDGenerationIntervalInMinutes;

        this.seedUUIDRecords = seedUUIDRepo.getAllSortedRecords();
        SeedUUIDRecord record = this.seedUUIDRecords.get(seedIndex);

        //TODO, send the seed and timestamp to the server
//        sendRequest(record);

        return null;
    }
}
