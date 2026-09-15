package com.airline.crew.dto;

import com.airline.crew.model.CrewRole;
import com.airline.crew.model.CrewStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record CreateCrewMemberRequest(
        @NotBlank(message = "Employee ID is required")
        @Size(min = 1, max = 50, message = "Employee ID must be between 1 and 50 characters")
        String employeeId,

        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name must not exceed 100 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name must not exceed 100 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        @NotNull(message = "Role is required")
        CrewRole role,

        @NotNull(message = "Status is required")
        CrewStatus status,

        @NotBlank(message = "Base airport is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Base airport must be a 3-letter IATA code")
        String baseAirport,

        @NotNull(message = "Hire date is required")
        @PastOrPresent(message = "Hire date must not be in the future")
        LocalDate hireDate
) {
}
