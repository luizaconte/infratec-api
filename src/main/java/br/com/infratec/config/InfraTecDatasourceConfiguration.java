package br.com.infratec.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class InfraTecDatasourceConfiguration {

    @Bean(name = "infratecProperties")
    @ConfigurationProperties("spring.datasource.infratec")
    public DataSourceProperties infratecDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "infratecDatasource")
    @ConfigurationProperties(prefix = "spring.datasource.infratec.hikari")
    public DataSource infratecDatasource() {
        return infratecDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

}
