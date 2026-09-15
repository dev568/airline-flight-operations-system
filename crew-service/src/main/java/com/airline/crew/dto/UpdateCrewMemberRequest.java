package com.airline.crew.dto;

import com.airline.crew.model.CrewRole;
import com.airline.crew.model.CrewStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record UpdateCrewMemberRequest(
        @Size(min = 1, max = 50, message = "Employee ID must be between 1 and 50 characters")
        String employeeId,

        @Size(max = 100, message = "First name must not exceed 100 characters")
        String firstName,

        @Size(max = 100, message = "Last name must not exceed 100 characters")
        String lastName,

        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        CrewRole role,

        CrewStatus status,

        @Pattern(regexp = "^[A-Z]{3}$", message = "Base airport must be a 3-letter IATA code")
        String baseAirport,

        @PastOrPresent(message = "Hire date must not be in the future")
        LocalDate hireDate
) {
}
