package guru.qa.rococo.config;


import guru.qa.rococo.service.cors.CorsCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@Profile({"local"})
public class SecurityConfigLocal {

    private final CorsCustomizer corsCustomizer;

    @Autowired
    public SecurityConfigLocal(CorsCustomizer corsCustomizer) {
        this.corsCustomizer = corsCustomizer;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        corsCustomizer.corsCustomizer(http);

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(customizer ->
                        customizer.requestMatchers(
                                        PathPatternRequestMatcher.withDefaults().matcher("/api/session"),
                                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/api/artist/**"),
                                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/api/museum/**"),
                                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/api/painting/**"))
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                ).oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
