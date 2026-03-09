// ================================================================
// FILE 2: OAuth2UserService.java
// Handles Google login — finds or creates user in our DB
// ================================================================
package com.fintrix.security;

import com.fintrix.modules.user.domain.User;
import com.fintrix.modules.user.domain.UserRole;
import com.fintrix.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * OAuth2UserService
 *
 * Flow:
 *  1. User clicks "Login with Google" on React frontend
 *  2. Google redirects to our callback URL with auth code
 *  3. Spring Boot exchanges code → gets user info from Google
 *  4. THIS class is called with that Google user info
 *  5. We find or create user in our PostgreSQL DB
 *  6. Return UserPrincipal → OAuth2SuccessHandler issues JWT
 *
 * Google provides these attributes:
 *  sub   → unique Google user ID (never changes)
 *  email → user's email
 *  name  → full name
 *  picture → profile photo URL
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        // ── STEP 1: Call Google to get user info ──────────────
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // ── STEP 2: Extract Google attributes ─────────────────
        String googleId  = (String) attributes.get("sub");
        String email     = (String) attributes.get("email");
        String fullName  = (String) attributes.get("name");
        String pictureUrl = (String) attributes.get("picture");

        log.info("Google OAuth login attempt for email: {}", email);

        // ── STEP 3: Find or create user in our DB ─────────────
        User user = userRepository
                .findByGoogleId(googleId)
                .map(existingUser -> updateExistingUser(
                        existingUser, fullName, pictureUrl))
                .orElseGet(() -> createNewUser(
                        googleId, email, fullName, pictureUrl));

        // ── STEP 4: Return UserPrincipal with Google attributes─
        return UserPrincipal.create(user, attributes);
    }

    // ── First time login → create new user ───────────────────
    private User createNewUser(String googleId, String email,
                               String fullName, String pictureUrl) {
        log.info("Creating new user for email: {}", email);

        User newUser = User.builder()
                .googleId(googleId)
                .email(email)
                .fullName(fullName)
                .profilePictureUrl(pictureUrl)
                .role(UserRole.USER)
                .isActive(true)
                .isProfileComplete(false)   // will complete profile next screen
                .build();

        return userRepository.save(newUser);
    }

    // ── Subsequent logins → update name/picture if changed ───
    private User updateExistingUser(User user, String fullName,
                                    String pictureUrl) {
        user.setFullName(fullName);
        user.setProfilePictureUrl(pictureUrl);
        return userRepository.save(user);
    }
}

