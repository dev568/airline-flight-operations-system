package com.airline.flightservice.dto;

import com.airline.flightservice.model.FlightStatus;
import com.airline.flightservice.validation.ValidFlightSchedule;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@ValidFlightSchedule
public record UpdateFlightRequest(
        @Pattern(regexp = "^[A-Z]{2}[0-9]{3,4}$", message = "Flight number must be in format XX123 or XX1234")
        String flightNumber,

        @Pattern(regexp = "^[A-Z]{3}$", message = "Departure airport must be a 3-letter IATA code")
        String departureAirport,

        @Pattern(regexp = "^[A-Z]{3}$", message = "Arrival airport must be a 3-letter IATA code")
        String arrivalAirport,

        @Future(message = "Scheduled departure must be in the future")
        LocalDateTime scheduledDeparture,

        @Future(message = "Scheduled arrival must be in the future")
        LocalDateTime scheduledArrival,

        FlightStatus status,

        @Size(max = 50, message = "Aircraft type must not exceed 50 characters")
        String aircraftType
) {
}
