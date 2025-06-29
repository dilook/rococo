package guru.qa.rococo.config;

import javax.annotation.Nonnull;

public interface Config {

    static @Nonnull Config getInstance() {
        return "docker".equals(System.getProperty("test.env"))
                ? DockerConfig.INSTANCE
                : LocalConfig.INSTANCE;
    }

    @Nonnull
    String frontUrl();

    @Nonnull
    String authUrl();

    @Nonnull
    String authJdbcUrl();

    @Nonnull
    String gatewayUrl();

    @Nonnull
    String userdataUrl();

    @Nonnull
    String userdataJdbcUrl();

    @Nonnull
    String museumJdbcUrl();

    @Nonnull
    String museumGrpcAddress();

    default int museumGrpcPort() {
        return 9091;
    }

}
