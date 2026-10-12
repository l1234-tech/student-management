package dev.geeks.studentmanagement.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

// =============================================================================
// Компонент:   JpaConfig
// Модуль:      dev.geeks.studentmanagement.config
// Назначение:  настройка JPA на EclipseLink вместо Hibernate по умолчанию.
// Функции:     создание EntityManagerFactory и TransactionManager;
//              перенос свойств spring.jpa.* в EclipseLink, обработка show-sql.
// Источник свойств: application.yml, application-dev.yml, application-prod.yml.
// Статус: реализовано.
// =============================================================================

// Конфигурация JPA для варианта Spring Boot + EclipseLink.
// Spring Boot по умолчанию поднимает Hibernate; здесь явно создаётся EntityManagerFactory
// на EclipseLink, чтобы variant лабораторной выполнялся: провайдер, DDL и логирование БД
// берутся из application.yml / application-dev.yml / application-prod.yml.
@Configuration
@EnableTransactionManagement
public class JpaConfig {

    // Корень приложения: здесь лежат @Entity студента (student.model) и пользователя (auth.model).
    private static final String BASE_PACKAGE = "dev.geeks.studentmanagement";

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
                                                                       JpaProperties jpaProperties) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPersistenceUnitName("student-management");
        factory.setPackagesToScan(BASE_PACKAGE);
        factory.setJpaVendorAdapter(new EclipseLinkJpaVendorAdapter());

        // Свойства из spring.jpa.* (в том числе eclipselink.* из профилей dev/prod).
        Map<String, Object> properties = new HashMap<>(jpaProperties.getProperties());

        // spring.jpa.show-sql: true -> показ SQL в логах EclipseLink.
        if (jpaProperties.isShowSql()) {
            properties.putIfAbsent("eclipselink.logging.level.sql", "FINE");
        }

        factory.setJpaPropertyMap(properties);
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean(PlatformTransactionManager.class)
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
