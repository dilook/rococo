package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.TestMuseum;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.service.MuseumGrpcClient;
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
public class MuseumExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MuseumExtension.class);

    private final MuseumGrpcClient museumClient = new MuseumGrpcClient();

    @Nullable
    public static MuseumJson createdMuseum() {
        final ExtensionContext context = TestMethodContextExtension.context();
        return context.getStore(NAMESPACE).get(
                context.getUniqueId(),
                MuseumJson.class
        );
    }

    public static void setMuseum(MuseumJson museum) {
        final ExtensionContext context = TestMethodContextExtension.context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                museum
        );
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), TestMuseum.class)
                .ifPresent(museumAnno -> {
                    final MuseumJson museum = new MuseumJson(
                            null,
                            "".equals(museumAnno.title()) ? RandomDataUtils.randomMuseumName() : museumAnno.title(),
                            museumAnno.description(),
                            getDataImageBase64FromResource(museumAnno.imagePath()),
                            new MuseumJson.Geo(
                                    museumAnno.city(),
                                    museumClient.getCountryByName(museumAnno.country())
                            )
                    );
                    MuseumJson createdMuseum = museumClient.createMuseum(museum);
                    setMuseum(createdMuseum);
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(MuseumJson.class);
    }

    @Override
    public MuseumJson resolveParameter(ParameterContext parameterContext,
                                       ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdMuseum();
    }
}
