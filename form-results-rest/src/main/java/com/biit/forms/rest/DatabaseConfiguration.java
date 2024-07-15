package com.biit.forms.rest;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.core.env.Environment;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "formResultsFactory", transactionManagerRef = "formResultsTransactionManager",
        basePackages = {DatabaseConfiguration.PACKAGE})
public class DatabaseConfiguration {
    public static final String PACKAGE = "com.biit.forms.persistence";

    @Autowired
    private Environment environment;

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.form-results.datasource")
    public DataSource formResultsDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean formResultsFactory(EntityManagerFactoryBuilder builder,
                                                                          @Qualifier("formResultsDataSource") DataSource dataSource) {
        final HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", environment.getProperty("spring.form-results.datasource.hibernate.ddl-auto"));
        return builder.dataSource(dataSource).properties(properties).packages(PACKAGE).build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager formResultsTransactionManager(
            @Qualifier ("formResultsFactory") EntityManagerFactory formResultsFactory) {
        return new JpaTransactionManager(formResultsFactory);
    }
}
