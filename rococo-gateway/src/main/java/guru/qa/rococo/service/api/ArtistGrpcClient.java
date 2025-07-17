package guru.qa.rococo.service.api;

import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.grpc.Artist;
import guru.qa.rococo.grpc.CreateArtistRequest;
import guru.qa.rococo.grpc.GetAllArtistsRequest;
import guru.qa.rococo.grpc.GetAllArtistsResponse;
import guru.qa.rococo.grpc.GetArtistByIdRequest;
import guru.qa.rococo.grpc.RococoArtistServiceGrpc;
import guru.qa.rococo.grpc.UpdateArtistRequest;
import guru.qa.rococo.model.ArtistJson;
import io.grpc.StatusRuntimeException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ArtistGrpcClient {

    private final RococoArtistServiceGrpc.RococoArtistServiceBlockingStub artistServiceStub;

    public ArtistGrpcClient(RococoArtistServiceGrpc.RococoArtistServiceBlockingStub artistServiceStub) {
        this.artistServiceStub = artistServiceStub;
    }

    public Page<ArtistJson> getAllArtist(Pageable pageable, String name) {
        try {
            GetAllArtistsRequest.Builder requestBuilder = GetAllArtistsRequest.newBuilder()
                    .setPage(pageable.getPageNumber())
                    .setSize(pageable.getPageSize());

            if (name != null && !name.isEmpty()) {
                requestBuilder.setName(name);
            }

            // Add sort information
            pageable.getSort().forEach(order -> {
                String sortStr = order.isDescending() ? "-" + order.getProperty() : order.getProperty();
                requestBuilder.addSort(sortStr);
            });

            GetAllArtistsResponse response = artistServiceStub.getAllArtists(requestBuilder.build());

            List<ArtistJson> artists = response.getArtistsList().stream()
                    .map(this::convertToArtistJson)
                    .toList();

            return new PageImpl<>(artists, pageable, response.getTotalElements());
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Error retrieving artists: " + e.getMessage(), e);
        }
    }

    public ArtistJson getArtistById(UUID id) {
        try {
            GetArtistByIdRequest request = GetArtistByIdRequest.newBuilder()
                    .setId(id.toString())
                    .build();

            Artist artist = artistServiceStub.getArtistById(request);
            return convertToArtistJson(artist);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == io.grpc.Status.Code.NOT_FOUND) {
                throw new NotFoundException("Artist not found with id: " + id);
            }
            throw new RuntimeException("Error retrieving artist: " + e.getMessage(), e);
        }
    }

    public ArtistJson updateArtist(ArtistJson artistJson) {
        try {
            UpdateArtistRequest.Builder requestBuilder = UpdateArtistRequest.newBuilder()
                    .setId(artistJson.id().toString())
                    .setName(artistJson.name())
                    .setBiography(artistJson.biography() != null ? artistJson.biography() : "");

            if (artistJson.photo() != null) {
                requestBuilder.setPhoto(artistJson.photo());
            }

            Artist artist = artistServiceStub.updateArtist(requestBuilder.build());
            return convertToArtistJson(artist);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == io.grpc.Status.Code.NOT_FOUND) {
                throw new NotFoundException("Artist not found with id: " + artistJson.id());
            }
            throw new RuntimeException("Error updating artist: " + e.getMessage(), e);
        }
    }

    public ArtistJson createArtist(ArtistJson artistJson) {
        try {
            CreateArtistRequest.Builder requestBuilder = CreateArtistRequest.newBuilder()
                    .setName(artistJson.name())
                    .setBiography(artistJson.biography() != null ? artistJson.biography() : "");

            if (artistJson.photo() != null) {
                requestBuilder.setPhoto(artistJson.photo());
            }

            Artist artist = artistServiceStub.createArtist(requestBuilder.build());
            return convertToArtistJson(artist);
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Error creating artist: " + e.getMessage(), e);
        }
    }

    private ArtistJson convertToArtistJson(Artist artist) {
        return new ArtistJson(
                UUID.fromString(artist.getId()),
                artist.getName(),
                artist.getBiography().isEmpty() ? null : artist.getBiography(),
                artist.getPhoto().isEmpty() ? null : artist.getPhoto()
        );
    }
}
