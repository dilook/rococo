package guru.qa.rococo.service;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.grpc.Artist;
import guru.qa.rococo.grpc.CreateArtistRequest;
import guru.qa.rococo.grpc.GetAllArtistsRequest;
import guru.qa.rococo.grpc.GetArtistByIdRequest;
import guru.qa.rococo.grpc.RococoArtistServiceGrpc;
import guru.qa.rococo.grpc.UpdateArtistRequest;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.utils.GrpcConsoleInterceptor;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.qameta.allure.grpc.AllureGrpc;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

import static guru.qa.rococo.utils.GrpcUtils.safe;

public class ArtistGrpcClient {

    private final Config CFG = Config.getInstance();

    private final Channel channel = ManagedChannelBuilder
            .forAddress(CFG.artistGrpcAddress(), CFG.artistGrpcPort())
            .intercept(new AllureGrpc())
            .intercept(new GrpcConsoleInterceptor())
            .usePlaintext()
            .build();

    private final RococoArtistServiceGrpc.RococoArtistServiceBlockingStub artistServiceStub
            = RococoArtistServiceGrpc.newBlockingStub(channel);

    public List<ArtistJson> getAllArtists(int page, int size, String name) {
        GetAllArtistsRequest request = GetAllArtistsRequest.newBuilder()
                .setPage(page)
                .setSize(size)
                .setName(name != null ? name : "")
                .build();

        return artistServiceStub.getAllArtists(request)
                .getArtistsList().stream()
                .map(this::convertFromGrpcArtist)
                .toList();
    }

    public ArtistJson getArtistById(UUID id) {
        GetArtistByIdRequest request = GetArtistByIdRequest.newBuilder()
                .setId(id.toString())
                .build();

        Artist artist = artistServiceStub.getArtistById(request);
        return convertFromGrpcArtist(artist);
    }

    public ArtistJson createArtist(@Nonnull ArtistJson artistJson) {
        CreateArtistRequest request = CreateArtistRequest.newBuilder()
                .setName(artistJson.name())
                .setBiography(safe(artistJson.biography()))
                .setPhoto(safe(artistJson.photo()))
                .build();

        Artist artist = artistServiceStub.createArtist(request);
        return convertFromGrpcArtist(artist);
    }

    public ArtistJson updateArtist(ArtistJson artistJson) {
        UpdateArtistRequest request = UpdateArtistRequest.newBuilder()
                .setId(artistJson.id().toString())
                .setName(artistJson.name())
                .setBiography(artistJson.biography() != null ? artistJson.biography() : "")
                .setPhoto(artistJson.photo() != null ? artistJson.photo() : "")
                .build();

        Artist artist = artistServiceStub.updateArtist(request);
        return convertFromGrpcArtist(artist);
    }

    private ArtistJson convertFromGrpcArtist(Artist artist) {
        return new ArtistJson(
                UUID.fromString(artist.getId()),
                artist.getName(),
                artist.getBiography().isEmpty() ? null : artist.getBiography(),
                artist.getPhoto().isEmpty() ? null : artist.getPhoto()
        );
    }
}