
// ================================================================
// FILE 2: UserServiceImpl.java — Implementation (business logic)
// ================================================================
package com.fintrix.modules.user.service;

import com.fintrix.common.exception.ResourceNotFoundException;
import com.fintrix.modules.user.domain.User;
import com.fintrix.modules.user.dto.UserProfileRequest;
import com.fintrix.modules.user.dto.UserProfileResponse;
import com.fintrix.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserServiceImpl — Business Logic
 *
 * Real-world patterns used here:
 *
 * @Transactional(readOnly = true)
 *   → tells DB this query will NOT modify data
 *   → DB can optimise: skip write locks, use read replicas
 *   → Always use for GET operations
 *
 * @Transactional (default: readOnly = false)
 *   → wraps the method in a DB transaction
 *   → if any exception → entire operation rolls back
 *   → Always use for POST/PUT/DELETE operations
 *
 * @Cacheable("user-profile")
 *   → first call: runs method, stores result in Redis
 *   → subsequent calls: returns from Redis, skips DB
 *   → key = userId → each user has their own cache entry
 *
 * @CacheEvict("user-profile")
 *   → when profile is updated → delete old cache
 *   → next GET will fetch fresh data from DB and re-cache
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // ── GET profile ───────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "user-profile", key = "#userId")
    public UserProfileResponse getMyProfile(String userId) {
        log.debug("Fetching profile for userId: {}", userId);

        User user = findUserById(userId);
        return mapToResponse(user);

        /*
         * First call  → hits DB → stores in Redis → returns
         * Second call → reads from Redis → DB never called
         * Cache key   → "user-profile::550e8400-e29b..."
         */
    }

    // ── UPDATE profile ────────────────────────────────────────
    @Override
    @Transactional
    @CacheEvict(value = "user-profile", key = "#userId")
    public UserProfileResponse updateMyProfile(String userId,
                                               UserProfileRequest request) {
        log.info("Updating profile for userId: {}", userId);

        User user = findUserById(userId);

        // Update only the fields the user is allowed to change
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setCity(request.getCity());
        user.setState(request.getState());
        user.setAge(request.getAge());

        // Mark profile as complete once basic info is filled
        user.setIsProfileComplete(true);

        User savedUser = userRepository.save(user);

        log.info("Profile updated successfully for userId: {}", userId);

        /*
         * @CacheEvict runs AFTER this method completes.
         * Old cached profile is deleted from Redis.
         * Next GET call will fetch fresh data from DB.
         */
        return mapToResponse(savedUser);
    }

    // ── DEACTIVATE account ────────────────────────────────────
    @Override
    @Transactional
    @CacheEvict(value = "user-profile", key = "#userId")
    public void deactivateAccount(String userId) {
        log.warn("Deactivating account for userId: {}", userId);

        User user = findUserById(userId);
        user.setIsActive(false);
        userRepository.save(user);

        /*
         * We never hard-delete users in a fintech app.
         * Regulatory requirement: keep records for audit trail.
         * Soft delete: isActive = false
         */
    }

    // ── Private helpers ───────────────────────────────────────

    private User findUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "id", userId));
        /*
         * ResourceNotFoundException → GlobalExceptionHandler
         * catches this and returns HTTP 404 automatically.
         * No try-catch needed in controllers.
         */
    }

    private UserProfileResponse mapToResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .phoneNumber(user.getPhoneNumber())
                .city(user.getCity())
                .state(user.getState())
                .age(user.getAge())
                .role(user.getRole().name())
                .isProfileComplete(user.getIsProfileComplete())
                .build();
    }
}