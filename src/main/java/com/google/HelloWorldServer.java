package com.google;


import services.hello.GreeterGrpc;
import services.hello.HelloReply;
import services.hello.HelloRequest;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import io.grpc.alts.AltsServerBuilder;

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
    private int port = 50051;

    public static void main(String[] args) throws IOException, InterruptedException {
        new HelloWorldServer().start(args);
    }

    private void parseArgs(String[] args) {
        boolean usage = false;
        for (String arg : args) {
            if (!arg.startsWith("--")) {
                System.err.println("All arguments must start with '--': " + arg);
                usage = true;
                break;
            }
            String[] parts = arg.substring(2).split("=", 2);
            String key = parts[0];
            if ("help".equals(key)) {
                usage = true;
                break;
            }
            if (parts.length != 2) {
                System.err.println("All arguments must be of the form --arg=value");
                usage = true;
                break;
            }
            String value = parts[1];
            if ("port".equals(key)) {
                port = Integer.parseInt(value);
            } else {
                System.err.println("Unknown argument: " + key);
                usage = true;
                break;
            }
        }
        if (usage) {
            HelloWorldServer s = new HelloWorldServer();
            System.out.println(
                    "Usage: [ARGS...]"
                            + "\n"
                            + "\n  --port=PORT           Server port to bind to. Default "
                            + s.port);
            System.exit(1);
        }
    }

    private void start(String[] args) throws IOException, InterruptedException {
        //parseArgs(args);
        server =
                AltsServerBuilder.forPort(port)
                        .addService(new GreeterImpl())
                        .executor(Executors.newFixedThreadPool(1))
                        .build();
        server.start();
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
