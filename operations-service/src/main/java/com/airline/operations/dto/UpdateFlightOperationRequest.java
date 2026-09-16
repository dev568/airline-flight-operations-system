package com.airline.operations.dto;

import com.airline.operations.model.OperationStatus;
import jakarta.validation.constraints.*;
import java.time.Instant;

public record UpdateFlightOperationRequest(
        OperationStatus status,

        @Future(message = "Scheduled time must be in the future")
        Instant scheduledAt,

        Instant actualAt,

        @Pattern(regexp = "^[A-Z]{3}$", message = "Airport code must be a 3-letter uppercase IATA code")
        String airportCode,

        @Size(max = 500, message = "Remarks must not exceed 500 characters")
        String remarks
) {
}
