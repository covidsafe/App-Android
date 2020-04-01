package edu.uw.covidsafe.comms;

import android.content.Context;
import android.os.AsyncTask;

import edu.uw.covidsafe.seed_uuid.SeedUUIDRecord;

import java.util.HashSet;
import java.util.List;

public class GetBLTContactLogsAsyncTask extends AsyncTask<Void, Void, Void> {

    Context context;
    List<SeedUUIDRecord> seedUuidRecords;
    HashSet<String> uuidMap;
//    List<BLTContactLog> logs;

//    public GetBLTContactLogsAsyncTask(Context context, List<BLTContactLog> logs) {
//        this.context = context;
//        this.logs = logs;
//    }
//
//    @Override
//    protected void onPostExecute(Void aVoid) {
//        super.onPostExecute(aVoid);
//
//        for (BLTContactLog log : logs) {
//            List<ByteString> bstrings = log.getUuidList();
//            List<Long> timestamps = log.getTimestampList();
//            for (int i = 0; i < bstrings.size(); i++) {
//                String receivedUUID = ByteUtils.byte2string(bstrings.get(i).toByteArray());
//                if (uuidMap.contains(receivedUUID)) {
//                    UUIDRecord recordedRecord = findUUIDRecord(receivedUUID);
//                    if (recordedRecord != null &&
//                        Math.abs(recordedRecord.getTs() - timestamps.get(i)) < Constants.TimestampDeviationInMilliseconds) {
////                        notifyUserOfExposure();
//                    }
//                }
//            }
//        }
//    }
//
//    public UUIDRecord findUUIDRecord(String str) {
//        for (UUIDRecord record : this.uuidRecords) {
//            if (record.uuid.equals(str)) {
//                return record;
//            }
//        }
//        return null;
//    }
//
    @Override
    protected Void doInBackground(Void... params) {
//        UUIDDbRecordRepository uuidRepo = new UUIDDbRecordRepository(context);
//        this.uuidRecords = uuidRepo.getAllRecords();
//        this.uuidMap = new HashSet<>();
//        for (UUIDRecord record : this.uuidRecords) {
//            this.uuidMap.add(record.uuid);
//        }
        return null;
    }
}
