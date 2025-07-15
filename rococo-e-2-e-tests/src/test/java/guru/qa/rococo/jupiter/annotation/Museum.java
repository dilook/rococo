package guru.qa.rococo.jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Museum {
    String value();
    String country() default "Россия";
    String city() default "Москва";
    String description() default "Один из крупнейших и самый популярный художественный музей мира.";
    String imagePath() default "img/lyvr.png";
}
