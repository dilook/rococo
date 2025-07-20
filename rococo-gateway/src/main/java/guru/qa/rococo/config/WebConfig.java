package guru.qa.rococo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Nonnull;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final String rococoFrontUri;

    public WebConfig(@Value("${rococo-front.base-uri}") String rococoFrontUri) {
        this.rococoFrontUri = rococoFrontUri;
    }

    @Override
    public void addCorsMappings(@Nonnull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(rococoFrontUri)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

}
