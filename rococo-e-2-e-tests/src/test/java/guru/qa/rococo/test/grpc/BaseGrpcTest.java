package guru.qa.rococo.test.grpc;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.grpc.RococoArtistServiceGrpc;
import guru.qa.rococo.grpc.RococoCountryServiceGrpc;
import guru.qa.rococo.grpc.RococoMuseumServiceGrpc;
import guru.qa.rococo.jupiter.annotation.meta.GrpcTest;
import guru.qa.rococo.utils.GrpcConsoleInterceptor;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.qameta.allure.grpc.AllureGrpc;

@GrpcTest
public class BaseGrpcTest {

    private static final int MAX_INBOUND_MESSAGE_SIZE = 150 * 1024 * 1024;
    protected static final Config CFG = Config.getInstance();

    protected static final Channel museumChannel = createGrpcChannel(
            CFG.museumGrpcAddress(),
            CFG.museumGrpcPort()
    );

    protected static final Channel artistChannel = createGrpcChannel(
            CFG.artistGrpcAddress(),
            CFG.artistGrpcPort()
    );

    protected static final RococoMuseumServiceGrpc.RococoMuseumServiceBlockingStub museumBlockingStub =
            RococoMuseumServiceGrpc.newBlockingStub(museumChannel);

    protected static final RococoCountryServiceGrpc.RococoCountryServiceBlockingStub countryBlockingStub =
            RococoCountryServiceGrpc.newBlockingStub(museumChannel);

    protected static final RococoArtistServiceGrpc.RococoArtistServiceBlockingStub artistBlockingStub =
            RococoArtistServiceGrpc.newBlockingStub(artistChannel);

    private static Channel createGrpcChannel(String address, int port) {
        return ManagedChannelBuilder
                .forAddress(address, port)
                .intercept(new GrpcConsoleInterceptor())
                .intercept(new AllureGrpc())
                .usePlaintext()
                .maxInboundMessageSize(MAX_INBOUND_MESSAGE_SIZE)
                .build();
    }
}
