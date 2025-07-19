package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.TestArtist;
import guru.qa.rococo.jupiter.annotation.TestMuseum;
import guru.qa.rococo.jupiter.annotation.TestPainting;
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
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static guru.qa.rococo.utils.ResourceUtils.getDataImageBase64FromResource;

@ParametersAreNonnullByDefault
public class PaintingExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(PaintingExtension.class);

    private final PaintingGrpcClient paintingClient = new PaintingGrpcClient();
    private final MuseumGrpcClient museumClient = new MuseumGrpcClient();
    private final ArtistGrpcClient artistClient = new ArtistGrpcClient();

    @Nullable
    public static PaintingJson createdPainting() {
        final ExtensionContext context = TestMethodContextExtension.context();
        return context.getStore(NAMESPACE).get(
                context.getUniqueId(),
                PaintingJson.class
        );
    }

    public static void setPainting(PaintingJson painting) {
        final ExtensionContext context = TestMethodContextExtension.context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                painting
        );
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), TestPainting.class)
                .ifPresent(paintingAnno -> {
                    final PaintingJson painting;
                    MuseumJson museum = createMuseum(paintingAnno.museum());
                    painting = new PaintingJson(
                            null,
                            "".equals(paintingAnno.title()) ? RandomDataUtils.randomPaintingName() : paintingAnno.title(),
                            paintingAnno.description(),
                            getDataImageBase64FromResource(paintingAnno.content()),
                            museum,
                            createArtist(paintingAnno.artist())
                    );
                    PaintingJson createdPainting = paintingClient.createPainting(painting);
                    setPainting(createdPainting);
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(PaintingJson.class);
    }

    @Override
    public PaintingJson resolveParameter(ParameterContext parameterContext,
                                         ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdPainting();
    }

    @Nonnull
    private MuseumJson createMuseum(TestMuseum annoMuseum) {
        final MuseumJson museum = new MuseumJson(
                null,
                "".equals(annoMuseum.title()) ? RandomDataUtils.randomMuseumName() : annoMuseum.title(),
                annoMuseum.description(),
                getDataImageBase64FromResource(annoMuseum.imagePath()),
                new MuseumJson.Geo(
                        annoMuseum.city(),
                        museumClient.getCountryByName(annoMuseum.country())
                )
        );
        return museumClient.createMuseum(museum);
    }

    @Nonnull
    private ArtistJson createArtist(TestArtist annoArtist) {
        final ArtistJson artist = new ArtistJson(
                null,
                "".equals(annoArtist.name()) ? RandomDataUtils.randomArtistName() : annoArtist.name(),
                annoArtist.biography(),
                getDataImageBase64FromResource(annoArtist.photo())
        );
        return artistClient.createArtist(artist);
    }
}