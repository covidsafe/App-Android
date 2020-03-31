package com.example.covidsafe.comms;

import android.content.Context;
import android.os.AsyncTask;

import com.example.corona.comms.AddedLogs;
import com.example.corona.comms.BLTResult;
import com.example.corona.comms.GPSCoordinate;
import com.example.corona.comms.Log;
import com.example.covidsafe.ble.BleDbRecordRepository;
import com.example.covidsafe.ble.BleRecord;
import com.example.covidsafe.event.SendInfectedLogEvent;
import com.example.covidsafe.gps.GpsDbRecordRepository;
import com.example.covidsafe.gps.GpsRecord;
import com.example.covidsafe.utils.ByteUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.grpc.stub.StreamObserver;

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
        queryBuilder.sendInfectedLogsOfUser(this.bleRecords, this.gpsRecords);
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
