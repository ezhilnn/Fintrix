
// ================================================================
// FILE 5: CorsConfig.java
// ================================================================
package com.fintrix.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CorsConfig
 *
 * CORS is fully handled by SecurityConfig.corsConfigurationSource().
 * This class exists as a placeholder for any future WebMVC customizations
 * such as message converters, interceptors, or path matchers.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    // CORS handled in SecurityConfig
}