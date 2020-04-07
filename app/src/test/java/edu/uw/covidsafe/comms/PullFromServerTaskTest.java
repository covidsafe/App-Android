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

//    0 = "6f6d3cf7-ec31-7a3b-2563-2aab28ec37bb"
//    1 = "ff268fd5-9ed9-46a4-556d-352936e9f064"
//    2 = "98dacf22-e717-9985-1386-6b6bf1493997"
//    3 = "51f6c6f1-b54d-a3fb-b217-2ba431858b07"
//    4 = "282be997-2dda-d85b-251f-de830bf6fc5c"
//    5 = "72676e4c-6b24-6859-eb80-4ebb97689189"
//    6 = "3529f5ef-de1d-9bab-41bb-4ed797590d34"
//    7 = "f86ed7a2-3e79-4f77-218f-bd62e6b94613"

    @Test
    public void getMessages() {
        long t0 = 1586200500000L;
        for (int testCase = 3; testCase < 4; testCase++) {
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
            } else if (testCase == 3) {
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
                        t0 + (60000 * 12), 0
                ));
                bleRecords.add(new BleRecord(
                        "ff268fd5-9ed9-46a4-556d-352936e9f064",
                        t0 + (60000 * 18), 0
                ));
                bleRecords.add(new BleRecord(
                        "ff268fd5-9ed9-46a4-556d-352936e9f064",
                        t0 + (60000 * 20), 0
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
            else if (testCase == 3) {
                assertNull(times);
            }
        }
    }
}