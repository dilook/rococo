package guru.qa.rococo.service;

import guru.qa.rococo.grpc.Country;
import guru.qa.rococo.grpc.CreateMuseumRequest;
import guru.qa.rococo.grpc.Geo;
import guru.qa.rococo.grpc.GetAllMuseumsRequest;
import guru.qa.rococo.grpc.GetAllMuseumsResponse;
import guru.qa.rococo.grpc.GetMuseumByIdRequest;
import guru.qa.rococo.grpc.Museum;
import guru.qa.rococo.grpc.RococoMuseumServiceGrpc;
import guru.qa.rococo.grpc.UpdateMuseumRequest;
import guru.qa.rococo.model.MuseumJson;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MuseumGrpcClient {

    private final RococoMuseumServiceGrpc.RococoMuseumServiceBlockingStub museumServiceStub;

    public MuseumGrpcClient(RococoMuseumServiceGrpc.RococoMuseumServiceBlockingStub museumServiceStub) {
        this.museumServiceStub = museumServiceStub;
    }

    public Page<MuseumJson> getAllMuseums(Pageable pageable, String title) {
        try {
            GetAllMuseumsRequest.Builder requestBuilder = GetAllMuseumsRequest.newBuilder()
                    .setPage(pageable.getPageNumber())
                    .setSize(pageable.getPageSize());

            // Add sorting
            if (pageable.getSort().isSorted()) {
                pageable.getSort().forEach(order -> {
                    String sortStr = order.isDescending() ? "-" + order.getProperty() : order.getProperty();
                    requestBuilder.addSort(sortStr);
                });
            }

            // Add title filter if provided
            if (title != null && !title.isEmpty()) {
                requestBuilder.setTitle(title);
            }

            GetAllMuseumsResponse response = museumServiceStub.getAllMuseums(requestBuilder.build());

            List<MuseumJson> museums = response.getMuseumsList().stream()
                    .map(this::convertFromGrpcMuseum)
                    .toList();

            return new PageImpl<>(
                    museums,
                    pageable,
                    response.getTotalElements()
            );
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Error retrieving museums: " + e.getStatus().getDescription(), e);
        }
    }

    public MuseumJson getMuseumById(UUID id) {
        try {
            GetMuseumByIdRequest request = GetMuseumByIdRequest.newBuilder()
                    .setId(id.toString())
                    .build();

            Museum museum = museumServiceStub.getMuseumById(request);
            return convertFromGrpcMuseum(museum);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new guru.qa.rococo.ex.NotFoundException("Museum not found with id: " + id);
            }
            throw new RuntimeException("Error retrieving museum: " + e.getStatus().getDescription(), e);
        }
    }

    public MuseumJson createMuseum(MuseumJson museumJson) {
        try {
            CreateMuseumRequest.Builder requestBuilder = CreateMuseumRequest.newBuilder()
                    .setTitle(museumJson.title())
                    .setDescription(museumJson.description() != null ? museumJson.description() : "");

            if (museumJson.photo() != null) {
                requestBuilder.setPhoto(museumJson.photo());
            }

            if (museumJson.geo() != null) {
                Geo.Builder geoBuilder = Geo.newBuilder();
                if (museumJson.geo().city() != null) {
                    geoBuilder.setCity(museumJson.geo().city());
                }
                if (museumJson.geo().country() != null) {
                    Country country = Country.newBuilder()
                            .setId(museumJson.geo().country().id().toString())
                            .setName(museumJson.geo().country().name())
                            .build();
                    geoBuilder.setCountry(country);
                }
                requestBuilder.setGeo(geoBuilder.build());
            }

            Museum museum = museumServiceStub.createMuseum(requestBuilder.build());
            return convertFromGrpcMuseum(museum);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.INVALID_ARGUMENT) {
                throw new guru.qa.rococo.ex.RequiredParamException(e.getStatus().getDescription());
            }
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new guru.qa.rococo.ex.NotFoundException(e.getStatus().getDescription());
            }
            throw new RuntimeException("Error creating museum: " + e.getStatus().getDescription(), e);
        }
    }

    public MuseumJson updateMuseum(MuseumJson museumJson) {
        try {
            UpdateMuseumRequest.Builder requestBuilder = UpdateMuseumRequest.newBuilder()
                    .setId(museumJson.id().toString())
                    .setTitle(museumJson.title())
                    .setDescription(museumJson.description() != null ? museumJson.description() : "");

            if (museumJson.photo() != null) {
                requestBuilder.setPhoto(museumJson.photo());
            }

            if (museumJson.geo() != null) {
                Geo.Builder geoBuilder = Geo.newBuilder();
                if (museumJson.geo().city() != null) {
                    geoBuilder.setCity(museumJson.geo().city());
                }
                if (museumJson.geo().country() != null) {
                    Country country = Country.newBuilder()
                            .setId(museumJson.geo().country().id().toString())
                            .setName(museumJson.geo().country().name())
                            .build();
                    geoBuilder.setCountry(country);
                }
                requestBuilder.setGeo(geoBuilder.build());
            }

            Museum museum = museumServiceStub.updateMuseum(requestBuilder.build());
            return convertFromGrpcMuseum(museum);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new guru.qa.rococo.ex.NotFoundException(e.getStatus().getDescription());
            }
            if (e.getStatus().getCode() == Status.Code.INVALID_ARGUMENT) {
                throw new guru.qa.rococo.ex.RequiredParamException(e.getStatus().getDescription());
            }
            throw new RuntimeException("Error updating museum: " + e.getStatus().getDescription(), e);
        }
    }

    private MuseumJson convertFromGrpcMuseum(Museum museum) {
        MuseumJson.Geo geo = null;
        if (museum.hasGeo()) {
            guru.qa.rococo.model.CountryJson country = null;
            if (museum.getGeo().hasCountry()) {
                country = new guru.qa.rococo.model.CountryJson(
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