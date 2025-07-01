package guru.qa.rococo.config;

import guru.qa.rococo.grpc.RococoCountryServiceGrpc;
import guru.qa.rococo.grpc.RococoMuseumServiceGrpc;
import guru.qa.rococo.service.GrpcClientLoggerInterceptor;
import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcConfig {

    private static final String MUSEUM_CHANNEL_NAME = "rococo-museum";

    @Bean
    public RococoMuseumServiceGrpc.RococoMuseumServiceBlockingStub museumServiceStub(
            GrpcChannelFactory channels,
            GrpcClientLoggerInterceptor loggerInterceptor) {
        Channel interceptedChannel = createInterceptedChannel(channels, loggerInterceptor, MUSEUM_CHANNEL_NAME);
        return RococoMuseumServiceGrpc.newBlockingStub(interceptedChannel);
    }

    @Bean
    public RococoCountryServiceGrpc.RococoCountryServiceBlockingStub countryServiceStub(
            GrpcChannelFactory channels,
            GrpcClientLoggerInterceptor loggerInterceptor) {
        Channel interceptedChannel = createInterceptedChannel(channels, loggerInterceptor, MUSEUM_CHANNEL_NAME);
        return RococoCountryServiceGrpc.newBlockingStub(interceptedChannel);
    }

    private Channel createInterceptedChannel(GrpcChannelFactory channels, GrpcClientLoggerInterceptor interceptor, String channelName) {
        return ClientInterceptors.intercept(
                channels.createChannel(channelName),
                interceptor
        );
    }
}
