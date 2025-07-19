package guru.qa.rococo.service.impl;

import guru.qa.rococo.api.ArtistApi;
import guru.qa.rococo.api.MuseumApi;
import guru.qa.rococo.api.PaintingApi;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.CountryJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.model.rest.PaintingJson;
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
    private final ArtistApi artistApi;
    private final PaintingApi paintingApi;

    public GatewayApiClient() {
        super(CFG.gatewayUrl());
        this.museumApi = create(MuseumApi.class);
        this.artistApi = create(ArtistApi.class);
        this.paintingApi = create(PaintingApi.class);
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

    @Step("Get all artists with page '{0}', size '{1}', name '{2}' using REST API")
    @NotNull
    public RestResponsePage<ArtistJson> getAllArtist(int page, int size, @Nullable String name) {
        try {
            Response<RestResponsePage<ArtistJson>> response = artistApi.getAllArtist(page, size, name).execute();
            Assertions.assertEquals(200, response.code(),
                    "Response code: " + response.code() + ", response message: " + response.message());
            return requireNonNull(response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Get artist by id '{0}' using REST API")
    @NotNull
    public ArtistJson getArtistById(UUID id) {
        try {
            Response<ArtistJson> response = artistApi.getArtistById(id).execute();
            Assertions.assertEquals(200, response.code(),
                    "Response code: " + response.code() + ", response message: " + response.message());
            return requireNonNull(response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Update artist using REST API")
    @NotNull
    public ArtistJson updateArtist(String bearerToken, ArtistJson artistJson) {
        try {
            Response<ArtistJson> response = artistApi.updateArtist(bearerToken, artistJson).execute();
            Assertions.assertEquals(200, response.code(), "Response code: " + response.code());
            return requireNonNull(response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Create artist using REST API")
    @NotNull
    public ArtistJson createArtist(String bearerToken, ArtistJson artistJson) {
        try {
            Response<ArtistJson> response = artistApi.createArtist(bearerToken, artistJson).execute();
            Assertions.assertEquals(200, response.code(), "Response code: " + response.code());
            return requireNonNull(response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Get all paintings with page '{0}', size '{1}', title '{2}' using REST API")
    @NotNull
    public RestResponsePage<PaintingJson> getAllPainting(int page, int size, @Nullable String title) {
        try {
            Response<RestResponsePage<PaintingJson>> response = paintingApi.getAllPainting(page, size, title).execute();
            Assertions.assertEquals(200, response.code(),
                    "Response code: " + response.code() + ", response message: " + response.message());
            return requireNonNull(response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Get painting by id '{0}' using REST API")
    @NotNull
    public PaintingJson getPaintingById(UUID id) {
        try {
            Response<PaintingJson> response = paintingApi.getPaintingById(id).execute();
            Assertions.assertEquals(200, response.code(),
                    "Response code: " + response.code() + ", response message: " + response.message());
            return requireNonNull(response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Update painting using REST API")
    @NotNull
    public PaintingJson updatePainting(String bearerToken, PaintingJson paintingJson) {
        try {
            Response<PaintingJson> response = paintingApi.updatePainting(bearerToken, paintingJson).execute();
            Assertions.assertEquals(200, response.code(), "Response code: " + response.code());
            return requireNonNull(response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Create painting using REST API")
    @NotNull
    public PaintingJson createPainting(String bearerToken, PaintingJson paintingJson) {
        try {
            Response<PaintingJson> response = paintingApi.createPainting(bearerToken, paintingJson).execute();
            Assertions.assertEquals(200, response.code(), "Response code: " + response.code());
            return requireNonNull(response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
