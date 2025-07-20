package guru.qa.rococo.config;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

enum DockerConfig implements Config {
    INSTANCE;

    @Nonnull
    @Override
    public String frontUrl() {
        return "http://frontend.rococo.dc/";
    }

    @Nonnull
    @Override
    public String authUrl() {
        return "http://auth.rococo.dc:9000/";
    }

    @Nonnull
    @Override
    public String authJdbcUrl() {
        return "jdbc:postgresql://rococo-all-db:5432/rococo-auth";
    }

    @Nonnull
    @Override
    public String gatewayUrl() {
        return "http://gateway.rococo.dc:8000/";
    }

    @Nonnull
    @Override
    public String userdataGrpcAddress() {
        return "userdata.rococo.dc";
    }

    @Nonnull
    @Override
    public String userdataJdbcUrl() {
        return "jdbc:postgresql://rococo-all-db:5432/rococo-userdata";
    }

    @NotNull
    @Override
    public String museumGrpcAddress() {
        return "museum.rococo.dc";
    }

    @NotNull
    @Override
    public String artistGrpcAddress() {
        return "artist.rococo.dc";
    }

    @NotNull
    @Override
    public String paintingGrpcAddress() {
        return "painting.rococo.dc";
    }

    @Override
    public String allureDockerServiceUrl() {
        final String url = System.getenv( "ALLURE_DOCKER_API");
        return url == null ? "http://allure:5050" : url;
    }

}
