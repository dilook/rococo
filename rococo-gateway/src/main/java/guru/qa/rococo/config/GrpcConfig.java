package guru.qa.rococo.config;

import guru.qa.rococo.grpc.RococoMuseumServiceGrpc;
import guru.qa.rococo.service.GrpcClientLoggerInterceptor;
import io.grpc.ClientInterceptors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcConfig {

    @Bean
    public RococoMuseumServiceGrpc.RococoMuseumServiceBlockingStub museumServiceStub(
            GrpcChannelFactory channels,
            GrpcClientLoggerInterceptor loggerInterceptor) {
        return RococoMuseumServiceGrpc.newBlockingStub(
                ClientInterceptors.intercept(
                        channels.createChannel("rococo-museum"),
                        loggerInterceptor
                )
        );
    }
}
