package guru.qa.rococo.service;

import guru.qa.rococo.data.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.grpc.Artist;
import guru.qa.rococo.grpc.CreateArtistRequest;
import guru.qa.rococo.grpc.GetAllArtistsRequest;
import guru.qa.rococo.grpc.GetAllArtistsResponse;
import guru.qa.rococo.grpc.GetArtistByIdRequest;
import guru.qa.rococo.grpc.RococoArtistServiceGrpc;
import guru.qa.rococo.grpc.UpdateArtistRequest;
import guru.qa.rococo.utils.GrpcUtils;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.grpc.server.service.GrpcService;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@GrpcService
public class ArtistGrpcService extends RococoArtistServiceGrpc.RococoArtistServiceImplBase {

    private final ArtistRepository artistRepository;

    public ArtistGrpcService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public void getAllArtists(GetAllArtistsRequest request, StreamObserver<GetAllArtistsResponse> responseObserver) {
        try {
            Sort sort = GrpcUtils.createSortFromList(request.getSortList());
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
            Page<ArtistEntity> artists = request.hasName() && !request.getName().isEmpty()
                    ? artistRepository.findByNameContainingIgnoreCase(request.getName(), pageable)
                    : artistRepository.findAll(pageable);

            GetAllArtistsResponse.Builder responseBuilder = GetAllArtistsResponse.newBuilder()
                    .setTotalPages(artists.getTotalPages())
                    .setTotalElements(artists.getTotalElements())
                    .setCurrentPage(artists.getNumber())
                    .setPageSize(artists.getSize())
                    .setFirst(artists.isFirst())
                    .setLast(artists.isLast());

            artists.getContent().forEach(artist -> 
                responseBuilder.addArtists(convertToGrpcArtist(artist))
            );

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error retrieving artists: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getArtistById(GetArtistByIdRequest request, StreamObserver<Artist> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());
            ArtistEntity artist = artistRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Artist not found with id: " + id));

            responseObserver.onNext(convertToGrpcArtist(artist));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid artist ID format")
                    .asRuntimeException());
        } catch (RuntimeException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error retrieving artist: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void createArtist(CreateArtistRequest request, StreamObserver<Artist> responseObserver) {
        try {
            ArtistEntity artistEntity = new ArtistEntity();
            artistEntity.setName(request.getName());
            artistEntity.setBiography(request.getBiography());
            if (!request.getPhoto().isEmpty()) {
                artistEntity.setPhoto(request.getPhoto().getBytes(StandardCharsets.UTF_8));
            }

            ArtistEntity savedArtist = artistRepository.save(artistEntity);
            responseObserver.onNext(convertToGrpcArtist(savedArtist));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error creating artist: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void updateArtist(UpdateArtistRequest request, StreamObserver<Artist> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());
            ArtistEntity artistEntity = artistRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Artist not found with id: " + id));

            artistEntity.setName(request.getName());
            artistEntity.setBiography(request.getBiography());
            if (!request.getPhoto().isEmpty()) {
                artistEntity.setPhoto(request.getPhoto().getBytes(StandardCharsets.UTF_8));
            } else {
                artistEntity.setPhoto(null);
            }

            ArtistEntity savedArtist = artistRepository.save(artistEntity);
            responseObserver.onNext(convertToGrpcArtist(savedArtist));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid artist ID format")
                    .asRuntimeException());
        } catch (RuntimeException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error updating artist: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    private Artist convertToGrpcArtist(ArtistEntity entity) {
        Artist.Builder builder = Artist.newBuilder()
                .setId(entity.getId().toString())
                .setName(entity.getName())
                .setBiography(entity.getBiography() != null ? entity.getBiography() : "");

        if (entity.getPhoto() != null && entity.getPhoto().length > 0) {
            builder.setPhoto(new String(entity.getPhoto(), StandardCharsets.UTF_8));
        }

        return builder.build();
    }
}
