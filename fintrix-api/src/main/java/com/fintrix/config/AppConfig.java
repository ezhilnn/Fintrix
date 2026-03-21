// ================================================================
// FILE 3: AppConfig.java — beans needed by new modules
// ================================================================
package com.fintrix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * AppConfig
 * Defines beans used across multiple modules.
 * RestTemplate is used by NotificationServiceImpl for FCM HTTP calls.
 */
@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}