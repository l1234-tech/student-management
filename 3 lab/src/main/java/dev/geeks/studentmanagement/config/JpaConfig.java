package dev.geeks.studentmanagement.config;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("dev.geeks.studentmanagement")
public class JpaConfig {
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) throws java.sql.SQLException {
        // Existing lab-2/early-lab-3 tables may still limit note to 255 characters.
        try (var connection = dataSource.getConnection()) {
            if ("PostgreSQL".equals(connection.getMetaData().getDatabaseProductName())) {
                try (var statement = connection.createStatement()) {
                    statement.execute("ALTER TABLE IF EXISTS students ALTER COLUMN note TYPE VARCHAR(2000)");
                }
            }
        }
        var factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan("dev.geeks.studentmanagement");
        factory.setJpaVendorAdapter(new EclipseLinkJpaVendorAdapter());
        factory.setJpaPropertyMap(Map.of(
            "eclipselink.weaving", "false",
            "eclipselink.ddl-generation", "create-or-extend-tables",
            "eclipselink.ddl-generation.output-mode", "database"
        ));
        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }
}
