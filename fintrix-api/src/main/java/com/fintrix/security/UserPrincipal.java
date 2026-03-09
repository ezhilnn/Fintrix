package com.fintrix.security;

import com.fintrix.modules.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * UserPrincipal
 *
 * This is the object Spring Security holds in memory
 * for the currently logged-in user.
 *
 * It implements TWO interfaces:
 *
 * 1. UserDetails  → used by JWT filter (token-based requests)
 * 2. OAuth2User   → used by Google OAuth flow (initial login)
 *
 * Why do we need this?
 *  Spring Security does not know about your User entity.
 *  It works with UserDetails interface.
 *  UserPrincipal bridges your User entity → Spring Security.
 *
 * Where it lives:
 *  SecurityContextHolder.getContext().getAuthentication().getPrincipal()
 *  → returns this UserPrincipal object
 *  → available anywhere in your app during a request
 */
@Getter
@AllArgsConstructor
public class UserPrincipal implements UserDetails, OAuth2User {

    private final String id;
    private final String email;
    private final String fullName;
    private final String role;
    private Map<String, Object> attributes;   // Google OAuth attributes

    // ── Factory method — build from your User entity ─────────
    public static UserPrincipal create(User user) {
        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                null
        );
    }

    // ── Factory method — build from User + Google attributes ─
    public static UserPrincipal create(User user, Map<String, Object> attributes) {
        UserPrincipal principal = create(user);
        principal.attributes = attributes;
        return principal;
    }

    // ── UserDetails interface ─────────────────────────────────

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Converts "USER" string → ROLE_USER authority
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return null;   // No password — we use Google OAuth only
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired()    { return true; }

    @Override
    public boolean isAccountNonLocked()     { return true; }

    @Override
    public boolean isCredentialsNonExpired(){ return true; }

    @Override
    public boolean isEnabled()              { return true; }

    // ── OAuth2User interface ──────────────────────────────────

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return id;   // Spring OAuth2 needs a unique name — we use userId
    }
}