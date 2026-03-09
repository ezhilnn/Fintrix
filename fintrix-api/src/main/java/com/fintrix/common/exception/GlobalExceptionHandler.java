// ================================================================
// FILE 3: GlobalExceptionHandler.java
// com/fintrix/common/exception/GlobalExceptionHandler.java
// ================================================================
package com.fintrix.common.exception;

import com.fintrix.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler
 *
 * @RestControllerAdvice → catches exceptions from ALL controllers
 * No try-catch needed in controllers or services.
 *
 * Flow:
 *  Service throws ResourceNotFoundException
 *      ↓
 *  GlobalExceptionHandler.handleResourceNotFound() catches it
 *      ↓
 *  Returns standardised ApiResponse with HTTP 404
 *      ↓
 *  Frontend receives: { success: false, message: "User not found..." }
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 404 Not Found ─────────────────────────────────────────
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    // ── 400 Validation Errors (@Valid failed) ─────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field   = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });

        log.warn("Validation failed: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("Validation failed")
                        .data(errors)
                        .build());
    }

    // ── 500 Catch-all ─────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        "Something went wrong. Please try again."));
    }
}

