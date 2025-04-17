package com.pm.billingservice.grpc;


import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
// StreamObserver is used by both the client stubs and service implementations for sending or receiving stream messages
// https://grpc.github.io/grpc-java/javadoc/io/grpc/stub/StreamObserver.html
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BillingGrpcService extends BillingServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);

    @Override
    public void createBillingAccount(billing.BillingRequest billingRequest,
            StreamObserver<billing.BillingResponse> responseObserver) {
        log.info("createBillingAccount request received {}", billingRequest.toString());
        // Business Login - e.g save to database, perform calculations etc
        BillingResponse billingResponse = BillingResponse.newBuilder()
                .setAccountId("12345")
                .setStatus("ACTIVE")
                .build();

        // StreamObserver method to send response from grpc (billing) service back to client.
        responseObserver.onNext(billingResponse);
        responseObserver.onCompleted(); // receives notification of successful stream completing and end cycles
    }
}
