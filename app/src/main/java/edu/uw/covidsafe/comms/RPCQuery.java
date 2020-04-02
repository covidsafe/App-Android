package edu.uw.covidsafe.comms;

import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;

public class RPCQuery {
//    private CovidSafeServerGrpc.CovidSafeServerBlockingStub stub;
//    private CovidSafeServerGrpc.CovidSafeServerStub asyncStub;

    public RPCQuery(ManagedChannel channel) {
        // Return back here and update to include `MaxInboundMessageSize()` if needed.
//        stub = CovidSafeServerGrpc.newBlockingStub(channel).withCompression("gzip");
//        asyncStub = CovidSafeServerGrpc.newStub(channel).withCompression("gzip");
    }

//    public CovidSafeServerGrpc.CovidSafeServerBlockingStub getStub() {
//        return  stub.withDeadlineAfter(10, TimeUnit.SECONDS);
//    }
//
//    public CovidSafeServerGrpc.CovidSafeServerStub getAsyncStub() {
//        return asyncStub.withDeadlineAfter(10, TimeUnit.SECONDS);
//    }
}
