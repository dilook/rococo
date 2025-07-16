package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.TestMuseum;
import guru.qa.rococo.jupiter.annotation.TestMuseums;
import guru.qa.rococo.model.rest.CountryJson;
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
import java.util.ArrayList;
import java.util.List;

import static guru.qa.rococo.utils.ResourceUtils.getDataImageBase64FromResource;

@ParametersAreNonnullByDefault
public class MuseumsExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MuseumsExtension.class);

    private final MuseumGrpcClient museumClient = new MuseumGrpcClient();

    @Nullable
    @SuppressWarnings("unchecked")
    public static List<MuseumJson> createdMuseums() {
        final ExtensionContext context = TestMethodContextExtension.context();
        return context.getStore(NAMESPACE).get(context.getUniqueId(), List.class);
    }

    public static void setMuseums(List<MuseumJson> museum) {
        final ExtensionContext context = TestMethodContextExtension.context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                museum
        );
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), TestMuseums.class)
                .ifPresent(museumAnno -> {
                    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), TestMuseum.class).ifPresent(museum -> {
                        throw new IllegalStateException("Only @Museum or @Museums annotation is allowed per test method!");
                    });
                    
                    if (museumAnno.count() <= 0) {
                        return;
                    }
                    final List<MuseumJson> listMuseums = new ArrayList<>();
                    for (int i = 0; i < museumAnno.count(); i++) {
                        MuseumJson museumJson = createMuseumJson();
                        listMuseums.add(museumClient.createMuseum(museumJson));
                    }
                    setMuseums(listMuseums);
                });
    }
    
    private MuseumJson createMuseumJson() {
        final MuseumJson museum;
        CountryJson country = museumClient.getRandomCountry();
        museum = new MuseumJson(
                null,
                RandomDataUtils.randomMuseumName(),
                RandomDataUtils.randomSentence(15),
                getDataImageBase64FromResource("img/lyvr.jpg"),
                new MuseumJson.Geo(
                        RandomDataUtils.randomCityName(),
                        country
                )
        );
        return museum;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(List.class) &&
                parameterContext.getParameter().getParameterizedType().toString().contains("MuseumJson");
    }

    @Override
    public List<MuseumJson> resolveParameter(ParameterContext parameterContext,
                                             ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdMuseums();
    }
}