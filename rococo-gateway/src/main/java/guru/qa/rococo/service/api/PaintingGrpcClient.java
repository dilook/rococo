package guru.qa.rococo.service.api;

import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.grpc.CreatePaintingRequest;
import guru.qa.rococo.grpc.GetAllPaintingsRequest;
import guru.qa.rococo.grpc.GetAllPaintingsResponse;
import guru.qa.rococo.grpc.GetPaintingByIdRequest;
import guru.qa.rococo.grpc.GetPaintingsByArtistIdRequest;
import guru.qa.rococo.grpc.Painting;
import guru.qa.rococo.grpc.RococoPaintingServiceGrpc;
import guru.qa.rococo.grpc.UpdatePaintingRequest;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.model.PaintingJson;
import io.grpc.StatusRuntimeException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PaintingGrpcClient {

    private final RococoPaintingServiceGrpc.RococoPaintingServiceBlockingStub paintingServiceStub;
    private final ArtistGrpcClient artistGrpcClient;
    private final MuseumGrpcClient museumGrpcClient;

    public PaintingGrpcClient(RococoPaintingServiceGrpc.RococoPaintingServiceBlockingStub paintingServiceStub,
                              ArtistGrpcClient artistGrpcClient,
                              MuseumGrpcClient museumGrpcClient) {
        this.paintingServiceStub = paintingServiceStub;
        this.artistGrpcClient = artistGrpcClient;
        this.museumGrpcClient = museumGrpcClient;
    }

    public Page<PaintingJson> getAllPainting(Pageable pageable, String title) {
        try {
            GetAllPaintingsRequest.Builder requestBuilder = GetAllPaintingsRequest.newBuilder()
                    .setPage(pageable.getPageNumber())
                    .setSize(pageable.getPageSize());

            if (title != null && !title.isEmpty()) {
                requestBuilder.setTitle(title);
            }

            // Add sort information
            pageable.getSort().forEach(order -> {
                String sortStr = order.isDescending() ? "-" + order.getProperty() : order.getProperty();
                requestBuilder.addSort(sortStr);
            });

            GetAllPaintingsResponse response = paintingServiceStub.getAllPaintings(requestBuilder.build());

            List<PaintingJson> paintings = response.getPaintingsList().stream()
                    .map(this::convertToPaintingJson)
                    .toList();

            return new PageImpl<>(paintings, pageable, response.getTotalElements());
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Error retrieving paintings: " + e.getMessage(), e);
        }
    }

    public PaintingJson getPaintingById(UUID id) {
        try {
            GetPaintingByIdRequest request = GetPaintingByIdRequest.newBuilder()
                    .setId(id.toString())
                    .build();

            Painting painting = paintingServiceStub.getPaintingById(request);
            return convertToPaintingJson(painting);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == io.grpc.Status.Code.NOT_FOUND) {
                throw new NotFoundException("Painting not found with id: " + id);
            }
            throw new RuntimeException("Error retrieving painting: " + e.getMessage(), e);
        }
    }

    public Page<PaintingJson> getPaintingsByArtistId(UUID artistId, Pageable pageable) {
        try {
            GetPaintingsByArtistIdRequest.Builder requestBuilder = GetPaintingsByArtistIdRequest.newBuilder()
                    .setArtistId(artistId.toString())
                    .setPage(pageable.getPageNumber())
                    .setSize(pageable.getPageSize());

            // Add sort information
            pageable.getSort().forEach(order -> {
                String sortStr = order.isDescending() ? "-" + order.getProperty() : order.getProperty();
                requestBuilder.addSort(sortStr);
            });

            GetAllPaintingsResponse response = paintingServiceStub.getPaintingsByArtistId(requestBuilder.build());

            List<PaintingJson> paintings = response.getPaintingsList().stream()
                    .map(this::convertToPaintingJson)
                    .toList();

            return new PageImpl<>(paintings, pageable, response.getTotalElements());
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Error retrieving paintings by artist: " + e.getMessage(), e);
        }
    }

    public PaintingJson updatePainting(PaintingJson paintingJson) {
        try {
            UpdatePaintingRequest.Builder requestBuilder = UpdatePaintingRequest.newBuilder()
                    .setId(paintingJson.id().toString())
                    .setTitle(paintingJson.title())
                    .setDescription(paintingJson.description() != null ? paintingJson.description() : "");

            if (paintingJson.artist() != null) {
                requestBuilder.setArtistId(paintingJson.artist().id().toString());
            } else {
                throw new IllegalArgumentException("Artist is required for a painting");
            }

            if (paintingJson.content() != null) {
                requestBuilder.setContent(paintingJson.content());
            }

            if (paintingJson.museum() != null && paintingJson.museum().id() != null) {
                requestBuilder.setMuseumId(paintingJson.museum().id().toString());
            }

            Painting painting = paintingServiceStub.updatePainting(requestBuilder.build());
            return convertToPaintingJson(painting);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == io.grpc.Status.Code.NOT_FOUND) {
                throw new NotFoundException("Painting not found with id: " + paintingJson.id());
            }
            throw new RuntimeException("Error updating painting: " + e.getMessage(), e);
        }
    }

    public PaintingJson createPainting(PaintingJson paintingJson) {
        try {
            CreatePaintingRequest.Builder requestBuilder = CreatePaintingRequest.newBuilder()
                    .setTitle(paintingJson.title())
                    .setDescription(paintingJson.description() != null ? paintingJson.description() : "");

            if (paintingJson.artist() != null) {
                requestBuilder.setArtistId(paintingJson.artist().id().toString());
            } else {
                throw new IllegalArgumentException("Artist is required for a painting");
            }

            if (paintingJson.content() != null) {
                requestBuilder.setContent(paintingJson.content());
            }

            if (paintingJson.museum() != null && paintingJson.museum().id() != null) {
                requestBuilder.setMuseumId(paintingJson.museum().id().toString());
            }

            Painting painting = paintingServiceStub.createPainting(requestBuilder.build());
            return convertToPaintingJson(painting);
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Error creating painting: " + e.getMessage(), e);
        }
    }

    private PaintingJson convertToPaintingJson(Painting painting) {
        ArtistJson artist = null;
        if (!painting.getArtistId().isEmpty()) {
            try {
                artist = artistGrpcClient.getArtistById(UUID.fromString(painting.getArtistId()));
            } catch (Exception e) {
                // If artist service is not available, create a minimal artist object
                artist = new ArtistJson(UUID.fromString(painting.getArtistId()), "Unknown", null, null);
            }
        }

        MuseumJson museum = null;
        if (!painting.getMuseumId().isEmpty()) {
            try {
                museum = museumGrpcClient.getMuseumById(UUID.fromString(painting.getMuseumId()));
            } catch (Exception e) {
                // If museum service is not available, create a minimal museum object
                museum = new MuseumJson(UUID.fromString(painting.getMuseumId()), "Unknown", null, null, null);
            }
        }

        return new PaintingJson(
                UUID.fromString(painting.getId()),
                painting.getTitle(),
                painting.getDescription().isEmpty() ? null : painting.getDescription(),
                painting.getContent().isEmpty() ? null : painting.getContent(),
                museum,
                artist
        );
    }
}
