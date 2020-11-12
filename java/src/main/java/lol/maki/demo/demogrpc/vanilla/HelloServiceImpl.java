package lol.maki.demo.demogrpc.vanilla;

import io.grpc.stub.StreamObserver;
import lol.maki.demo.grpc.HelloRequest;
import lol.maki.demo.grpc.HelloResponse;
import lol.maki.demo.grpc.HelloServiceGrpc.HelloServiceImplBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloServiceImpl extends HelloServiceImplBase {
	private final Logger log = LoggerFactory.getLogger(HelloServiceImpl.class);

	@Override
	public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
		log.info("sayHello");
		final HelloResponse response = HelloResponse.newBuilder()
				.setReply(String.format("Hello %s!", request.getGreeting()))
				.build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void lotsOfReplies(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
		log.info("lotsOfReplies");
		for (int i = 0; i < 10; i++) {
			final HelloResponse response = HelloResponse.newBuilder()
					.setReply(String.format("[%05d] Hello %s!", i, request.getGreeting()))
					.build();
			responseObserver.onNext(response);
		}
		responseObserver.onCompleted();
	}
}
