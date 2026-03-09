package com.fintrix.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthenticationFilter
 *
 * This filter runs on EVERY incoming HTTP request — exactly once.
 * Extends OncePerRequestFilter → guaranteed single execution per request.
 *
 * What it does:
 *  1. Reads Authorization header from request
 *  2. Extracts JWT token (removes "Bearer " prefix)
 *  3. Validates the token using JwtTokenProvider
 *  4. Loads user from DB using userId in token
 *  5. Sets authentication in SecurityContext
 *     → from this point, @AuthenticationPrincipal works in controllers
 *
 * Request flow:
 *
 *  HTTP Request
 *      ↓
 *  JwtAuthenticationFilter.doFilterInternal()
 *      ↓
 *  Valid token?
 *      ↓ YES                    ↓ NO
 *  Set Authentication       Skip (request continues
 *  in SecurityContext        as anonymous user)
 *      ↓                        ↓
 *  Controller runs          SecurityConfig blocks
 *  with user identity       protected routes → 401
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider    jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest  request,
            HttpServletResponse response,
            FilterChain         filterChain
    ) throws ServletException, IOException {

        try {
            // ── STEP 1: Extract token from header ─────────────
            String jwt = extractTokenFromRequest(request);

            // ── STEP 2: Validate token ─────────────────────────
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {

                // ── STEP 3: Get userId from token ──────────────
                String userId = jwtTokenProvider.getUserIdFromToken(jwt);

                // ── STEP 4: Load UserPrincipal from DB ─────────
                UserPrincipal userPrincipal =
                        (UserPrincipal) userDetailsService.loadUserById(userId);

                // ── STEP 5: Build authentication object ────────
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userPrincipal,
                                null,                            // no password
                                userPrincipal.getAuthorities()   // ROLE_USER
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)           // stores IP, session
                );

                // ── STEP 6: Store in SecurityContext ───────────
                // From this point, any controller can call:
                // @AuthenticationPrincipal UserPrincipal user
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

                log.debug("Authenticated user: {} for URI: {}",
                        userId, request.getRequestURI());
            }

        } catch (Exception ex) {
            // Do NOT throw — just log and continue as anonymous
            // SecurityConfig will block protected routes anyway
            log.error("Could not set user authentication: {}", ex.getMessage());
        }

        // ── Always continue filter chain ───────────────────────
        filterChain.doFilter(request, response);
    }

    // ── Extract JWT from "Authorization: Bearer <token>" ──────
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // Header format: "Bearer eyJhbGciOiJIUzI1NiJ9..."
        if (StringUtils.hasText(bearerToken)
                && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);   // remove "Bearer " prefix
        }
        return null;
    }
}