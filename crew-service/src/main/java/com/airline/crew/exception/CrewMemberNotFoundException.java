package com.airline.crew.exception;

import java.util.UUID;

public class CrewMemberNotFoundException extends RuntimeException {

    public CrewMemberNotFoundException(UUID id) {
        super("Crew member not found with id: " + id);
    }

    public CrewMemberNotFoundException(String message) {
        super(message);
    }
}
