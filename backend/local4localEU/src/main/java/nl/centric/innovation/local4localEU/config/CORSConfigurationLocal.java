package nl.centric.innovation.local4localEU.config;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("local")
public class CORSConfigurationLocal implements WebMvcConfigurer {

    @Value("${local4localEU.server.name}")
    private String local4localEuServerName;
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedOrigins(local4localEuServerName)
                .exposedHeaders("Set-Cookie")
                .allowCredentials(true)
                .allowedHeaders("*");
    }
}
