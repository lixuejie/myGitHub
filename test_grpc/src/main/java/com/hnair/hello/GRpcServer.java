package com.hnair.hello;
import io.grpc.Metadata;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.ServerInterceptors;
import io.grpc.ServerCall.Listener;

import java.io.IOException;
import java.util.logging.Logger;

public class GRpcServer {
    private static final Logger logger = Logger.getLogger(GRpcServer.class.getName());

    private Server server;

    private void start() throws IOException {
        int port = 8848;
        server = ServerBuilder
                .forPort(port)
                .addService(ServerInterceptors.intercept(new HelloServiceImpl(), new ServerInterceptor() {
					
					@Override
					public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
						logger.info(headers.toString());
						Listener<ReqT> listener = next.startCall(call,headers);
		                return listener;
					}
				}))
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                GRpcServer.this.stop();
                logger.info("Server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final GRpcServer server = new GRpcServer();
        server.start();
        server.blockUntilShutdown();
    }
}