package br.com.infratec.config;

import br.com.infratec.repository.support.ZCSimpleJpaRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableJpaRepositories(
        repositoryBaseClass = ZCSimpleJpaRepository.class,
        entityManagerFactoryRef = "salesEntityManagerFactory",
        transactionManagerRef = "salesTransactionManager",
        basePackages = {"br.com.infratec.repository.sales"})
public class SalesJpaConfiguration {


    @Bean(name = "salesEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean salesEntityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("salesDatasource") DataSource dataSource) {
        return builder.dataSource(dataSource)
                .packages("br.com.infratec.model.sales")
                .build();
    }

    @Bean
    public PlatformTransactionManager salesTransactionManager(
            @Qualifier("salesEntityManagerFactory") LocalContainerEntityManagerFactoryBean salesEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(salesEntityManagerFactory.getObject()));
    }
}
