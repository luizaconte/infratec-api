package br.com.infratec.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private static final String[] allowedOrigins = {
            "https://localhost:4200",
            "http://186.225.139.194:4200"
    };

    private static final String[] allowedOriginPatterns = {
            "http://localhost:4200*",
    };


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .allowedOriginPatterns(allowedOriginPatterns)
                .allowedOrigins(allowedOrigins);
    }
}
