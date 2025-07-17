package guru.qa.rococo.service;

import guru.qa.rococo.data.PaintingEntity;
import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.grpc.CreatePaintingRequest;
import guru.qa.rococo.grpc.GetAllPaintingsRequest;
import guru.qa.rococo.grpc.GetAllPaintingsResponse;
import guru.qa.rococo.grpc.GetPaintingByIdRequest;
import guru.qa.rococo.grpc.GetPaintingsByArtistIdRequest;
import guru.qa.rococo.grpc.Painting;
import guru.qa.rococo.grpc.RococoPaintingServiceGrpc;
import guru.qa.rococo.grpc.UpdatePaintingRequest;
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
public class PaintingGrpcService extends RococoPaintingServiceGrpc.RococoPaintingServiceImplBase {

    private final PaintingRepository paintingRepository;

    public PaintingGrpcService(PaintingRepository paintingRepository) {
        this.paintingRepository = paintingRepository;
    }

    @Override
    public void getAllPaintings(GetAllPaintingsRequest request, StreamObserver<GetAllPaintingsResponse> responseObserver) {
        try {
            Sort sort = GrpcUtils.createSortFromList(request.getSortList());
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
            Page<PaintingEntity> paintings = !request.getTitle().isEmpty()
                    ? paintingRepository.findByTitleContainingIgnoreCase(request.getTitle(), pageable)
                    : paintingRepository.findAll(pageable);

            GetAllPaintingsResponse.Builder responseBuilder = GetAllPaintingsResponse.newBuilder()
                    .setTotalPages(paintings.getTotalPages())
                    .setTotalElements(paintings.getTotalElements())
                    .setCurrentPage(paintings.getNumber())
                    .setPageSize(paintings.getSize())
                    .setFirst(paintings.isFirst())
                    .setLast(paintings.isLast());

            paintings.getContent().forEach(painting -> 
                responseBuilder.addPaintings(convertToGrpcPainting(painting))
            );

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error retrieving paintings: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getPaintingById(GetPaintingByIdRequest request, StreamObserver<Painting> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());
            PaintingEntity painting = paintingRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Painting not found with id: " + id));

            responseObserver.onNext(convertToGrpcPainting(painting));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid painting ID format")
                    .asRuntimeException());
        } catch (RuntimeException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error retrieving painting: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getPaintingsByArtistId(GetPaintingsByArtistIdRequest request, StreamObserver<GetAllPaintingsResponse> responseObserver) {
        try {
            UUID artistId = UUID.fromString(request.getArtistId());
            Sort sort = GrpcUtils.createSortFromList(request.getSortList());
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
            Page<PaintingEntity> paintings = paintingRepository.findByArtistId(artistId, pageable);

            GetAllPaintingsResponse.Builder responseBuilder = GetAllPaintingsResponse.newBuilder()
                    .setTotalPages(paintings.getTotalPages())
                    .setTotalElements(paintings.getTotalElements())
                    .setCurrentPage(paintings.getNumber())
                    .setPageSize(paintings.getSize())
                    .setFirst(paintings.isFirst())
                    .setLast(paintings.isLast());

            paintings.getContent().forEach(painting -> 
                responseBuilder.addPaintings(convertToGrpcPainting(painting))
            );

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid artist ID format")
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error retrieving paintings by artist: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void createPainting(CreatePaintingRequest request, StreamObserver<Painting> responseObserver) {
        try {
            UUID artistId = UUID.fromString(request.getArtistId());

            PaintingEntity paintingEntity = new PaintingEntity();
            paintingEntity.setTitle(request.getTitle());
            paintingEntity.setDescription(request.getDescription());
            paintingEntity.setArtistId(artistId);

            if (!request.getContent().isEmpty()) {
                paintingEntity.setContent(request.getContent().getBytes(StandardCharsets.UTF_8));
            }

            if (!request.getMuseumId().isEmpty()) {
                UUID museumId = UUID.fromString(request.getMuseumId());
                paintingEntity.setMuseumId(museumId);
            }

            PaintingEntity savedPainting = paintingRepository.save(paintingEntity);
            responseObserver.onNext(convertToGrpcPainting(savedPainting));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid ID format")
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error creating painting: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void updatePainting(UpdatePaintingRequest request, StreamObserver<Painting> responseObserver) {
        try {
            UUID id = UUID.fromString(request.getId());
            PaintingEntity paintingEntity = paintingRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Painting not found with id: " + id));

            paintingEntity.setTitle(request.getTitle());
            paintingEntity.setDescription(request.getDescription());

            UUID artistId = UUID.fromString(request.getArtistId());
            paintingEntity.setArtistId(artistId);

            if (!request.getContent().isEmpty()) {
                paintingEntity.setContent(request.getContent().getBytes(StandardCharsets.UTF_8));
            } else {
                paintingEntity.setContent(null);
            }

            if (!request.getMuseumId().isEmpty()) {
                UUID museumId = UUID.fromString(request.getMuseumId());
                paintingEntity.setMuseumId(museumId);
            } else {
                paintingEntity.setMuseumId(null);
            }

            PaintingEntity savedPainting = paintingRepository.save(paintingEntity);
            responseObserver.onNext(convertToGrpcPainting(savedPainting));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid ID format")
                    .asRuntimeException());
        } catch (RuntimeException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error updating painting: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    private Painting convertToGrpcPainting(PaintingEntity entity) {
        Painting.Builder builder = Painting.newBuilder()
                .setId(entity.getId().toString())
                .setTitle(entity.getTitle())
                .setDescription(entity.getDescription() != null ? entity.getDescription() : "")
                .setArtistId(entity.getArtistId().toString());

        if (entity.getContent() != null && entity.getContent().length > 0) {
            builder.setContent(new String(entity.getContent(), StandardCharsets.UTF_8));
        }

        if (entity.getMuseumId() != null) {
            builder.setMuseumId(entity.getMuseumId().toString());
        }

        return builder.build();
    }
}
