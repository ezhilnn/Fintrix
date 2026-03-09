// ================================================================
// FILE 2: UserProfileResponse.java
// What we SEND BACK to the frontend after fetch/update
// ================================================================
package com.fintrix.modules.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * UserProfileResponse — DTO
 *
 * Why not send the full User entity back?
 *
 *  - Entity has internal fields like googleId — no need to expose
 *  - Entity has JPA annotations — not clean for API response
 *  - We control exactly what the frontend receives
 *  - Easier to version the API later without changing entity
 *
 * Builder pattern (@Builder):
 *  Allows: UserProfileResponse.builder()
 *              .id("abc")
 *              .email("user@gmail.com")
 *              .build();
 *  Clean, readable, no 10-argument constructor needed.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private String  id;
    private String  email;
    private String  fullName;
    private String  profilePictureUrl;
    private String  phoneNumber;
    private String  city;
    private String  state;
    private Integer age;
    private String  role;
    private Boolean isProfileComplete;

    /*
     * Notice: googleId is NOT here.
     * We never send internal OAuth identifiers to frontend.
     */
}