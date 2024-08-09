package br.com.infratec.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class LoggingConfig {

    @Bean
    public CommonsRequestLoggingFilter logging() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setMaxPayloadLength(100000);
        filter.setIncludePayload(true);
        filter.setIncludeHeaders(true);
        filter.setIncludeQueryString(true);
        return filter;
    }
}
