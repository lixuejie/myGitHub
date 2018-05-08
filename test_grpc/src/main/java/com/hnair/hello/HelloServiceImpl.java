
package com.hnair.hello;

import com.hnair.hello.Hello.HelloRequest;
import com.hnair.hello.Hello.HelloResponse;

import io.grpc.stub.StreamObserver;

public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

	@Override
	public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
		HelloResponse response = HelloResponse.newBuilder().setWords("Hello, " + request.getName() + ".").build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

}