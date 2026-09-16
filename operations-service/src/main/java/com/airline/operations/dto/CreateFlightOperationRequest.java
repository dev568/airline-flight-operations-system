package com.airline.operations.dto;

import com.airline.operations.model.OperationStatus;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.UUID;

public record CreateFlightOperationRequest(
        @NotNull(message = "Flight ID is required")
        UUID flightId,

        @NotBlank(message = "Operation reference is required")
        @Size(min = 1, max = 50, message = "Operation reference must be between 1 and 50 characters")
        String operationReference,

        @NotNull(message = "Status is required")
        OperationStatus status,

        @NotNull(message = "Scheduled time is required")
        @Future(message = "Scheduled time must be in the future")
        Instant scheduledAt,

        Instant actualAt,

        @NotBlank(message = "Airport code is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Airport code must be a 3-letter uppercase IATA code")
        String airportCode,

        @Size(max = 500, message = "Remarks must not exceed 500 characters")
        String remarks
) {
}
