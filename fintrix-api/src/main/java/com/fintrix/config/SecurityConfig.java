package com.fintrix.config;

import com.fintrix.security.JwtAuthenticationFilter;
import com.fintrix.security.OAuth2AuthenticationSuccessHandler;
import com.fintrix.security.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * SecurityConfig
 *
 * This is the central security configuration for Fintrix.
 * It wires together:
 *  - CORS (allows React frontend to call our API)
 *  - CSRF disabled (we use JWT, not cookies)
 *  - Session stateless (no server-side sessions)
 *  - Public routes (login, OAuth callback)
 *  - Protected routes (everything else needs JWT)
 *  - Google OAuth2 login flow
 *  - JWT filter registration
 *
 * Request lifecycle:
 *
 *  HTTP Request
 *      ↓
 *  CorsFilter          → allow React origin
 *      ↓
 *  JwtAuthenticationFilter → validate token, set SecurityContext
 *      ↓
 *  SecurityFilterChain → is this route public or protected?
 *      ↓ Public              ↓ Protected
 *  Allow through         Check SecurityContext
 *                            ↓ authenticated → allow
 *                            ↓ anonymous     → 401 Unauthorized
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity           // enables @PreAuthorize on controllers
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter        jwtAuthenticationFilter;
    private final OAuth2UserService              oAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler;

    // @Value("${fintrix.cors.allowed-origins}")
    // private List<String> allowedOrigins;

//     private final List<String> allowedOrigins = List.of(
//         "http://localhost:5173",
//         "https://fintrix.vercel.app"
// );
private final FintrixCorsProperties corsProperties;
    // ── PUBLIC routes — no JWT needed ─────────────────────────
    private static final String[] PUBLIC_URLS = {
            "/api/v1/auth/**",             // OAuth2 login + callback
            "/oauth2/**",                  // Spring OAuth2 internals
            "/actuator/health",            // health check (for Render/Railway)
            "/actuator/info"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            // ── 1. Disable CSRF ──────────────────────────────
            // Why? We use JWT in Authorization header, not cookies.
            // CSRF attacks only work against cookie-based auth.
            .csrf(AbstractHttpConfigurer::disable)

            // ── 2. Configure CORS ────────────────────────────
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // ── 3. Stateless sessions ────────────────────────
            // Why? JWT carries all auth info. No need to store
            // sessions in memory or DB. Scales horizontally.
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // ── 4. Route authorization rules ─────────────────
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(PUBLIC_URLS).permitAll()
                    .anyRequest().authenticated()   // all other routes need JWT
            )

            // ── 5. Google OAuth2 login ────────────────────────
            .oauth2Login(oauth2 -> oauth2
                    // Spring handles /oauth2/authorization/google
                    // and /login/oauth2/code/google automatically
                    .userInfoEndpoint(userInfo -> userInfo
                            .userService(oAuth2UserService))   // our custom service
                    .successHandler(oAuth2SuccessHandler)      // issues JWT on success
            )

            // ── 6. Register JWT filter ────────────────────────
            // Runs BEFORE UsernamePasswordAuthenticationFilter
            // on every request to validate JWT token
            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ── CORS Configuration ────────────────────────────────────
    // Allows React (localhost:5173) to call Spring Boot (localhost:8080)
    // @Bean
    // public CorsConfigurationSource corsConfigurationSource() {
    //     CorsConfiguration config = new CorsConfiguration();

    //     config.setAllowedOrigins(allowedOrigins);          // from application.yml
    //     config.setAllowedMethods(List.of(
    //             "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    //     config.setAllowedHeaders(List.of(
    //             "Authorization",        // JWT token header
    //             "Content-Type",
    //             "Accept",
    //             "Origin",
    //             "X-Requested-With"));
    //     config.setExposedHeaders(List.of("Authorization"));
    //     config.setAllowCredentials(true);
    //     config.setMaxAge(3600L);        // cache preflight response 1 hour

    //     UrlBasedCorsConfigurationSource source =
    //             new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", config);
    //     return source;
    // }
    @Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    config.setAllowedOrigins(corsProperties.getAllowedOrigins());
    config.setAllowedMethods(List.of(
            "GET","POST","PUT","PATCH","DELETE","OPTIONS"));

    config.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With"));

    config.setExposedHeaders(List.of("Authorization"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", config);
    return source;
}

}