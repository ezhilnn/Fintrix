package com.fintrix.modules.user.repository;

import com.fintrix.modules.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository
 *
 * Extends JpaRepository → gives us these for FREE:
 *  save(), findById(), findAll(), deleteById(),
 *  count(), existsById() — no SQL needed.
 *
 * We add custom queries only when JpaRepository
 * does not cover our need.
 *
 * Spring Data JPA naming convention:
 *  findBy + FieldName → generates SELECT WHERE field = ?
 *
 *  findByEmail(String email)
 *    → SELECT * FROM users WHERE email = ?
 *
 *  findByGoogleId(String googleId)
 *    → SELECT * FROM users WHERE google_id = ?
 *
 * No SQL written by us — Spring generates it at startup.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // Used by OAuth2UserService to find returning users
    Optional<User> findByGoogleId(String googleId);

    // Used by CustomUserDetailsService to load by email
    Optional<User> findByEmail(String email);

    // Used by admin checks
    boolean existsByEmail(String email);

    // Used by dashboard — check if profile setup is complete
    @Query("SELECT u.isProfileComplete FROM User u WHERE u.id = :userId")
    Optional<Boolean> findIsProfileCompleteById(String userId);
    long countByIsActiveTrue();

}