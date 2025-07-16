package guru.qa.rococo.test.rest;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.CountryJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.service.impl.GatewayApiClient;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static guru.qa.rococo.utils.ResourceUtils.getDataImageBase64FromResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RestTest
public class MuseumRestTest {

    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.api();

    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    @Test
    void shouldGetAllMuseumsUsingGatewayApiClient() {
        RestResponsePage<MuseumJson> museums = gatewayApiClient.getAllMuseum(0, 4, null);

        assertNotNull(museums);
        assertTrue(museums.getSize() > 1);
    }

    @Test
    @ApiLogin
    @User
    void shouldCreateAndGetMuseumUsingGatewayApiClient(@Token String token) {
        CountryJson randomCountry = gatewayApiClient.getRandomCountry(token);
        MuseumJson museum = new MuseumJson(
                null,
                RandomDataUtils.randomMuseumName(),
                RandomDataUtils.randomSentence(12),
                getDataImageBase64FromResource("img/lyvr.jpg"),
                new MuseumJson.Geo(
                        RandomDataUtils.randomCityName(),
                        randomCountry
                )
        );
        MuseumJson createdMuseum = gatewayApiClient.createMuseum(token, museum);

        assertNotNull(createdMuseum);
        assertNotNull(createdMuseum.id());

        MuseumJson retrievedMuseum = gatewayApiClient.getMuseumById(createdMuseum.id());

        assertNotNull(retrievedMuseum);
        assertNotNull(retrievedMuseum.id());
        assertEquals(retrievedMuseum.title(), createdMuseum.title());
    }

    @Test
    @ApiLogin
    @User
    @Museum
    void shouldUpdateMuseumUsingGatewayApiClient(@Token String token, MuseumJson createdMuseum) {
        MuseumJson updatedMuseum = new MuseumJson(
                createdMuseum.id(),
                "Updated " + createdMuseum.title(),
                createdMuseum.description(),
                createdMuseum.photo(),
                createdMuseum.geo()
        );

        MuseumJson result = gatewayApiClient.updateMuseum(token, updatedMuseum);

        assertNotNull(result);
        assertTrue(result.title().startsWith("Updated"));
    }
}