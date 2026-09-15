package com.airline.flightservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class ValidFlightScheduleValidator implements ConstraintValidator<ValidFlightSchedule, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            java.lang.reflect.Field scheduledDepartureField = value.getClass().getDeclaredField("scheduledDeparture");
            java.lang.reflect.Field scheduledArrivalField = value.getClass().getDeclaredField("scheduledArrival");
            
            scheduledDepartureField.setAccessible(true);
            scheduledArrivalField.setAccessible(true);
            
            LocalDateTime scheduledDeparture = (LocalDateTime) scheduledDepartureField.get(value);
            LocalDateTime scheduledArrival = (LocalDateTime) scheduledArrivalField.get(value);
            
            if (scheduledDeparture == null || scheduledArrival == null) {
                return true;
            }
            
            return scheduledArrival.isAfter(scheduledDeparture);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return true;
        }
    }
}
