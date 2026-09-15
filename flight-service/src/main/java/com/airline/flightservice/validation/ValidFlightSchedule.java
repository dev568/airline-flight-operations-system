package com.airline.flightservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidFlightScheduleValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFlightSchedule {
    String message() default "Scheduled arrival must be after scheduled departure";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
