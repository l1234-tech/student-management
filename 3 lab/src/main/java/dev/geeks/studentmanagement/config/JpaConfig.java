// настройка взаимодействия с Spring Data JPA с бд через EclipseLink

package dev.geeks.studentmanagement.config;

import jakarta.persistence.EntityManagerFactory;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
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
  private final String weaving;
  private final String ddlGeneration;
  private final String ddlOutputMode;

  public JpaConfig(
      @Value("${spring.jpa.properties.eclipselink.weaving:false}") String weaving,
      @Value("${spring.jpa.properties.eclipselink.ddl-generation:create-or-extend-tables}")
          String ddlGeneration,
      @Value("${spring.jpa.properties.eclipselink.ddl-generation.output-mode:database}")
          String ddlOutputMode) {
    this.weaving = weaving;
    this.ddlGeneration = ddlGeneration;
    this.ddlOutputMode = ddlOutputMode;
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource)
      throws SQLException {
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
    factory.setJpaPropertyMap(
        Map.of(
            "eclipselink.weaving", weaving,
            "eclipselink.ddl-generation", ddlGeneration,
            "eclipselink.ddl-generation.output-mode", ddlOutputMode));
    return factory;
  }

  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory factory) {
    return new JpaTransactionManager(factory);
  }
}
