
// ================================================================
// FILE 3: DatabaseConfig.java
// ================================================================
package com.fintrix.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * DatabaseConfig
 *
 * @EnableJpaRepositories  → Spring Data JPA scans for @Repository
 * @EnableJpaAuditing      → activates createdAt/updatedAt in AuditableEntity
 *                           (also annotated on FintrixApplication — both work)
 * @EnableTransactionManagement → activates @Transactional processing
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.fintrix")
@EnableTransactionManagement
public class DatabaseConfig {
    /*
     * DataSource bean is auto-created by Spring Boot
     * from application.yml spring.datasource.* properties.
     * No manual DataSource bean needed.
     */
}

