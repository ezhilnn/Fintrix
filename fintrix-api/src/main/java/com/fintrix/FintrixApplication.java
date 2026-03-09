package com.fintrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Fintrix Application Entry Point
 *
 * @SpringBootApplication       = @Configuration + @EnableAutoConfiguration + @ComponentScan
 * @EnableJpaAuditing           = activates createdAt / updatedAt auto-fill in AuditableEntity
 * @EnableCaching               = activates Redis cache via @Cacheable annotations
 * @EnableScheduling            = activates background jobs (EMI reminders, score refresh)
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableScheduling
public class FintrixApplication {

    public static void main(String[] args) {
        SpringApplication.run(FintrixApplication.class, args);
    }
}