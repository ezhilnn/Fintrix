// ================================================================
// FILE 1: UserProfileRequest.java
// What the frontend SENDS to update user profile
// ================================================================
package com.fintrix.modules.user.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * UserProfileRequest — DTO (Data Transfer Object)
 *
 * Why DTO and not directly use User entity?
 *
 *  Problem with using entity directly:
 *   - Frontend could send any field including id, role, isActive
 *   - That is a MASS ASSIGNMENT vulnerability
 *   - User could send role=ADMIN and become admin
 *
 *  DTO solves this:
 *   - Only fields in this class can be received from frontend
 *   - id, role, googleId, isActive are NOT here → cannot be set
 *   - Clean separation: API contract vs DB structure
 *
 * @NotBlank  → field must not be null or empty string
 * @Size      → length constraints
 * @Min @Max  → number range constraints
 * @Pattern   → regex validation
 */
@Getter
@Setter
public class UserProfileRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
    private String fullName;

    @Pattern(
        regexp = "^[6-9]\\d{9}$",
        message = "Enter a valid 10-digit Indian mobile number"
    )
    private String phoneNumber;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City name too long")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State name too long")
    private String state;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "You must be at least 18 years old")
    @Max(value = 100, message = "Please enter a valid age")
    private Integer age;
}


