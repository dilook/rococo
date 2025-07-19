package guru.qa.rococo.jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestPainting {
    String title() default "";
    String description() default "Прекрасная картина, созданная талантливым художником.";
    String content() default "img/painting.jpg";
    TestArtist artist() default @TestArtist();
    TestMuseum museum() default @TestMuseum();
}