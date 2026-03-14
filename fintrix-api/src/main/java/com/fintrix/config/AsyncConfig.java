package com.fintrix.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * AsyncConfig
 *
 * Enables @Async processing used by AuditService.
 * Without this, @Async methods execute synchronously.
 *
 * Thread pool configured in application.yml:
 *   spring.task.execution.pool.*
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    // Thread pool configured via spring.task.execution in application.yml
}