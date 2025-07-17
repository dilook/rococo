package guru.qa.rococo.test.grpc;

import guru.qa.rococo.grpc.Artist;
import guru.qa.rococo.grpc.CreateArtistRequest;
import guru.qa.rococo.grpc.GetAllArtistsRequest;
import guru.qa.rococo.grpc.GetAllArtistsResponse;
import guru.qa.rococo.grpc.GetArtistByIdRequest;
import guru.qa.rococo.grpc.UpdateArtistRequest;
import guru.qa.rococo.jupiter.annotation.TestArtist;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.utils.RandomDataUtils;
import guru.qa.rococo.utils.ResourceUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ArtistGrpcTest extends BaseGrpcTest {

    @Test
    void shouldReturnedPageableArtists() {
        final GetAllArtistsResponse response = artistBlockingStub.getAllArtists(GetAllArtistsRequest.newBuilder()
                .setPage(0)
                .setSize(4)
                .build()
        );
        final List<Artist> artistsList = response.getArtistsList();
        Assertions.assertEquals(4, artistsList.size());
        Assertions.assertEquals(0, response.getCurrentPage());
        Assertions.assertEquals(4, response.getPageSize());
        Assertions.assertTrue(response.getFirst());
        Assertions.assertFalse(response.getLast());
    }

    @Test
    @TestArtist
    void shouldReturnAllArtistsWithNameFilter(ArtistJson createdArtist) {
        final GetAllArtistsResponse response = artistBlockingStub.getAllArtists(GetAllArtistsRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .setName(createdArtist.name())
                .build()
        );
        final List<Artist> artistsList = response.getArtistsList();
        Assertions.assertFalse(artistsList.isEmpty());
        artistsList.forEach(artist ->
                Assertions.assertTrue(artist.getName().toLowerCase().contains(createdArtist.name().toLowerCase()))
        );
    }

    @Test
    void shouldReturnAllArtistsWithSorting() {
        final GetAllArtistsResponse response = artistBlockingStub.getAllArtists(GetAllArtistsRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .addSort("name")
                .build()
        );
        final List<Artist> artistsList = response.getArtistsList();
        Assertions.assertFalse(artistsList.isEmpty());
        // Verify that artists are sorted by name
        for (int i = 1; i < artistsList.size(); i++) {
            Assertions.assertTrue(
                    artistsList.get(i - 1).getName().compareTo(artistsList.get(i).getName()) <= 0,
                    "Artist '" + artistsList.get(i - 1).getName() + "' should be before '" + artistsList.get(i).getName() + "' in the list"
            );
        }
    }

    @Test
    void shouldReturnLastPageOfArtists() {
        final GetAllArtistsResponse firstResponse = artistBlockingStub.getAllArtists(GetAllArtistsRequest.newBuilder()
                .setPage(0)
                .setSize(4)
                .build()
        );

        int lastPage = firstResponse.getTotalPages() - 1;

        final GetAllArtistsResponse response = artistBlockingStub.getAllArtists(GetAllArtistsRequest.newBuilder()
                .setPage(lastPage)
                .setSize(4)
                .build()
        );

        Assertions.assertEquals(lastPage, response.getCurrentPage());
        Assertions.assertFalse(response.getFirst());
        Assertions.assertTrue(response.getLast());
    }

    @Test
    @TestArtist
    void shouldReturnArtistById(ArtistJson createdArtist) {
        String artistId = createdArtist.id().toString();

        final Artist artist = artistBlockingStub.getArtistById(GetArtistByIdRequest.newBuilder()
                .setId(artistId)
                .build()
        );

        Assertions.assertNotNull(artist);
        Assertions.assertEquals(artistId, artist.getId());
        Assertions.assertEquals(createdArtist.name(), artist.getName());
        Assertions.assertEquals(createdArtist.biography(), artist.getBiography());
    }

    @Test
    void shouldCreateArtist() {
        String artistPhoto = ResourceUtils.getDataImageBase64FromResource("img/artist.jpg");
        String randomArtistName = RandomDataUtils.randomArtistName();
        final Artist createdArtist = artistBlockingStub.createArtist(CreateArtistRequest.newBuilder()
                .setName(randomArtistName)
                .setBiography("Test artist biography")
                .setPhoto(artistPhoto)
                .build()
        );

        Assertions.assertNotNull(createdArtist);
        Assertions.assertNotNull(createdArtist.getId());
        Assertions.assertEquals(randomArtistName, createdArtist.getName());
        Assertions.assertEquals("Test artist biography", createdArtist.getBiography());
        Assertions.assertEquals(artistPhoto, createdArtist.getPhoto());
    }

    @Test
    @TestArtist
    void shouldUpdateArtist(ArtistJson createdArtist) {
        String newArtistName = RandomDataUtils.randomArtistName();
        final Artist updatedArtist = artistBlockingStub.updateArtist(UpdateArtistRequest.newBuilder()
                .setId(createdArtist.id().toString())
                .setName(newArtistName)
                .setBiography("Updated biography")
                .setPhoto("updated-photo.jpg")
                .build()
        );

        Assertions.assertNotNull(updatedArtist);
        Assertions.assertEquals(createdArtist.id().toString(), updatedArtist.getId());
        Assertions.assertEquals(newArtistName, updatedArtist.getName());
        Assertions.assertEquals("Updated biography", updatedArtist.getBiography());
        Assertions.assertEquals("updated-photo.jpg", updatedArtist.getPhoto());
    }

    @Test
    void shouldReturnEmptyListWhenNoArtistsMatchFilter() {
        final GetAllArtistsResponse response = artistBlockingStub.getAllArtists(GetAllArtistsRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .setName("NonExistentArtistName12345")
                .build()
        );

        Assertions.assertTrue(response.getArtistsList().isEmpty());
        Assertions.assertEquals(0, response.getTotalElements());
        Assertions.assertEquals(0, response.getTotalPages());
    }
}