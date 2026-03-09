
// ================================================================
// FILE 2: ResourceNotFoundException.java
// com/fintrix/common/exception/ResourceNotFoundException.java
// ================================================================
package com.fintrix.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ResourceNotFoundException
 *
 * Thrown when an entity is not found in the DB.
 * @ResponseStatus(NOT_FOUND) → Spring automatically
 * returns HTTP 404 when this exception is thrown.
 *
 * Usage in service:
 *   throw new ResourceNotFoundException("User", "id", userId);
 *
 * Produces message:
 *   "User not found with id: 550e8400-e29b..."
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName,
                                      String fieldName,
                                      Object fieldValue) {
        super(String.format("%s not found with %s: '%s'",
                resourceName, fieldName, fieldValue));
    }
}

