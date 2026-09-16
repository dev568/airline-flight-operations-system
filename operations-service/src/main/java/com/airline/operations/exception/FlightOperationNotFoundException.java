package com.airline.operations.exception;

import java.util.UUID;

public class FlightOperationNotFoundException extends RuntimeException {

    public FlightOperationNotFoundException(UUID id) {
        super("Flight operation not found with id: " + id);
    }

    public FlightOperationNotFoundException(String message) {
        super(message);
    }
}
