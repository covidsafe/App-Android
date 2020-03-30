package com.example.covidsafe.comms;
import android.content.Context;
import android.os.Messenger;
import android.util.Log;

import com.example.covidsafe.uuid.UUIDDbRecordRepository;
import com.example.covidsafe.uuid.UUIDRecord;

import java.util.List;

public class PullFromServerTask implements Runnable {

    Messenger messenger;
    Context context;

    public PullFromServerTask(Messenger messenger, Context context) {
        this.messenger = messenger;
        this.context = context;
    }

    @Override
    public void run() {
        Log.e("uuid", "PULL FROM SERVER");
        UUIDDbRecordRepository repo = new UUIDDbRecordRepository(context);
        List<UUIDRecord> records = repo.getAllRecords();
        for (UUIDRecord record : records) {
            Log.e("uuid",record.toString());
        }
        CommunicationConfig config = new CommunicationConfig(NetworkConstant.HOSTNAME, NetworkConstant.PORT, "TestServer");
        QueryBuilder queryBuilder = new QueryBuilder(config);
        queryBuilder.getBLTContactLogs();

        notifyUserOfExposure();
    }

    public void notifyUserOfExposure() {
//        King County COVID-19 call center: 206-477-3977. Open daily from 8 a.m. to 7 p.m
//        Washington State COVID-19 call center: 800-525-0127
//        https://scanpublichealth.org/faq
    }
}
