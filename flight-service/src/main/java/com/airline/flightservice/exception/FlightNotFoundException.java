package com.airline.flightservice.exception;

import java.util.UUID;

public class FlightNotFoundException extends RuntimeException {
    public FlightNotFoundException(UUID id) {
        super("Flight not found with id: " + id);
    }

    public FlightNotFoundException(String message) {
        super(message);
    }
}
