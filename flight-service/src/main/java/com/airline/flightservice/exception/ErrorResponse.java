package com.airline.flightservice.exception;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        UUID correlationId,
        Map<String, String> validationErrors
) {
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
        this(timestamp, status, error, message, path, null, null);
    }

    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path, UUID correlationId) {
        this(timestamp, status, error, message, path, correlationId, null);
    }

    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path, Map<String, String> validationErrors) {
        this(timestamp, status, error, message, path, null, validationErrors);
    }
}
