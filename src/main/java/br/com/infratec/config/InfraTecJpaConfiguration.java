package br.com.infratec.config;

import br.com.infratec.repository.support.ZCSimpleJpaRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        entityManagerFactoryRef = "infratecEntityManagerFactory",
        transactionManagerRef = "infratecTransactionManager",
        basePackages = {"br.com.infratec.repository"})
public class InfraTecJpaConfiguration {


    @Bean(name = "infratecEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean infratecEntityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("infratecDatasource") DataSource dataSource) {
        return builder.dataSource(dataSource)
                .packages("br.com.infratec.model")
                .build();
    }

    @Bean
    public PlatformTransactionManager infratecTransactionManager(
            @Qualifier("infratecEntityManagerFactory") LocalContainerEntityManagerFactoryBean infratecEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(infratecEntityManagerFactory.getObject()));
    }
}
