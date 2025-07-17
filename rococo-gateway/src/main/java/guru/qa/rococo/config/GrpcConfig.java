package guru.qa.rococo.config;

import guru.qa.rococo.grpc.RococoArtistServiceGrpc;
import guru.qa.rococo.grpc.RococoCountryServiceGrpc;
import guru.qa.rococo.grpc.RococoMuseumServiceGrpc;
import guru.qa.rococo.grpc.RococoPaintingServiceGrpc;
import guru.qa.rococo.grpc.RococoUserServiceGrpc;
import guru.qa.rococo.service.GrpcClientLoggerInterceptor;
import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcConfig {

    private static final String MUSEUM_CHANNEL_NAME = "rococo-museum";
    private static final String ARTIST_CHANNEL_NAME = "rococo-artist";
    private static final String PAINTING_CHANNEL_NAME = "rococo-painting";
    private static final String USERDATA_CHANNEL_NAME = "rococo-userdata";

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

    @Bean
    public RococoArtistServiceGrpc.RococoArtistServiceBlockingStub artistServiceStub(
            GrpcChannelFactory channels,
            GrpcClientLoggerInterceptor loggerInterceptor) {
        Channel interceptedChannel = createInterceptedChannel(channels, loggerInterceptor, ARTIST_CHANNEL_NAME);
        return RococoArtistServiceGrpc.newBlockingStub(interceptedChannel);
    }

    @Bean
    public RococoPaintingServiceGrpc.RococoPaintingServiceBlockingStub paintingServiceStub(
            GrpcChannelFactory channels,
            GrpcClientLoggerInterceptor loggerInterceptor) {
        Channel interceptedChannel = createInterceptedChannel(channels, loggerInterceptor, PAINTING_CHANNEL_NAME);
        return RococoPaintingServiceGrpc.newBlockingStub(interceptedChannel);
    }

    @Bean
    public RococoUserServiceGrpc.RococoUserServiceBlockingStub userServiceStub(
            GrpcChannelFactory channels,
            GrpcClientLoggerInterceptor loggerInterceptor) {
        Channel interceptedChannel = createInterceptedChannel(channels, loggerInterceptor, USERDATA_CHANNEL_NAME);
        return RococoUserServiceGrpc.newBlockingStub(interceptedChannel);
    }

    private Channel createInterceptedChannel(GrpcChannelFactory channels, GrpcClientLoggerInterceptor interceptor, String channelName) {
        return ClientInterceptors.intercept(
                channels.createChannel(channelName),
                interceptor
        );
    }
}
