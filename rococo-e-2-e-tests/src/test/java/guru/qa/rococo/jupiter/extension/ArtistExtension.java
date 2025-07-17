package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.TestArtist;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.service.ArtistGrpcClient;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static guru.qa.rococo.utils.ResourceUtils.getDataImageBase64FromResource;

@ParametersAreNonnullByDefault
public class ArtistExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistExtension.class);

    private final ArtistGrpcClient artistClient = new ArtistGrpcClient();

    @Nullable
    public static ArtistJson createdArtist() {
        final ExtensionContext context = TestMethodContextExtension.context();
        return context.getStore(NAMESPACE).get(
                context.getUniqueId(),
                ArtistJson.class
        );
    }

    public static void setArtist(ArtistJson artist) {
        final ExtensionContext context = TestMethodContextExtension.context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                artist
        );
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), TestArtist.class)
                .ifPresent(artistAnno -> {
                    final ArtistJson artist;
                    artist = new ArtistJson(
                            null,
                            "".equals(artistAnno.name()) ? RandomDataUtils.randomArtistName() : artistAnno.name(),
                            artistAnno.biography(),
                            getDataImageBase64FromResource(artistAnno.photo())
                    );
                    ArtistJson createdArtist = artistClient.createArtist(artist);
                    setArtist(createdArtist);
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(ArtistJson.class);
    }

    @Override
    public ArtistJson resolveParameter(ParameterContext parameterContext,
                                       ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdArtist();
    }
}