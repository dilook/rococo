package guru.qa.rococo.test.grpc;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.grpc.RococoCountryServiceGrpc;
import guru.qa.rococo.grpc.RococoMuseumServiceGrpc;
import guru.qa.rococo.jupiter.annotation.meta.GrpcTest;
import guru.qa.rococo.utils.GrpcConsoleInterceptor;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.qameta.allure.grpc.AllureGrpc;

@GrpcTest
public class BaseGrpcTest {

    protected static final Config CFG = Config.getInstance();

    protected static final Channel museumChannel = ManagedChannelBuilder
            .forAddress(CFG.museumGrpcAddress(), CFG.museumGrpcPort())
            .intercept(new GrpcConsoleInterceptor())
            .intercept(new AllureGrpc())
            .usePlaintext()
            .maxInboundMessageSize(150 * 1024 * 1024)
            .build();

    protected static final RococoMuseumServiceGrpc.RococoMuseumServiceBlockingStub museumBlockingStub =
            RococoMuseumServiceGrpc.newBlockingStub(museumChannel);

    protected static final RococoCountryServiceGrpc.RococoCountryServiceBlockingStub countryBlockingStub =
            RococoCountryServiceGrpc.newBlockingStub(museumChannel);
}
