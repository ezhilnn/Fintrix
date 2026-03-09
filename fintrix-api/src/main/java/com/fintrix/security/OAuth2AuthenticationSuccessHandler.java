
// ================================================================
// FILE 3: OAuth2AuthenticationSuccessHandler.java
// Called after Google login succeeds — issues our JWT to frontend
// ================================================================
package com.fintrix.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import com.fintrix.config.FintrixOAuth2Properties;

/**
 * OAuth2AuthenticationSuccessHandler
 *
 * Called by Spring Security after Google login succeeds.
 *
 * What it does:
 *  1. Gets UserPrincipal from Authentication object
 *  2. Generates a JWT token for this user
 *  3. Redirects user to React frontend with token in URL
 *
 * Redirect URL format:
 *  http://localhost:5173/oauth2/callback?token=eyJhbGci...
 *
 * React frontend reads the token from URL,
 * stores it in memory/state,
 * and uses it for all future API calls.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final FintrixOAuth2Properties oauth2Properties;

    // @Value("${fintrix.oauth2.authorized-redirect-uris}")
    // private List<String> authorizedRedirectUris;
//     private final List<String> authorizedRedirectUris = List.of(
//         "http://localhost:5173/oauth2/callback"
// );

    // @Override
    // public void onAuthenticationSuccess(
    //         HttpServletRequest request,
    //         HttpServletResponse response,
    //         Authentication authentication
    // ) throws IOException {

    //     UserPrincipal userPrincipal =
    //             (UserPrincipal) authentication.getPrincipal();

    //     String token = jwtTokenProvider.generateToken(userPrincipal.getId());

    //     log.info("JWT issued for user: {}", userPrincipal.getEmail());

    //     String redirectUri = authorizedRedirectUris.get(0);

    //     String targetUrl = UriComponentsBuilder
    //             .fromUriString(redirectUri)
    //             .queryParam("token", token)
    //             .build()
    //             .toUriString();

    //     getRedirectStrategy().sendRedirect(request, response, targetUrl);
    // }
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        UserPrincipal userPrincipal =
                (UserPrincipal) authentication.getPrincipal();

        String token = jwtTokenProvider.generateToken(userPrincipal.getId());

        log.info("JWT issued for user: {}", userPrincipal.getEmail());

        String redirectUri =
                oauth2Properties.getAuthorizedRedirectUris().get(0);

        String targetUrl = UriComponentsBuilder
                .fromUriString(redirectUri)
                .queryParam("token", token)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}