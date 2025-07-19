package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.TestPainting;
import guru.qa.rococo.jupiter.annotation.TestPaintings;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.model.rest.PaintingJson;
import guru.qa.rococo.service.ArtistGrpcClient;
import guru.qa.rococo.service.MuseumGrpcClient;
import guru.qa.rococo.service.PaintingGrpcClient;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static guru.qa.rococo.utils.ResourceUtils.getDataImageBase64FromResource;

@ParametersAreNonnullByDefault
public class PaintingsExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(PaintingsExtension.class);

    private final PaintingGrpcClient paintingClient = new PaintingGrpcClient();
    private final MuseumGrpcClient museumClient = new MuseumGrpcClient();
    private final ArtistGrpcClient artistClient = new ArtistGrpcClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), TestPaintings.class)
                .ifPresent(paintingAnno -> {
                    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), TestPainting.class).ifPresent(painting -> {
                        throw new IllegalStateException("Only @TestPainting or @TestPaintings annotation is allowed per test method!");
                    });

                    if (paintingAnno.count() <= 0) {
                        return;
                    }
                    final List<PaintingJson> listPaintings = new ArrayList<>();
                    for (int i = 0; i < paintingAnno.count(); i++) {
                        PaintingJson paintingJson = createPaintingJson();
                        listPaintings.add(paintingClient.createPainting(paintingJson));
                    }
                    context.getStore(NAMESPACE).put(context.getUniqueId(), listPaintings);
                });
    }

    private PaintingJson createPaintingJson() {
        return new PaintingJson(
                null,
                RandomDataUtils.randomPaintingName(),
                "Прекрасная картина, созданная талантливым художником.",
                getDataImageBase64FromResource("img/painting.jpg"),
                createMuseum(),
                createArtist()
        );
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(List.class) &&
                parameterContext.getParameter().getParameterizedType().toString().contains("PaintingJson");
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PaintingJson> resolveParameter(ParameterContext parameterContext,
                                               ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), List.class);
    }

    @Nonnull
    private MuseumJson createMuseum() {
        final MuseumJson museum = new MuseumJson(
                null,
                RandomDataUtils.randomMuseumName(),
                RandomDataUtils.randomSentence(20),
                getDataImageBase64FromResource("img/lyvr.jpg"),
                new MuseumJson.Geo(
                        RandomDataUtils.randomCityName(),
                        museumClient.getRandomCountry()
                )
        );
        return museumClient.createMuseum(museum);
    }

    @Nonnull
    private ArtistJson createArtist() {
        final ArtistJson artist = new ArtistJson(
                null,
                RandomDataUtils.randomArtistName(),
                RandomDataUtils.randomArtistBiography(),
                getDataImageBase64FromResource("img/artist.jpg")
        );
        return artistClient.createArtist(artist);
    }
}