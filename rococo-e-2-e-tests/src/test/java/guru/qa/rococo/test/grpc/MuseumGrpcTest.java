package guru.qa.rococo.test.grpc;

import guru.qa.rococo.grpc.Country;
import guru.qa.rococo.grpc.CreateMuseumRequest;
import guru.qa.rococo.grpc.Geo;
import guru.qa.rococo.grpc.GetAllMuseumsRequest;
import guru.qa.rococo.grpc.GetAllMuseumsResponse;
import guru.qa.rococo.grpc.GetCountryRequest;
import guru.qa.rococo.grpc.GetMuseumByIdRequest;
import guru.qa.rococo.grpc.Museum;
import guru.qa.rococo.grpc.UpdateMuseumRequest;
import guru.qa.rococo.jupiter.annotation.TestMuseum;
import guru.qa.rococo.jupiter.annotation.TestMuseums;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.utils.RandomDataUtils;
import guru.qa.rococo.utils.ResourceUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MuseumGrpcTest extends BaseGrpcTest {

    @Test
    void shouldReturnedPageableMuseums() {
        final GetAllMuseumsResponse response = museumBlockingStub.getAllMuseums(GetAllMuseumsRequest.newBuilder()
                .setPage(0)
                .setSize(4)
                .build()
        );
        final List<Museum> museumsList = response.getMuseumsList();
        Assertions.assertEquals(4, museumsList.size());
        Assertions.assertEquals(0, response.getCurrentPage());
        Assertions.assertEquals(4, response.getPageSize());
        Assertions.assertTrue(response.getFirst());
        Assertions.assertFalse(response.getLast());
    }

    @Test
    @TestMuseum
    void shouldReturnAllMuseumsWithTitleFilter(MuseumJson createdMuseum) {
        final GetAllMuseumsResponse response = museumBlockingStub.getAllMuseums(GetAllMuseumsRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .setTitle(createdMuseum.title())
                .build()
        );
        final List<Museum> museumsList = response.getMuseumsList();
        Assertions.assertFalse(museumsList.isEmpty());
        museumsList.forEach(museum ->
                Assertions.assertTrue(museum.getTitle().toLowerCase().contains(createdMuseum.title()))
        );
    }

    @Test
    void shouldReturnAllMuseumsWithSorting() {
        final GetAllMuseumsResponse response = museumBlockingStub.getAllMuseums(GetAllMuseumsRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .addSort("title")
                .build()
        );
        final List<Museum> museumsList = response.getMuseumsList();
        Assertions.assertFalse(museumsList.isEmpty());
        // Verify that museums are sorted by title
        for (int i = 1; i < museumsList.size(); i++) {
            Assertions.assertTrue(
                    museumsList.get(i - 1).getTitle().compareTo(museumsList.get(i).getTitle()) <= 0,
                    "Museum '" + museumsList.get(i - 1).getTitle() + "' should be before '" + museumsList.get(i).getTitle() + "' in the list"
            );
        }
    }

    @Test
    @TestMuseums(count = 5)
    void shouldReturnLastPageOfMuseums() {
        final GetAllMuseumsResponse firstResponse = museumBlockingStub.getAllMuseums(GetAllMuseumsRequest.newBuilder()
                .setPage(0)
                .setSize(4)
                .build()
        );

        int lastPage = firstResponse.getTotalPages() - 1;

        final GetAllMuseumsResponse response = museumBlockingStub.getAllMuseums(GetAllMuseumsRequest.newBuilder()
                .setPage(lastPage)
                .setSize(4)
                .build()
        );

        Assertions.assertEquals(lastPage, response.getCurrentPage());
        Assertions.assertFalse(response.getFirst());
        Assertions.assertTrue(response.getLast());
    }

    @Test
    @TestMuseum
    void shouldReturnMuseumById(MuseumJson createdMuseum) {
        String museumId = createdMuseum.id().toString();

        final Museum museum = museumBlockingStub.getMuseumById(GetMuseumByIdRequest.newBuilder()
                .setId(museumId)
                .build()
        );

        Assertions.assertNotNull(museum);
        Assertions.assertEquals(museumId, museum.getId());
        Assertions.assertEquals(createdMuseum.title(), museum.getTitle());
        Assertions.assertEquals(createdMuseum.description(), museum.getDescription());
    }

    @Test
    void shouldCreateMuseum() {
        final Country country = countryBlockingStub.getCountry(GetCountryRequest.newBuilder()
                .setName("Япония")
                .build());

        final Geo geo = Geo.newBuilder()
                .setCity("Токио")
                .setCountry(country)
                .build();

        String lyvrPhoto = ResourceUtils.getDataImageBase64FromResource("img/lyvr.jpg");
        String randomMuseumName = RandomDataUtils.randomMuseumName();
        final Museum createdMuseum = museumBlockingStub.createMuseum(CreateMuseumRequest.newBuilder()
                .setTitle(randomMuseumName)
                .setDescription("Test museum description")
                .setPhoto(lyvrPhoto)
                .setGeo(geo)
                .build()
        );

        Assertions.assertNotNull(createdMuseum);
        Assertions.assertNotNull(createdMuseum.getId());
        Assertions.assertEquals(randomMuseumName, createdMuseum.getTitle());
        Assertions.assertEquals("Test museum description", createdMuseum.getDescription());
        Assertions.assertEquals(lyvrPhoto, createdMuseum.getPhoto());
        Assertions.assertEquals("Токио", createdMuseum.getGeo().getCity());
        Assertions.assertEquals("Япония", createdMuseum.getGeo().getCountry().getName());
    }

    @Test
    @TestMuseum
    void shouldUpdateMuseum(MuseumJson createdMuseum) {
        final Geo updatedGeo = Geo.newBuilder()
                .setCity("Saint Petersburg")
                .setCountry(createdMuseum.geo().country().toGrpc())
                .build();

        String newMuseumTitle = RandomDataUtils.randomMuseumName();
        final Museum updatedMuseum = museumBlockingStub.updateMuseum(UpdateMuseumRequest.newBuilder()
                .setId(createdMuseum.id().toString())
                .setTitle(newMuseumTitle)
                .setDescription("Updated description")
                .setPhoto("updated-photo.jpg")
                .setGeo(updatedGeo)
                .build()
        );

        Assertions.assertNotNull(updatedMuseum);
        Assertions.assertEquals(createdMuseum.id().toString(), updatedMuseum.getId());
        Assertions.assertEquals(newMuseumTitle, updatedMuseum.getTitle());
        Assertions.assertEquals("Updated description", updatedMuseum.getDescription());
        Assertions.assertEquals("updated-photo.jpg", updatedMuseum.getPhoto());
        Assertions.assertEquals("Saint Petersburg", updatedMuseum.getGeo().getCity());
        Assertions.assertEquals(createdMuseum.geo().country().name(), updatedMuseum.getGeo().getCountry().getName());
    }

    @Test
    void shouldReturnEmptyListWhenNoMuseumsMatchFilter() {
        final GetAllMuseumsResponse response = museumBlockingStub.getAllMuseums(GetAllMuseumsRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .setTitle("NonExistentMuseumName12345")
                .build()
        );

        Assertions.assertTrue(response.getMuseumsList().isEmpty());
        Assertions.assertEquals(0, response.getTotalElements());
        Assertions.assertEquals(0, response.getTotalPages());
    }
}
