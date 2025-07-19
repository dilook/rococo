package guru.qa.rococo.test.rest;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.TestArtist;
import guru.qa.rococo.jupiter.annotation.TestMuseum;
import guru.qa.rococo.jupiter.annotation.TestPainting;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.model.rest.PaintingJson;
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
public class PaintingRestTest {

    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.api();

    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    @Test
    void shouldGetAllPaintingsUsingGatewayApiClient() {
        RestResponsePage<PaintingJson> paintings = gatewayApiClient.getAllPainting(0, 4, null);

        assertNotNull(paintings);
        assertTrue(paintings.getSize() > 1);
    }

    @Test
    @ApiLogin
    @User
    @TestMuseum
    @TestArtist
    void shouldCreateAndGetPaintingUsingGatewayApiClient(@Token String token, MuseumJson createdMuseum, ArtistJson createdArtist) {
        PaintingJson painting = new PaintingJson(
                null,
                RandomDataUtils.randomPaintingName(),
                "Прекрасная картина, созданная талантливым художником.",
                getDataImageBase64FromResource("img/painting.jpg"),
                createdMuseum,
                createdArtist
        );
        PaintingJson createdPainting = gatewayApiClient.createPainting(token, painting);

        assertNotNull(createdPainting);
        assertNotNull(createdPainting.id());

        PaintingJson retrievedPainting = gatewayApiClient.getPaintingById(createdPainting.id());

        assertNotNull(retrievedPainting);
        assertNotNull(retrievedPainting.id());
        assertEquals(retrievedPainting.title(), createdPainting.title());
        assertEquals(retrievedPainting.description(), createdPainting.description());
        assertEquals(retrievedPainting.content(), createdPainting.content());
        assertEquals(retrievedPainting.museum().id(), createdPainting.museum().id());
        assertEquals(retrievedPainting.artist().id(), createdPainting.artist().id());
    }

    @Test
    @ApiLogin
    @User
    @TestPainting
    void shouldUpdatePaintingUsingGatewayApiClient(@Token String token, PaintingJson createdPainting) {
        PaintingJson updatedPainting = new PaintingJson(
                createdPainting.id(),
                "Updated " + createdPainting.title(),
                createdPainting.description(),
                createdPainting.content(),
                createdPainting.museum(),
                createdPainting.artist()
        );

        PaintingJson result = gatewayApiClient.updatePainting(token, updatedPainting);

        assertNotNull(result);
        assertTrue(result.title().startsWith("Updated"));
    }

    @Test
    void shouldNotCreatePaintingForUnauthorizedUser() {
        PaintingJson painting = new PaintingJson(
                null,
                RandomDataUtils.randomPaintingName(),
                "Прекрасная картина, созданная талантливым художником.",
                getDataImageBase64FromResource("img/painting.jpg"),
                new MuseumJson(null, "Test Museum", "Test Description", null, null),
                new ArtistJson(null, "Test Artist", "Test Biography", null)
        );

        // Test with a null token
        AssertionError assertionError = assertThrows(AssertionError.class, () -> gatewayApiClient.createPainting(null, painting));
        assertTrue(assertionError.getMessage().contains("Response code: 401"));

        // Test with an empty token
        assertionError = assertThrows(AssertionError.class, () -> gatewayApiClient.createPainting("", painting));
        assertTrue(assertionError.getMessage().contains("Response code: 401"));

        // Test with invalid token
        assertionError = assertThrows(AssertionError.class, () -> gatewayApiClient.createPainting("Bearer invalid-token", painting));
        assertTrue(assertionError.getMessage().contains("Response code: 401"));
    }

    @Test
    @TestPainting
    void shouldNotUpdatePaintingForUnauthorizedUser(PaintingJson createdPainting) {
        PaintingJson updatedPainting = new PaintingJson(
                createdPainting.id(),
                "Updated " + createdPainting.title(),
                createdPainting.description(),
                createdPainting.content(),
                createdPainting.museum(),
                createdPainting.artist()
        );

        // Test with a null token
        AssertionError assertionError = assertThrows(AssertionError.class, () -> gatewayApiClient.updatePainting(null, updatedPainting));
        assertTrue(assertionError.getMessage().contains("Response code: 401"));

        // Test with an empty token
        assertionError = assertThrows(AssertionError.class, () -> gatewayApiClient.updatePainting("", updatedPainting));
        assertTrue(assertionError.getMessage().contains("Response code: 401"));

        // Test with invalid token
        assertionError = assertThrows(AssertionError.class, () -> gatewayApiClient.updatePainting("Bearer invalid-token", updatedPainting));
        assertTrue(assertionError.getMessage().contains("Response code: 401"));
    }
}