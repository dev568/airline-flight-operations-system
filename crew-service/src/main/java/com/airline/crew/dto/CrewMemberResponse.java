package com.airline.crew.dto;

import com.airline.crew.model.CrewRole;
import com.airline.crew.model.CrewStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CrewMemberResponse(
        UUID id,
        String employeeId,
        String firstName,
        String lastName,
        String email,
        CrewRole role,
        CrewStatus status,
        String baseAirport,
        LocalDate hireDate,
        Instant createdAt,
        Instant updatedAt
) {
}
