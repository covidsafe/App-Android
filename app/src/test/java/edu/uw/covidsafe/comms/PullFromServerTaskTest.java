package edu.uw.covidsafe.comms;

import android.location.Location;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.uw.covidsafe.ble.BleRecord;

import static org.junit.Assert.*;

public class PullFromServerTaskTest {

    @Test
    public void getMessages() {
        long t0 = 1586200500000L;
        for (int testCase = 1; testCase < 3; testCase++) {
            HashMap<String, List<Long>> scannedBleMap = new HashMap<>();
            List<BleRecord> bleRecords = new ArrayList<>();
            if (testCase == 1) {
                bleRecords.add(new BleRecord(
                        "6f6d3cf7-ec31-7a3b-2563-2aab28ec37bb",
                        t0 + (60000 * 12), 0
                ));
                bleRecords.add(new BleRecord(
                        "ff268fd5-9ed9-46a4-556d-352936e9f064",
                        t0 + (60000 * 17), 0
                ));
            } else if (testCase == 2) {
                bleRecords.add(new BleRecord(
                        "6f6d3cf7-ec31-7a3b-2563-2aab28ec37bb",
                        t0 + (60000 * 3), 0
                ));
                bleRecords.add(new BleRecord(
                        "6f6d3cf7-ec31-7a3b-2563-2aab28ec37bb",
                        t0 + (60000 * 8), 0
                ));
                bleRecords.add(new BleRecord(
                        "6f6d3cf7-ec31-7a3b-2563-2aab28ec37bb",
                        t0 + (60000 * 13), 0
                ));
                bleRecords.add(new BleRecord(
                        "ff268fd5-9ed9-46a4-556d-352936e9f064",
                        t0 + (60000 * 18), 0
                ));
            }
            for (BleRecord bleRecord : bleRecords) {
//            Log.e("ble",bleRecord.toString());
                if (!scannedBleMap.containsKey(bleRecord.getUuid())) {
                    scannedBleMap.put(bleRecord.getUuid(), new LinkedList<Long>());
                }
                scannedBleMap.get(bleRecord.getUuid()).add(bleRecord.getTs());
            }

            // this is a seed
            long[] times = PullFromServerTask.isExposed("00000000-0000-0000-0000-000000000000",
                    t0, scannedBleMap);
            if (testCase == 1) {
                assertNull(times);
            } else if (testCase == 2) {
                long start = (times[0] - t0) / 60000;
                long end = (times[1] - t0) / 60000;
                System.out.println("times " + start + "," + end);
                assertEquals(start, 3);
                assertEquals(end, 18);
            }
        }
    }
}