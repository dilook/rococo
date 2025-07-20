package guru.qa.rococo.service;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.grpc.CreatePaintingRequest;
import guru.qa.rococo.grpc.GetAllPaintingsRequest;
import guru.qa.rococo.grpc.GetPaintingByIdRequest;
import guru.qa.rococo.grpc.GetPaintingsByArtistIdRequest;
import guru.qa.rococo.grpc.Painting;
import guru.qa.rococo.grpc.RococoPaintingServiceGrpc;
import guru.qa.rococo.grpc.UpdatePaintingRequest;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.model.rest.PaintingJson;
import guru.qa.rococo.utils.GrpcConsoleInterceptor;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.qameta.allure.Step;
import io.qameta.allure.grpc.AllureGrpc;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

import static guru.qa.rococo.utils.GrpcUtils.safe;

public class PaintingGrpcClient {

    private final Config CFG = Config.getInstance();

    private final Channel channel = ManagedChannelBuilder
            .forAddress(CFG.paintingGrpcAddress(), CFG.paintingGrpcPort())
            .intercept(new AllureGrpc())
            .intercept(new GrpcConsoleInterceptor())
            .usePlaintext()
            .build();

    private final ArtistGrpcClient artistGrpcClient = new ArtistGrpcClient();
    private final MuseumGrpcClient museumGrpcClient = new MuseumGrpcClient();

    private final RococoPaintingServiceGrpc.RococoPaintingServiceBlockingStub paintingServiceStub
            = RococoPaintingServiceGrpc.newBlockingStub(channel);

    @Step("Получить все картины со страницы {page}, размер {size}, заголовок '{title}' через gRPC")
    public List<PaintingJson> getAllPaintings(int page, int size, String title) {
        return getAllPaintings(page, size, title, null);
    }

    @Step("Получить все картины со страницы {page}, размер {size}, заголовок '{title}' с сортировкой через gRPC")
    public List<PaintingJson> getAllPaintings(int page, int size, String title, List<String> sort) {
        GetAllPaintingsRequest.Builder requestBuilder = GetAllPaintingsRequest.newBuilder()
                .setPage(page)
                .setSize(size)
                .setTitle(title != null ? title : "");

        if (sort != null && !sort.isEmpty()) {
            requestBuilder.addAllSort(sort);
        }

        GetAllPaintingsRequest request = requestBuilder.build();

        return paintingServiceStub.getAllPaintings(request)
                .getPaintingsList().stream()
                .map(this::convertFromGrpcPainting)
                .toList();
    }

    @Step("Получить картину по ID '{id}' через gRPC")
    public PaintingJson getPaintingById(UUID id) {
        GetPaintingByIdRequest request = GetPaintingByIdRequest.newBuilder()
                .setId(id.toString())
                .build();

        Painting painting = paintingServiceStub.getPaintingById(request);
        return convertFromGrpcPainting(painting);
    }

    @Step("Получить картины художника '{artistId}' со страницы {page}, размер {size} через gRPC")
    public List<PaintingJson> getPaintingsByArtistId(UUID artistId, int page, int size) {
        return getPaintingsByArtistId(artistId, page, size, null);
    }

    @Step("Получить картины художника '{artistId}' со страницы {page}, размер {size} с сортировкой через gRPC")
    public List<PaintingJson> getPaintingsByArtistId(UUID artistId, int page, int size, List<String> sort) {
        GetPaintingsByArtistIdRequest.Builder requestBuilder = GetPaintingsByArtistIdRequest.newBuilder()
                .setArtistId(artistId.toString())
                .setPage(page)
                .setSize(size);

        if (sort != null && !sort.isEmpty()) {
            requestBuilder.addAllSort(sort);
        }

        GetPaintingsByArtistIdRequest request = requestBuilder.build();

        return paintingServiceStub.getPaintingsByArtistId(request)
                .getPaintingsList().stream()
                .map(this::convertFromGrpcPainting)
                .toList();
    }

    @Step("Создать картину через gRPC")
    public PaintingJson createPainting(@Nonnull PaintingJson paintingJson) {
        CreatePaintingRequest.Builder requestBuilder = CreatePaintingRequest.newBuilder()
                .setTitle(paintingJson.title())
                .setDescription(safe(paintingJson.description()))
                .setContent(safe(paintingJson.content()));

        if (paintingJson.artist() != null && paintingJson.artist().id() != null) {
            requestBuilder.setArtistId(paintingJson.artist().id().toString());
        }

        if (paintingJson.museum() != null && paintingJson.museum().id() != null) {
            requestBuilder.setMuseumId(paintingJson.museum().id().toString());
        }

        Painting painting = paintingServiceStub.createPainting(requestBuilder.build());
        return convertFromGrpcPainting(painting);
    }

    @Step("Обновить картину через gRPC")
    public PaintingJson updatePainting(PaintingJson paintingJson) {
        UpdatePaintingRequest.Builder requestBuilder = UpdatePaintingRequest.newBuilder()
                .setId(paintingJson.id().toString())
                .setTitle(paintingJson.title())
                .setDescription(paintingJson.description() != null ? paintingJson.description() : "")
                .setContent(paintingJson.content() != null ? paintingJson.content() : "");

        if (paintingJson.artist() != null && paintingJson.artist().id() != null) {
            requestBuilder.setArtistId(paintingJson.artist().id().toString());
        }

        if (paintingJson.museum() != null && paintingJson.museum().id() != null) {
            requestBuilder.setMuseumId(paintingJson.museum().id().toString());
        }

        Painting painting = paintingServiceStub.updatePainting(requestBuilder.build());
        return convertFromGrpcPainting(painting);
    }

    private PaintingJson convertFromGrpcPainting(Painting painting) {
        ArtistJson artist = null;
        painting.getArtistId();
        if (!painting.getArtistId().isEmpty()) {
            artist = artistGrpcClient.getArtistById(UUID.fromString(painting.getArtistId()));
        }

        MuseumJson museum = null;
        painting.getMuseumId();
        if (!painting.getMuseumId().isEmpty()) {
            museum = museumGrpcClient.getMuseumById(UUID.fromString(painting.getMuseumId()));
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
