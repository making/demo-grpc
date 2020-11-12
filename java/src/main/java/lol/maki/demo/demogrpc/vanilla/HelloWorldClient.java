package lol.maki.demo.demogrpc.vanilla;

import java.util.Iterator;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lol.maki.demo.grpc.HelloRequest;
import lol.maki.demo.grpc.HelloResponse;
import lol.maki.demo.grpc.HelloServiceGrpc;
import lol.maki.demo.grpc.HelloServiceGrpc.HelloServiceBlockingStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorldClient {
	private static final Logger log = LoggerFactory.getLogger(HelloWorldClient.class);

	public static void main(String[] args) {
		final ManagedChannel channel = ManagedChannelBuilder
				.forTarget("localhost:50051")
				//.forTarget("cf-tcpapps.io:3381")
				.usePlaintext()
				.defaultLoadBalancingPolicy("round_robin")
				.build();
		final HelloServiceBlockingStub helloService = HelloServiceGrpc.newBlockingStub(channel);
		final HelloRequest request = HelloRequest.newBuilder().setGreeting("World").build();
		for (int i = 0; i < 10; i++) {
			final HelloResponse response = helloService.sayHello(request);
			log.info("Response = {}", response.getReply());
		}
		final Iterator<HelloResponse> lotsOfReplies = helloService.lotsOfReplies(request);
		lotsOfReplies.forEachRemaining(r -> log.info("LotsOfReplies = {}", r.getReply()));
	}
}
