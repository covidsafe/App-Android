package com.example.covidsafe.comms;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Messenger;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.covidsafe.R;
import com.example.covidsafe.ble.BluetoothHelper;
import com.example.covidsafe.comms.CommunicationConfig;
import com.example.covidsafe.comms.NetworkConstant;
import com.example.covidsafe.comms.QueryBuilder;
import com.example.covidsafe.utils.ByteUtils;
import com.example.covidsafe.utils.Constants;
import com.example.covidsafe.utils.Utils;
import com.example.covidsafe.uuid.UUIDDbRecordRepository;
import com.example.covidsafe.uuid.UUIDOpsAsyncTask;
import com.example.covidsafe.uuid.UUIDRecord;

import java.util.List;
import java.util.UUID;

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
