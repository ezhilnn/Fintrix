// ================================================================
// FILE 1: CustomUserDetailsService.java
// Loads user from DB by userId — used by JwtAuthenticationFilter
// ================================================================
package com.fintrix.security;

import com.fintrix.modules.user.domain.User;
import com.fintrix.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CustomUserDetailsService
 *
 * Spring Security calls this to load a user from DB.
 * We override loadUserByUsername to load by email,
 * and add loadUserById for JWT filter usage.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Called by JwtAuthenticationFilter after token validation
    @Transactional(readOnly = true)
    public UserDetails loadUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with id: " + userId));
        return UserPrincipal.create(user);
    }

    // Required by UserDetailsService interface
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with email: " + email));
        return UserPrincipal.create(user);
    }
}
