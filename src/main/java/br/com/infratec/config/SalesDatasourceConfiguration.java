package br.com.infratec.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class SalesDatasourceConfiguration {

    @Bean(name = "salesProperties")
    @ConfigurationProperties("spring.datasource.sales")
    public DataSourceProperties salesDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "salesDatasource")
    @ConfigurationProperties(prefix = "spring.datasource.sales.hikari")
    public DataSource salesDatasource() {
        return salesDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

}
