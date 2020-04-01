package edu.uw.covidsafe.comms;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.GrpcSslContexts;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

public class GrpcClient {
    protected ManagedChannel channel;
    protected ManagedChannel secureChannel;

    private boolean useSSL = false;

    private static SslContext buildSslContext(String trustCertCollectionFilePath) throws SSLException {
        SslContextBuilder builder = GrpcSslContexts.forClient().clientAuth(ClientAuth.OPTIONAL);
        if (trustCertCollectionFilePath != null) {
            builder.trustManager(new File(trustCertCollectionFilePath));
        }
        return builder.build();
    }

    public GrpcClient(CommunicationConfig config) {
        // TODO: Replace later with transport security.
        this.channel = ManagedChannelBuilder.forAddress(config.getHost(), config.getPort())
                .usePlaintext()
                .build();

        if (useSSL) {
            // FIXME: Check later if this works with a working TLS certificate with a valid CA.
            this.secureChannel = ManagedChannelBuilder.forAddress(config.getHost(), config.getPort())
                    .useTransportSecurity().build();
        }
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(60, TimeUnit.SECONDS);
        secureChannel.shutdown().awaitTermination(60, TimeUnit.SECONDS);
    }

    public ManagedChannel getChannel() {
        if (useSSL) {
            return secureChannel;
        }
        return channel;
    }
}
