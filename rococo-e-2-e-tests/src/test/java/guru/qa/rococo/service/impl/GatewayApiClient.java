package guru.qa.rococo.service.impl;

import guru.qa.rococo.api.MuseumApi;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.CountryJson;
import guru.qa.rococo.model.rest.MuseumJson;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import retrofit2.Response;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class GatewayApiClient extends RestClient {

    private final MuseumApi museumApi;

    public GatewayApiClient() {
        super(CFG.gatewayUrl());
        this.museumApi = create(MuseumApi.class);
    }

    @Step("Get all museums with page '{0}', size '{1}', title '{2}' using REST API")
    @NotNull
    public RestResponsePage<MuseumJson> getAllMuseum(int page, int size, @Nullable String title) {
        try {
            Response<RestResponsePage<MuseumJson>> response = museumApi.getAllMuseum(page, size, title).execute();
            Assertions.assertEquals(200, response.code(),
                    "Response code: " + response.code() + ", response message: " + response.message());
            return requireNonNull(response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Get museum by id '{0}' using REST API")
    @NotNull
    public MuseumJson getMuseumById(UUID id) {
        try {
            Response<MuseumJson> response = museumApi.getMuseumById(id).execute();
            Assertions.assertEquals(200, response.code(),
                    "Response code: " + response.code() + ", response message: " + response.message());
            return requireNonNull(response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Update museum using REST API")
    @NotNull
    public MuseumJson updateMuseum(String bearerToken, MuseumJson museumJson) {
        try {
            Response<MuseumJson> response = museumApi.updateMuseum(bearerToken, museumJson).execute();
            Assertions.assertEquals(200, response.code(), "Response code: " + response.code());
            return requireNonNull(response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Create museum using REST API")
    @NotNull
    public MuseumJson createMuseum(String bearerToken, MuseumJson museumJson) {
        try {
            Response<MuseumJson> response = museumApi.createMuseum(bearerToken, museumJson).execute();
            Assertions.assertEquals(200, response.code(), "Response code: " + response.code());
            return requireNonNull(response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Get random country using REST API")
    @NotNull
    public CountryJson getRandomCountry(String token) {
        try {
            Response<RestResponsePage<CountryJson>> response = museumApi.getAllCountry(token, 0, 100, null).execute();
            Assertions.assertEquals(200, response.code(), "Response code: " + response.code());
            return requireNonNull(response.body()).stream()
                    .skip(ThreadLocalRandom.current().nextInt(response.body().getSize()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No country found"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}