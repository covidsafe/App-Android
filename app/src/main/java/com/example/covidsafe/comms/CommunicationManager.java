package com.example.covidsafe.comms;

import java.util.concurrent.TimeUnit;

import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

// Good resource for future based stub: https://github.com/grpc/grpc-java/blob/master/benchmarks/src/main/java/io/grpc/benchmarks/qps/AsyncClient.java
public class CommunicationManager {
    private final ManagedChannel channel;
    private final TestServiceGrpc.TestServiceBlockingStub stub;
    private final CallCredentials callCredentials;

    public CommunicationManager(CommunicationConfig config) {
        // callCredentials = new JwtCredential(clientId);
        callCredentials = null;
        this.channel = ManagedChannelBuilder
                .forAddress(config.getHost(), config.getPort())
                // Channels are secure by default (via SSL/TLS). For this example we disable TLS
                // to avoid needing certificates, but it is recommended to use a secure channel
                // while passing credentials.
                .usePlaintext()
                .build();
        this.stub = TestServiceGrpc.newBlockingStub(channel);
    }

    public String greet(String name) {
        TestRequest request = TestRequest.newBuilder().setMesssage(name).build();

        TestReply response =
                stub
                        .withCallCredentials(callCredentials)
                        .sendTest(request);

        return response.getMessage();
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
