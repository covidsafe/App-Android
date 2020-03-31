package com.example.covidsafe.comms;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.corona.comms.AddedLogs;
import com.example.corona.comms.BLTContactLog;
import com.example.corona.comms.BLTResult;
import com.example.corona.comms.GPSCoordinate;
import com.example.corona.comms.InfectedUserData;
import com.example.corona.comms.Key;
import com.example.corona.comms.Location;
import com.example.corona.comms.LocationTime;
import com.example.corona.comms.Log;
import com.example.corona.comms.Registered;
import com.example.corona.comms.RegistrationInfo;
import com.example.corona.comms.UserDataSent;
import com.example.covidsafe.ble.BleDbRecordRepository;
import com.example.covidsafe.ble.BleRecord;
import com.example.covidsafe.crypto.ByteHelper;
import com.example.covidsafe.crypto.SHA256;
import com.example.covidsafe.event.ContactLogEvent;
import com.example.covidsafe.event.RegistrationEvent;
import com.example.covidsafe.event.SendInfectedLogEvent;
import com.example.covidsafe.event.SendInfectedUserDataEvent;
import com.example.covidsafe.gps.GpsDbRecordRepository;
import com.example.covidsafe.gps.GpsRecord;
import com.example.covidsafe.utils.ByteUtils;
import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;

import org.greenrobot.eventbus.EventBus;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import io.grpc.stub.StreamObserver;

public class QueryBuilder {
    private RPCQuery query;
    private CommunicationConfig config;

    public CommunicationConfig getCommunicationConfig() {
        return config;
    }

    public QueryBuilder(CommunicationConfig config) {
        this.config = config;
        GrpcClient client = new GrpcClient(config);
        query = new RPCQuery(client.getChannel());
    }

    private Empty createEmptyRequest() {
        Empty empty_request = Empty.newBuilder().build();
        return empty_request;
    }

    /*
    Private Helper methods for the functions.
     */
    private RegistrationInfo createRegistrationInfoRequest(String key, String phone) {
        RegistrationInfo request = RegistrationInfo.newBuilder()
                .setKey(key)
                .setPhone(phone)
                .build();
        return request;
    }

    private InfectedUserData createInfectedUserDataRequest(String name, long dob) {
        InfectedUserData request = InfectedUserData.newBuilder()
                .setName(name)
                .setDob(dob)
                .build();
        return request;
    }

    /*
    Public RPC Methods which can be used from the application.
     */
//    public void registerUser() {
//        // TEST Entries delete for later
//        String phone = "1234567890";
//        String key = "thisisalongkeyvalue";
//        // END TEST Entries section
//        RegistrationInfo registration_request = createRegistrationInfoRequest(key, phone);
//
//        query.getAsyncStub().registerUser(registration_request, new StreamObserver<Registered>() {
//            @Override
//            public void onNext(Registered value) {
//                RegistrationEvent event = new RegistrationEvent();
//                event.setRequestStatus(true);
//                event.setResponse(value);
//                EventBus.getDefault().post(event);
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                RegistrationEvent event = new RegistrationEvent();
//                event.setRequestStatus(false);
//                event.setResponse(null);
//                EventBus.getDefault().post(event);
//            }
//
//            @Override
//            public void onCompleted() {
//
//            }
//        });
//    }

    public void sendInfectedLogsOfUser(List<BleRecord> bleRecords, List<GpsRecord> gpsRecords) {
        // Take the corresponding log files and process them accordingly before sending it
        android.util.Log.e("ble","do in background");
        StreamObserver<AddedLogs> responseObserver = new StreamObserver<AddedLogs>() {
            @Override
            public void onNext(AddedLogs value) {
                SendInfectedLogEvent event = new SendInfectedLogEvent();
                event.setRequestStatus(true);
                event.setResponse(value);
                EventBus.getDefault().post(event);
            }

            @Override
            public void onError(Throwable t) {
                SendInfectedLogEvent event = new SendInfectedLogEvent();
                event.setRequestStatus(false);
                event.setResponse(null);
                EventBus.getDefault().post(event);
            }

            @Override
            public void onCompleted() {

            }
        };

        StreamObserver<Log> requestObserver = query.getAsyncStub().sendInfectedLogs(responseObserver);
        try {
            for (BleRecord bleRecord : bleRecords) {
                android.util.Log.e("ble","log ble "+bleRecord);
                BLTResult bltResult = BLTResult.newBuilder()
                        .setUuid(ByteUtils.string2bytestring(bleRecord.getId()))
                        .build();

                Log log = Log.newBuilder()
                        .setBltResult(bltResult)
                        .setTimestamp(bleRecord.getTs())
                        .setType(Log.LogType.BLT)
                        .build();

                requestObserver.onNext(log);
            }

            for (GpsRecord gpsRecord : gpsRecords) {
                android.util.Log.e("ble","log gps "+gpsRecord);
                Location gpsCoordinate = Location.newBuilder()
                        .setLattitude((float)gpsRecord.getLat())
                        .setLongitude((float)gpsRecord.getLongi())
                        .build();

                Log log = Log.newBuilder()
                        .setCoordinate(gpsCoordinate)
                        .setTimestamp(gpsRecord.getTs())
                        .setType(Log.LogType.GPS)
                        .build();

                requestObserver.onNext(log);
            }
        } catch (RuntimeException e) {
            // Cancel and stop the RPC request from being continued. KILL Stream.
            requestObserver.onError(e);
            throw e;
        }
        requestObserver.onCompleted();
        android.util.Log.e("ble", "completed rpc");
    }

    public void getBLTContactLogs(double latitude, double longitude, double radius, long ts) {
        AtomicBoolean receivedValidResponse = new AtomicBoolean(false);
        List<BLTContactLog> contactLogs = Collections.synchronizedList(new ArrayList<>());

        StreamObserver<BLTContactLog> responseObserver = new StreamObserver<BLTContactLog>() {
            @Override
            public void onNext(BLTContactLog value) {
                receivedValidResponse.getAndSet(true);
                if (value != null) {
                    contactLogs.add(value);
                }
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                ContactLogEvent event = new ContactLogEvent();
                event.setContactLogs(contactLogs);
                if (receivedValidResponse.get()) {
                    event.setRequestStatus(true);
                } else {
                    event.setRequestStatus(false);
                }
                EventBus.getDefault().post(event);
            }
        };

        LocationTime loc = LocationTime.newBuilder()
                .setLocation(Location.newBuilder()
                    .setLattitude((float)latitude)
                    .setLongitude((float)longitude)
                    .setRadiusMeters((float)radius))
                .setTime(ts)
                .build();

        query.getAsyncStub().getBLTContactLogs(loc, responseObserver);
//        try {
//            for (int i = 0; i < 10; i++) {
//                byte[] key = SHA256.hash(Integer.toString(i));
//                Key.Builder keyBuilder = Key.newBuilder();
//                keyBuilder.setKey(ByteString.copyFrom(key));
//                Key k = keyBuilder.build();
//                android.util.Log.d("[SENDING]", ByteHelper.convertBytesToHex(key));
//                requestObserver.onNext(k);
//            }
//        } catch (RuntimeException e) {
//            requestObserver.onError(e);
//            throw e;
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        requestObserver.onCompleted();
    }


    public void sendInfectedUserData(String name, long dob) {
        InfectedUserData infected_user_data_request = createInfectedUserDataRequest(name, dob);

        query.getAsyncStub().sendInfectedUserData(infected_user_data_request, new StreamObserver<UserDataSent>() {
            @Override
            public void onNext(UserDataSent value) {
                SendInfectedUserDataEvent event = new SendInfectedUserDataEvent();
                event.setRequestStatus(true);
                event.setResponse(value);
                EventBus.getDefault().post(event);
            }

            @Override
            public void onError(Throwable t) {
                SendInfectedUserDataEvent event = new SendInfectedUserDataEvent();
                event.setRequestStatus(false);
                event.setResponse(null);
                EventBus.getDefault().post(event);
            }

            @Override
            public void onCompleted() {

            }
        });
    }
}
