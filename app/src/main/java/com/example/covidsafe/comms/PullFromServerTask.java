package com.example.covidsafe.comms;

import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.os.Messenger;
import android.os.ParcelUuid;
import android.util.Log;

import com.example.covidsafe.ble.BluetoothHelper;
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
    }
}
