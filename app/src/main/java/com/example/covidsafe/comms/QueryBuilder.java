package com.example.covidsafe.comms;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.corona.comms.AddedLogs;
import com.example.corona.comms.BLTContactLog;
import com.example.corona.comms.BLTResult;
import com.example.corona.comms.Key;
import com.example.corona.comms.Log;
import com.example.corona.comms.Registered;
import com.example.corona.comms.RegistrationInfo;
import com.example.covidsafe.crypto.ByteHelper;
import com.example.covidsafe.crypto.SHA256;
import com.example.covidsafe.event.ContactLogEvent;
import com.example.covidsafe.event.RegistrationEvent;
import com.example.covidsafe.event.SendInfectedLogEvent;
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


    /*
    Public RPC Methods which can be used from the application.
     */
    public void registerUser() {
        // TEST Entries delete for later
        String phone = "1234567890";
        String key = "thisisalongkeyvalue";
        // END TEST Entries section
        RegistrationInfo registration_request = createRegistrationInfoRequest(key, phone);

        query.getAsyncStub().registerUser(registration_request, new StreamObserver<Registered>() {
            @Override
            public void onNext(Registered value) {
                RegistrationEvent event = new RegistrationEvent();
                event.setRequestStatus(true);
                event.setResponse(value);
                EventBus.getDefault().post(event);
            }

            @Override
            public void onError(Throwable t) {
                RegistrationEvent event = new RegistrationEvent();
                event.setRequestStatus(false);
                event.setResponse(null);
                EventBus.getDefault().post(event);
            }

            @Override
            public void onCompleted() {

            }
        });
    }


    public void sendInfectedLogsOfUser() {
        // Take the corresponding log files and process them accordingly before sending it
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
            // THIS IS AN EXAMPLE LOG MESSAGE; change this accordingly.
            for (int i=0; i<10; i++) {
                Log.Builder b = Log.newBuilder();

                long timestampValue = System.currentTimeMillis();
                BLTResult.Builder bltResult = BLTResult.newBuilder();
                UUID randomUUID = UUID.randomUUID();
                bltResult.setUuid(ByteString.copyFrom(ByteUtils.uuid2bytes(randomUUID)));
                bltResult.setName("TESTBLTNAME " + i);

                b.setType(Log.LogType.BLT);
                b.setTimestamp(timestampValue);
                b.setBltResult(bltResult);

                Log logObjectInRequest = b.build();

                requestObserver.onNext(logObjectInRequest);
            }
        } catch (RuntimeException e) {
            // Cancel and stop the RPC request from being continued. KILL Stream.
            requestObserver.onError(e);
            throw e;
        }
        requestObserver.onCompleted();
    }

    public void getBLTContactLogs() {
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

        StreamObserver<Key> requestObserver = query.getAsyncStub().getBLTContactLogs(responseObserver);

        try {
            for (int i=0; i<10; i++) {
                byte[] key = SHA256.hash(Integer.toString(i));
                Key.Builder keyBuilder = Key.newBuilder();
                keyBuilder.setKey(ByteString.copyFrom(key));
                Key k = keyBuilder.build();
                android.util.Log.d("[SENDING]", ByteHelper.convertBytesToHex(key));
                requestObserver.onNext(k);
            }
        } catch (RuntimeException e) {
            requestObserver.onError(e);
            throw e;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        requestObserver.onCompleted();
    }
}
