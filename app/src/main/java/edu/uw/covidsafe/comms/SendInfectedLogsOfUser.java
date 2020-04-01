package edu.uw.covidsafe.comms;

import android.content.Context;
import android.os.AsyncTask;

import edu.uw.covidsafe.ble.BleDbRecordRepository;
import edu.uw.covidsafe.ble.BleRecord;
import edu.uw.covidsafe.gps.GpsDbRecordRepository;
import edu.uw.covidsafe.gps.GpsRecord;

import java.util.List;

public class SendInfectedLogsOfUser extends AsyncTask<Void, Void, Void> {

    List<BleRecord> bleRecords;
    List<GpsRecord> gpsRecords;
    Context context;
    String fname;
    String lname;
    long dob;

    public SendInfectedLogsOfUser(Context context, String fname, String lname, long dob) {
        this.context = context;
        this.fname = fname;
        this.lname = lname;
        this.dob = dob;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        CommunicationConfig config = new CommunicationConfig(NetworkConstant.HOSTNAME, NetworkConstant.PORT, "TestServer");
        QueryBuilder queryBuilder = new QueryBuilder(config);
//        queryBuilder.sendInfectedLogsOfUser(this.bleRecords, this.gpsRecords);
//        queryBuilder.sendInfectedUserData(this.fname, this.lname, this.dob);
    }

    @Override
    protected Void doInBackground(Void... params) {
        BleDbRecordRepository bleRepo = new BleDbRecordRepository(context);
        this.bleRecords = bleRepo.getAllRecords();
        GpsDbRecordRepository gpsRepo = new GpsDbRecordRepository(context);
        this.gpsRecords = gpsRepo.getAllRecords();
        return null;
    }
}
