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
        return "http://auth.rococo.dc/";
    }

    @Nonnull
    @Override
    public String authJdbcUrl() {
        return "jdbc:postgresql://rococo-all-db:5432/rococo-auth";
    }

    @Nonnull
    @Override
    public String gatewayUrl() {
        return "http://gateway.rococo.dc:8080/";
    }

    @Nonnull
    @Override
    public String userdataUrl() {
        return "http://userdata.rococo.dc:8080/";
    }

    @Nonnull
    @Override
    public String userdataJdbcUrl() {
        return "jdbc:postgresql://rococo-all-db:5432/rococo-gateway";
    }

    @NotNull
    @Override
    public String museumJdbcUrl() {
        return "jdbc:postgresql://rococo-all-db:5432/rococo-museum";
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

}
