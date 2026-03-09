package com.fintrix.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JwtTokenProvider
 *
 * Responsibility:
 *  1. Generate a JWT token after successful Google login
 *  2. Validate incoming JWT tokens on every API request
 *  3. Extract userId from a valid token
 *
 * What is a JWT?
 *  JWT = JSON Web Token
 *  It has 3 parts separated by dots:
 *
 *  eyJhbGciOiJIUzI1NiJ9        → Header  (algorithm used)
 *  .eyJ1c2VySWQiOiIxMjMifQ     → Payload (your data: userId, expiry)
 *  .SflKxwRJSMeKKF2QT4fwpMeJf  → Signature (proves token was not tampered)
 *
 * Why JWT?
 *  - Stateless: server does not store sessions in DB
 *  - Self-contained: userId is inside the token itself
 *  - Secure: signature ensures nobody can fake a token
 */
@Slf4j
@Component
public class JwtTokenProvider {

    // ── Loaded from application.yml → fintrix.jwt.secret ────
    @Value("${fintrix.jwt.secret}")
    private String jwtSecret;

    // ── Loaded from application.yml → fintrix.jwt.expiration-ms
    @Value("${fintrix.jwt.expiration-ms}")
    private long jwtExpirationMs;

    // ────────────────────────────────────────────────────────
    // BUILD the signing key from the secret string
    // HMAC-SHA256 requires minimum 256-bit (32 char) key
    // ────────────────────────────────────────────────────────
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // ────────────────────────────────────────────────────────
    // GENERATE token — called once after Google login succeeds
    // ────────────────────────────────────────────────────────
    public String generateToken(String userId) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(userId)                  // userId stored inside token
                .setIssuedAt(now)                    // when token was created
                .setExpiration(expiry)               // when token expires (24h)
                .signWith(getSigningKey(),
                        SignatureAlgorithm.HS256)     // sign with our secret
                .compact();

        /*
         * Real-world learning:
         * .setSubject(userId) → this is the "sub" claim in JWT payload
         * The payload looks like:
         * {
         *   "sub": "550e8400-e29b-41d4-a716-446655440000",  ← userId
         *   "iat": 1711084800,   ← issued at (unix timestamp)
         *   "exp": 1711171200    ← expires at (unix timestamp)
         * }
         */
    }

    // ────────────────────────────────────────────────────────
    // VALIDATE token — called on every incoming API request
    // Returns true if token is valid and not expired
    // ────────────────────────────────────────────────────────
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);           // throws if invalid
            return true;

        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("JWT token malformed: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT token empty: {}", e.getMessage());
        }
        return false;
    }

    // ────────────────────────────────────────────────────────
    // EXTRACT userId from token
    // Called after validateToken() returns true
    // ────────────────────────────────────────────────────────
    public String getUserIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();                       // returns the userId we stored
    }
}