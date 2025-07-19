package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.TestArtist;
import guru.qa.rococo.jupiter.annotation.TestArtists;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.service.ArtistGrpcClient;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static guru.qa.rococo.utils.ResourceUtils.getDataImageBase64FromResource;

@ParametersAreNonnullByDefault
public class ArtistsExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistsExtension.class);

    private final ArtistGrpcClient artistClient = new ArtistGrpcClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), TestArtists.class)
                .ifPresent(artistsAnno -> {
                    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), TestArtist.class).ifPresent(artist -> {
                        throw new IllegalStateException("Only @TestArtist or @TestArtists annotation is allowed per test method!");
                    });

                    if (artistsAnno.count() <= 0) {
                        return;
                    }
                    final List<ArtistJson> listArtists = new ArrayList<>();
                    for (int i = 0; i < artistsAnno.count(); i++) {
                        ArtistJson artistJson = createArtistJson();
                        listArtists.add(artistClient.createArtist(artistJson));
                    }
                    context.getStore(NAMESPACE).put(context.getUniqueId(), listArtists);
                });
    }

    private ArtistJson createArtistJson() {
        final ArtistJson artist;
        artist = new ArtistJson(
                null,
                RandomDataUtils.randomArtistName(),
                RandomDataUtils.randomArtistBiography(),
                getDataImageBase64FromResource("img/artist.jpg")
        );
        return artist;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(List.class) &&
                parameterContext.getParameter().getParameterizedType().toString().contains("ArtistJson");
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ArtistJson> resolveParameter(ParameterContext parameterContext,
                                             ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), List.class);
    }
}