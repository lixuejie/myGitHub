package com.hnair.hello;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.google.common.util.concurrent.ListenableFuture;
import com.hnair.hello.Hello.HelloRequest;
import com.hnair.hello.Hello.HelloResponse;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.netty.util.concurrent.DefaultEventExecutor;

public class Client {

	private static final Logger logger = Logger.getLogger(Client.class.getName());

    private ManagedChannel channel;

    private HelloServiceGrpc.HelloServiceBlockingStub blockingStub;
    
    private HelloServiceGrpc.HelloServiceFutureStub futureStub;

    
    public Client(String host, int port) {
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true);
        channel = channelBuilder.build();
        blockingStub = HelloServiceGrpc.newBlockingStub(channel);
        futureStub = HelloServiceGrpc.newFutureStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void sayHello(String name) {
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        try {
        	ListenableFuture<HelloResponse> listenableFuture = futureStub.sayHello(request);
        	Executor executorService = new DefaultEventExecutor();
			listenableFuture.addListener(new Runnable() {
                @Override
                public void run() {
                    try {
                    	logger.info("get listenable future's result " + listenableFuture.get());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }, executorService);
        	/*HelloResponse response = blockingStub.sayHello(request);
        	logger.info("Server says: " + response.getWords());*/
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client("localhost", 8848);
		client.sayHello("lixuejie");
		client.shutdown();
    }
}
