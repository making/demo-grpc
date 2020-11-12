package lol.maki.demo.demogrpc.armeria;

import java.util.List;

import com.linecorp.armeria.common.grpc.GrpcSerializationFormats;
import com.linecorp.armeria.server.grpc.GrpcService;
import com.linecorp.armeria.server.logging.AccessLogWriter;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;
import io.grpc.BindableService;
import io.grpc.protobuf.services.ProtoReflectionService;
import lol.maki.demo.demogrpc.vanilla.HelloServiceImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArmeriaServerConfig {
	@Bean
	public HelloServiceImpl helloService() {
		return new HelloServiceImpl();
	}

	@Bean
	public ArmeriaServerConfigurator armeriaServerConfigurator(List<BindableService> grpcServices) {
		return serverBuilder -> serverBuilder
				.accessLogWriter(AccessLogWriter.combined(), true)
				.service(GrpcService.builder()
						.addServices(grpcServices)
						.addService(ProtoReflectionService.newInstance())
						.supportedSerializationFormats(GrpcSerializationFormats.values())
						.enableUnframedRequests(true)
						.build());
	}
}
