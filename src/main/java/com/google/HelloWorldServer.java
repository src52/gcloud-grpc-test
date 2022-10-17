package com.google;


import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.alts.AltsServerBuilder;
import io.grpc.stub.StreamObserver;
import services.hello.GreeterGrpc;
import services.hello.HelloReply;
import services.hello.HelloRequest;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */
public class HelloWorldServer {
    private static final Logger logger = Logger.getLogger(HelloWorldServer.class.getName());
    private Server server;
    private int port = 8080;

    public static void main(String[] args) throws IOException, InterruptedException {
        new HelloWorldServer().start(args);
    }

    private void start(String[] args) throws IOException, InterruptedException {
        //parseArgs(args);
//        server = ServerBuilder.forPort(port)
//            .useTransportSecurity(certChain, privateKey)
//                .addService(new GreeterImpl())
//                .addService(new PromotionServiceImpl(factory))
//                .addService(new RewardServiceImpl(factory))
//                .build()
//                .start();
        server = AltsServerBuilder.forPort(port)
                        .addService(new GreeterImpl())
                        .executor(Executors.newFixedThreadPool(1))
                        .build().start();
        logger.log(Level.INFO, "Started on {0}", port);
        server.awaitTermination();
    }
    static class GreeterImpl extends GreeterGrpc.GreeterImplBase {
        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> observer) {
            observer.onNext(HelloReply.newBuilder().setMessage("Hello, " + request.getName()).build());
            observer.onCompleted();
        }
    }
}
