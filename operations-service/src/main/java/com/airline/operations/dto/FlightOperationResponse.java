package com.airline.operations.dto;

import com.airline.operations.model.OperationStatus;

import java.time.Instant;
import java.util.UUID;

public record FlightOperationResponse(
        UUID id,
        UUID flightId,
        String operationReference,
        OperationStatus status,
        Instant scheduledAt,
        Instant actualAt,
        String airportCode,
        String remarks,
        Instant createdAt,
        Instant updatedAt
) {
}
