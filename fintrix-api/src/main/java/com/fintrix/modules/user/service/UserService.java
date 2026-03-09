// ================================================================
// FILE 1: UserService.java — Interface (contract)
// ================================================================
package com.fintrix.modules.user.service;

import com.fintrix.modules.user.dto.UserProfileRequest;
import com.fintrix.modules.user.dto.UserProfileResponse;

/**
 * UserService — Interface
 *
 * Why define an interface AND an implementation?
 *
 * Real-world reason 1 — Testability:
 *  In tests you can swap UserServiceImpl with a mock:
 *  UserService service = mock(UserService.class);
 *  No DB needed in unit tests.
 *
 * Real-world reason 2 — Clean Architecture:
 *  Controller depends on UserService (abstraction)
 *  NOT on UserServiceImpl (concrete class)
 *  If you change implementation → controller never changes
 *
 * Real-world reason 3 — Spring AOP:
 *  Spring wraps your service in a proxy for @Transactional
 *  Proxies work best with interfaces.
 */
public interface UserService {

    // Get current logged-in user's profile
    UserProfileResponse getMyProfile(String userId);

    // Update profile (name, phone, city, state, age)
    UserProfileResponse updateMyProfile(String userId,
                                        UserProfileRequest request);

    // Deactivate account
    void deactivateAccount(String userId);
}

