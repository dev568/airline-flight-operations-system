package com.airline.flightservice.dto;

import com.airline.flightservice.model.FlightStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record FlightResponse(
        UUID id,
        String flightNumber,
        String departureAirport,
        String arrivalAirport,
        LocalDateTime scheduledDeparture,
        LocalDateTime scheduledArrival,
        FlightStatus status,
        String aircraftType,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
