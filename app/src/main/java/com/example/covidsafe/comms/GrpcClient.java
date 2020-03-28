package com.example.covidsafe.comms;

import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClient {
    protected ManagedChannel channel;

    public GrpcClient(CommunicationConfig config) {
        // TODO: Replace later with transport security.
        this.channel = ManagedChannelBuilder.forAddress(config.getHost(), config.getPort())
                .usePlaintext()
                .build();
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(60, TimeUnit.SECONDS);
    }
}
