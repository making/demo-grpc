package lol.maki.demo.demogrpc.vanilla;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lol.maki.demo.grpc.HelloRequest;
import lol.maki.demo.grpc.HelloResponse;
import lol.maki.demo.grpc.HelloServiceGrpc;
import lol.maki.demo.grpc.HelloServiceGrpc.HelloServiceFutureStub;
import reactor.core.publisher.Flux;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	private final String grpcTarget;

	private final HelloServiceFutureStub helloService;

	public HelloController(@Value("${GRPC_TARGET:localhost:50051}") String grpcTarget) {
		System.out.println("GRPC_TARGET = " + grpcTarget);
		final ManagedChannel channel = ManagedChannelBuilder.forTarget(grpcTarget)
				.defaultLoadBalancingPolicy("round_robin")
				.usePlaintext()
				.build();
		this.grpcTarget = grpcTarget;
		this.helloService = HelloServiceGrpc.newFutureStub(channel);
	}

	@GetMapping(path = "/")
	public Map<?, ?> check() {
		return Collections.singletonMap("grpcTarget", grpcTarget);
	}


	@GetMapping(path = "/hello", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> hello(@RequestParam(name = "count", defaultValue = "10") int count) {
		final AtomicInteger counter = new AtomicInteger(0);
		return Flux.<String>create(sink -> {
			for (int i = 0; i < count; i++) {
				final HelloRequest request = HelloRequest.newBuilder().setGreeting("Hello " + i).build();
				final ListenableFuture<HelloResponse> future = this.helloService.sayHello(request);
				Futures.addCallback(future, new FutureCallback<>() {
					@Override
					public void onSuccess(HelloResponse response) {
						sink.next(response.getReply());
						if (counter.incrementAndGet() == count) {
							sink.complete();
						}
					}

					@Override
					public void onFailure(Throwable throwable) {
						sink.error(throwable);
					}
				}, MoreExecutors.directExecutor());
			}
		}).log();
	}
}
