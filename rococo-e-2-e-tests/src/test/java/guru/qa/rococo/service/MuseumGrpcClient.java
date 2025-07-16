package guru.qa.rococo.service;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.grpc.Country;
import guru.qa.rococo.grpc.CreateMuseumRequest;
import guru.qa.rococo.grpc.Geo;
import guru.qa.rococo.grpc.GetAllCountriesRequest;
import guru.qa.rococo.grpc.GetAllMuseumsRequest;
import guru.qa.rococo.grpc.GetCountryRequest;
import guru.qa.rococo.grpc.GetMuseumByIdRequest;
import guru.qa.rococo.grpc.Museum;
import guru.qa.rococo.grpc.RococoCountryServiceGrpc;
import guru.qa.rococo.grpc.RococoMuseumServiceGrpc;
import guru.qa.rococo.grpc.UpdateMuseumRequest;
import guru.qa.rococo.model.rest.CountryJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.utils.GrpcConsoleInterceptor;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.qameta.allure.grpc.AllureGrpc;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

import static guru.qa.rococo.utils.GrpcUtils.safe;


public class MuseumGrpcClient {

    private final Config CFG = Config.getInstance();

    private final Channel channel = ManagedChannelBuilder
            .forAddress(CFG.museumGrpcAddress(), CFG.museumGrpcPort())
            .intercept(new AllureGrpc())
            .intercept(new GrpcConsoleInterceptor())
            .usePlaintext()
            .build();

    private final RococoMuseumServiceGrpc.RococoMuseumServiceBlockingStub museumServiceStub
            = RococoMuseumServiceGrpc.newBlockingStub(channel);

    private final RococoCountryServiceGrpc.RococoCountryServiceBlockingStub countryBlockingStub
            = RococoCountryServiceGrpc.newBlockingStub(channel);


    public List<CountryJson> getAllCountries() {
        return countryBlockingStub.getAllCountries(GetAllCountriesRequest.newBuilder().setPage(0).setSize(200).build())
                .getCountriesList().stream()
                .map(country -> new CountryJson(
                        UUID.fromString(country.getId()),
                        country.getName()
                )).toList();
    }

    public CountryJson getCountryByName(String name) {
        Country country = countryBlockingStub.getCountry(GetCountryRequest.newBuilder().setName(name).build());
        return new CountryJson(UUID.fromString(country.getId()), country.getName());
    }

    public CountryJson getRandomCountry() {
        return getAllCountries().stream().findAny().get();
    }

    public List<MuseumJson> getAllMuseums(int page, int size, String title) {
        GetAllMuseumsRequest request = GetAllMuseumsRequest.newBuilder()
                .setPage(page)
                .setSize(size)
                .setTitle(title != null ? title : "")
                .build();

        return museumServiceStub.getAllMuseums(request)
                .getMuseumsList().stream()
                .map(this::convertFromGrpcMuseum)
                .toList();
    }

    public MuseumJson getMuseumById(UUID id) {
        GetMuseumByIdRequest request = GetMuseumByIdRequest.newBuilder()
                .setId(id.toString())
                .build();

        Museum museum = museumServiceStub.getMuseumById(request);
        return convertFromGrpcMuseum(museum);
    }

    public MuseumJson createMuseum(@Nonnull MuseumJson museumJson) {
        CreateMuseumRequest request = CreateMuseumRequest.newBuilder()
                .setTitle(museumJson.title())
                .setDescription(safe(museumJson.description()))
                .setPhoto(safe(museumJson.photo()))
                .setGeo(Geo.newBuilder()
                        .setCity(safe(museumJson.geo().city()))
                        .setCountry(Country.newBuilder()
                                .setId(safe(museumJson.geo().country().id()))
                                .setName(safe(museumJson.geo().country().name()))
                                .build())
                ).build();

        Museum museum = museumServiceStub.createMuseum(request);
        return convertFromGrpcMuseum(museum);
    }

    public MuseumJson updateMuseum(MuseumJson museumJson) {
        UpdateMuseumRequest request = UpdateMuseumRequest.newBuilder()
                .setId(museumJson.id().toString())
                .setTitle(museumJson.title())
                .setDescription(museumJson.description() != null ? museumJson.description() : "")
                .setPhoto(museumJson.photo() != null ? museumJson.photo() : "")
                .setGeo(Geo.newBuilder()
                        .setCity(museumJson.geo().city() != null ? museumJson.geo().city() : "")
                        .setCountry(Country.newBuilder()
                                .setId(museumJson.geo().country().id().toString())
                                .setName(museumJson.geo().country().name())
                                .build()
                        )
                ).build();
        Museum museum = museumServiceStub.updateMuseum(request);
        return convertFromGrpcMuseum(museum);
    }

    private MuseumJson convertFromGrpcMuseum(Museum museum) {
        MuseumJson.Geo geo = null;
        if (museum.hasGeo()) {
            CountryJson country = null;
            if (museum.getGeo().hasCountry()) {
                country = new CountryJson(
                        UUID.fromString(museum.getGeo().getCountry().getId()),
                        museum.getGeo().getCountry().getName()
                );
            }
            geo = new MuseumJson.Geo(
                    museum.getGeo().getCity().isEmpty() ? null : museum.getGeo().getCity(),
                    country
            );
        }

        return new MuseumJson(
                UUID.fromString(museum.getId()),
                museum.getTitle(),
                museum.getDescription().isEmpty() ? null : museum.getDescription(),
                museum.getPhoto().isEmpty() ? null : museum.getPhoto(),
                geo
        );
    }
}