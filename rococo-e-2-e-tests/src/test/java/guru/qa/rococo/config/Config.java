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
    String userdataJdbcUrl();

    @Nonnull
    String userdataGrpcAddress();

    default int userdataGrpcPort() {
        return 9094;
    }

    @Nonnull
    String museumGrpcAddress();

    default int museumGrpcPort() {
        return 9091;
    }

    @Nonnull
    String artistGrpcAddress();

    default int artistGrpcPort() {
        return 9090;
    }

    @Nonnull
    String paintingGrpcAddress();

    default int paintingGrpcPort() {
        return 9093;
    }

    String allureDockerServiceUrl();

    @Nonnull
    default String ghUrl() {
        return "https://api.github.com/";
    }

}
