package guru.qa.rococo.test.grpc;

import guru.qa.rococo.jupiter.annotation.TestArtist;
import guru.qa.rococo.jupiter.annotation.TestMuseum;
import guru.qa.rococo.jupiter.annotation.TestPainting;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.model.rest.PaintingJson;
import guru.qa.rococo.service.PaintingGrpcClient;
import guru.qa.rococo.utils.RandomDataUtils;
import guru.qa.rococo.utils.ResourceUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static guru.qa.rococo.utils.ResourceUtils.getDataImageBase64FromResource;

public class PaintingGrpcTest extends BaseGrpcTest {

    private final PaintingGrpcClient paintingGrpcClient = new PaintingGrpcClient();

    @Test
    void shouldReturnAllPaintings() {
        List<PaintingJson> paintings = paintingGrpcClient.getAllPaintings(0, 4, null);

        Assertions.assertNotNull(paintings);
        Assertions.assertFalse(paintings.isEmpty());
        Assertions.assertTrue(paintings.size() >= 4);
    }

    @Test
    @TestPainting
    void shouldReturnAllPaintingsWithTitleFilter(PaintingJson createdPainting) {
        List<PaintingJson> paintings = paintingGrpcClient.getAllPaintings(0, 10, createdPainting.title());

        Assertions.assertFalse(paintings.isEmpty());
        paintings.forEach(painting ->
                Assertions.assertTrue(painting.title().toLowerCase().contains(createdPainting.title().toLowerCase()))
        );
    }

    @Test
    @TestPainting
    void shouldReturnPaintingById(PaintingJson createdPainting) {
        PaintingJson painting = paintingGrpcClient.getPaintingById(createdPainting.id());

        Assertions.assertNotNull(painting);
        Assertions.assertEquals(createdPainting.id(), painting.id());
        Assertions.assertEquals(createdPainting.title(), painting.title());
        Assertions.assertEquals(createdPainting.description(), painting.description());
    }

    @Test
    @TestPainting
    void shouldReturnPaintingsByArtistId(PaintingJson createdPainting) {
        final PaintingJson anotherPainting = new PaintingJson(
                null,
                RandomDataUtils.randomPaintingName(),
                RandomDataUtils.randomSentence(20),
                getDataImageBase64FromResource("img/painting.jpg"),
                createdPainting.museum(),
                createdPainting.artist()
        );
        paintingGrpcClient.createPainting(anotherPainting);

        UUID artistId = createdPainting.artist().id();
        List<PaintingJson> paintings = paintingGrpcClient.getPaintingsByArtistId(artistId, 0, 10);

        Assertions.assertEquals(2, paintings.size());
        paintings.forEach(painting ->
                Assertions.assertEquals(artistId, painting.artist().id())
        );
    }

    @Test
    @TestMuseum
    @TestArtist
    void shouldCreatePainting(MuseumJson createdMuseum, ArtistJson createdArtist) {
        String paintingTitle = RandomDataUtils.randomPaintingName();
        String paintingDescription = "Прекрасная картина, созданная талантливым художником.";
        String paintingContent = ResourceUtils.getDataImageBase64FromResource("img/painting.jpg");

        PaintingJson paintingToCreate = new PaintingJson(
                null,
                paintingTitle,
                paintingDescription,
                paintingContent,
                createdMuseum,
                createdArtist
        );

        PaintingJson createPaintingResponse = paintingGrpcClient.createPainting(paintingToCreate);

        Assertions.assertNotNull(createPaintingResponse);
        Assertions.assertNotNull(createPaintingResponse.id());
        Assertions.assertEquals(paintingTitle, createPaintingResponse.title());
        Assertions.assertEquals(paintingDescription, createPaintingResponse.description());
        Assertions.assertEquals(paintingContent, createPaintingResponse.content());
        Assertions.assertEquals(createdMuseum.id(), createPaintingResponse.museum().id());
        Assertions.assertEquals(createdArtist.id(), createPaintingResponse.artist().id());
    }

    @Test
    @TestPainting
    void shouldUpdatePainting(PaintingJson createdPainting) {
        String updatedTitle = "Updated " + createdPainting.title();
        String updatedDescription = "Updated description for the painting.";

        PaintingJson paintingToUpdate = new PaintingJson(
                createdPainting.id(),
                updatedTitle,
                updatedDescription,
                createdPainting.content(),
                createdPainting.museum(),
                createdPainting.artist()
        );

        PaintingJson updatedPainting = paintingGrpcClient.updatePainting(paintingToUpdate);

        Assertions.assertNotNull(updatedPainting);
        Assertions.assertEquals(createdPainting.id(), updatedPainting.id());
        Assertions.assertEquals(updatedTitle, updatedPainting.title());
        Assertions.assertEquals(updatedDescription, updatedPainting.description());
        Assertions.assertEquals(createdPainting.content(), updatedPainting.content());
        Assertions.assertEquals(createdPainting.museum().id(), updatedPainting.museum().id());
        Assertions.assertEquals(createdPainting.artist().id(), updatedPainting.artist().id());
    }

    @Test
    void shouldReturnEmptyListWhenNoMatchingPaintings() {
        List<PaintingJson> paintings = paintingGrpcClient.getAllPaintings(0, 10, "NonExistentPaintingTitle12345");

        Assertions.assertNotNull(paintings);
        Assertions.assertTrue(paintings.isEmpty());
    }

    @Test
    @TestArtist
    void shouldReturnEmptyListWhenArtistHasNoPaintings(ArtistJson createdArtist) {
        List<PaintingJson> paintings = paintingGrpcClient.getPaintingsByArtistId(createdArtist.id(), 0, 10);
        Assertions.assertTrue(paintings.isEmpty());
    }
}
