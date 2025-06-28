package guru.qa.rococo.config;

import guru.qa.rococo.grpc.RococoMuseumServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcConfig {

    @Bean
    RococoMuseumServiceGrpc.RococoMuseumServiceBlockingStub museumServiceStub(GrpcChannelFactory channels) {
        return RococoMuseumServiceGrpc.newBlockingStub(channels.createChannel("rococo-museum"));
    }
}
