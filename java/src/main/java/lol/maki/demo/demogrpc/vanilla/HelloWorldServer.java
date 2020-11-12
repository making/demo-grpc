package lol.maki.demo.demogrpc.vanilla;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorldServer {
	private static final Logger log = LoggerFactory.getLogger(HelloWorldServer.class);

	public static void main(String[] args) throws Exception {
		final int port = Optional.ofNullable(System.getenv("PORT")).map(Integer::parseInt).orElse(50051);
		final Server server = ServerBuilder.forPort(port)
				.addService(new HelloServiceImpl())
				.build()
				.start();
		log.info("Server started.");
		Runtime.getRuntime()
				.addShutdownHook(new Thread(() -> {
					try {
						server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
					}
					catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					log.info("Server finished.");
				}));
		server.awaitTermination();
	}
}
