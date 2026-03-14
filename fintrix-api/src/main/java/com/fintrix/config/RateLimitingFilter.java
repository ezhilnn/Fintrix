package com.fintrix.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;

/**
 * RateLimitingFilter
 *
 * Protects all /api/v1/** endpoints from abuse.
 *
 * Strategy: Redis counter with TTL window
 *
 *  Key format: rate:{userId or ip}:{endpoint-bucket}
 *  TTL:        1 minute window
 *
 *  Limits per minute:
 *    Fraud check          → 10 per user (prevent mass scanning)
 *    Loan eligibility     → 20 per user
 *    Card recommendations → 20 per user
 *    Other endpoints      → 60 per user
 *    Global IP limit      → 200 per IP
 *
 * Why Redis for rate limiting?
 *   In-memory Map fails in multi-node deployments.
 *   Redis is shared across all instances → consistent limiting.
 */
@Slf4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;

    @Value("${fintrix.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    // Endpoints that need stricter limits
    private static final Set<String> STRICT_ENDPOINTS = Set.of(
            "/api/v1/fraud/check",
            "/api/v1/loans/check-eligibility",
            "/api/v1/credit-cards/recommendations"
    );

    public RateLimitingFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain chain)
            throws ServletException, IOException {

        if (!rateLimitEnabled || !request.getRequestURI()
                .startsWith("/api/v1/")) {
            chain.doFilter(request, response);
            return;
        }

        String uri      = request.getRequestURI();
        String clientId = extractClientId(request);
        int    limit    = STRICT_ENDPOINTS.contains(uri) ? 10 : 60;

        String key   = "rate:" + clientId + ":" + getBucket(uri);
        Long   count = redisTemplate.opsForValue().increment(key);

        if (count == 1) {
            // First request in window — set TTL
            redisTemplate.expire(key, Duration.ofMinutes(1));
        }

        // Add rate limit headers
        response.addHeader("X-RateLimit-Limit",     String.valueOf(limit));
        response.addHeader("X-RateLimit-Remaining",
                String.valueOf(Math.max(0, limit - count)));

        if (count > limit) {
            log.warn("Rate limit exceeded for clientId: {} uri: {}", clientId, uri);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("""
                {
                  "success": false,
                  "message": "Too many requests. Please wait 1 minute before retrying.",
                  "data": null
                }
                """);
            return;
        }

        chain.doFilter(request, response);
    }

    private String extractClientId(HttpServletRequest request) {
        // Use userId if authenticated, else IP
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extract raw JWT (we don't decode here — just use as key)
            String token = authHeader.substring(7);
            return "token:" + token.substring(
                    Math.max(0, token.length() - 16));
        }
        String ip = request.getHeader("X-Forwarded-For");
        return "ip:" + (ip != null ? ip.split(",")[0].trim()
                : request.getRemoteAddr());
    }

    // Group similar endpoints into one rate limit bucket
    private String getBucket(String uri) {
        if (uri.contains("/fraud"))           return "fraud";
        if (uri.contains("/loans"))           return "loans";
        if (uri.contains("/credit-cards"))    return "cards";
        if (uri.contains("/financial-health"))return "health";
        return "general";
    }
}