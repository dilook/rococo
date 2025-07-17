package guru.qa.rococo.test.rest;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.TestArtist;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.service.impl.GatewayApiClient;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static guru.qa.rococo.utils.ResourceUtils.getDataImageBase64FromResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RestTest
public class ArtistRestTest {

    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.api();

    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    @Test
    void shouldGetAllArtistsUsingGatewayApiClient() {
        RestResponsePage<ArtistJson> artists = gatewayApiClient.getAllArtist(0, 4, null);

        assertNotNull(artists);
        assertTrue(artists.getSize() > 1);
    }

    @Test
    @ApiLogin
    @User
    void shouldCreateAndGetArtistUsingGatewayApiClient(@Token String token) {
        ArtistJson artist = new ArtistJson(
                null,
                RandomDataUtils.randomArtistName(),
                RandomDataUtils.randomArtistBiography(),
                getDataImageBase64FromResource("img/lyvr.jpg")
        );
        ArtistJson createdArtist = gatewayApiClient.createArtist(token, artist);

        assertNotNull(createdArtist);
        assertNotNull(createdArtist.id());

        ArtistJson retrievedArtist = gatewayApiClient.getArtistById(createdArtist.id());

        assertNotNull(retrievedArtist);
        assertNotNull(retrievedArtist.id());
        assertEquals(retrievedArtist.name(), createdArtist.name());
        assertEquals(retrievedArtist.biography(), createdArtist.biography());
        assertEquals(retrievedArtist.photo(), createdArtist.photo());
    }

    @Test
    @ApiLogin
    @User
    @TestArtist
    void shouldUpdateArtistUsingGatewayApiClient(@Token String token, ArtistJson createdArtist) {
        ArtistJson updatedArtist = new ArtistJson(
                createdArtist.id(),
                "Updated " + createdArtist.name(),
                createdArtist.biography(),
                createdArtist.photo()
        );

        ArtistJson result = gatewayApiClient.updateArtist(token, updatedArtist);

        assertNotNull(result);
        assertTrue(result.name().startsWith("Updated"));
    }

    @Test
    void shouldNotCreateArtistForUnauthorizedUser() {
        ArtistJson artist = new ArtistJson(
                null,
                RandomDataUtils.randomArtistName(),
                RandomDataUtils.randomArtistBiography(),
                getDataImageBase64FromResource("img/lyvr.jpg")
        );

        // Test with a null token
        AssertionError assertionError = assertThrows(AssertionError.class, () -> gatewayApiClient.createArtist(null, artist));
        assertTrue(assertionError.getMessage().contains("Response code: 401"));

        // Test with an empty token
        assertionError = assertThrows(AssertionError.class, () -> gatewayApiClient.createArtist("", artist));
        assertTrue(assertionError.getMessage().contains("Response code: 401"));

        // Test with invalid token
        assertionError = assertThrows(AssertionError.class, () -> gatewayApiClient.createArtist("Bearer invalid-token", artist));
        assertTrue(assertionError.getMessage().contains("Response code: 401"));
    }

    @Test
    @TestArtist
    void shouldNotUpdateArtistForUnauthorizedUser(ArtistJson createdArtist) {
        ArtistJson updatedArtist = new ArtistJson(
                createdArtist.id(),
                "Updated " + createdArtist.name(),
                createdArtist.biography(),
                createdArtist.photo()
        );

        // Test with a null token
        AssertionError assertionError = assertThrows(AssertionError.class, () -> gatewayApiClient.updateArtist(null, updatedArtist));
        assertTrue(assertionError.getMessage().contains("Response code: 401"));

        // Test with an empty token
        assertionError = assertThrows(AssertionError.class, () -> gatewayApiClient.updateArtist("", updatedArtist));
        assertTrue(assertionError.getMessage().contains("Response code: 401"));

        // Test with invalid token
        assertionError = assertThrows(AssertionError.class, () -> gatewayApiClient.updateArtist("Bearer invalid-token", updatedArtist));
        assertTrue(assertionError.getMessage().contains("Response code: 401"));
    }
}