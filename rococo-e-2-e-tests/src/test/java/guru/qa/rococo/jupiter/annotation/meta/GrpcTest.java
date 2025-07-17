package guru.qa.rococo.jupiter.annotation.meta;

import guru.qa.rococo.jupiter.extension.ArtistExtension;
import guru.qa.rococo.jupiter.extension.ArtistsExtension;
import guru.qa.rococo.jupiter.extension.MuseumExtension;
import guru.qa.rococo.jupiter.extension.MuseumsExtension;
import guru.qa.rococo.jupiter.extension.UserExtension;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith({
        UserExtension.class,
        MuseumExtension.class,
        MuseumsExtension.class,
        AllureJunit5.class,
        ArtistsExtension.class,
        ArtistExtension.class
})
public @interface GrpcTest {
}
