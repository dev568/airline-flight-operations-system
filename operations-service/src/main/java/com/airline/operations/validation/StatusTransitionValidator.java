package com.airline.operations.validation;
import com.airline.operations.exception.InvalidStatusTransitionException;
import com.airline.operations.model.OperationStatus;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

public class StatusTransitionValidator {
    
    private static final EnumMap<OperationStatus, Set<OperationStatus>> VALID_TRANSITIONS = new EnumMap<>(OperationStatus.class);
    static {
        // PLANNED can transition to: CHECK_IN_OPEN, DELAYED, CANCELLED
        Set<OperationStatus> plannedTransitions = new HashSet<>();
        plannedTransitions.add(OperationStatus.CHECK_IN_OPEN);
        plannedTransitions.add(OperationStatus.DELAYED);
        plannedTransitions.add(OperationStatus.CANCELLED);
        VALID_TRANSITIONS.put(OperationStatus.PLANNED, plannedTransitions);
       
        // CHECK_IN_OPEN can transition to: BOARDING, DELAYED, CANCELLED
        Set<OperationStatus> checkInTransitions = new HashSet<>();
        checkInTransitions.add(OperationStatus.BOARDING);
        checkInTransitions.add(OperationStatus.DELAYED);
        checkInTransitions.add(OperationStatus.CANCELLED);
        VALID_TRANSITIONS.put(OperationStatus.CHECK_IN_OPEN, checkInTransitions);
        
        // BOARDING can transition to: DEPARTED, DELAYED, CANCELLED
        Set<OperationStatus> boardingTransitions = new HashSet<>();
        boardingTransitions.add(OperationStatus.DEPARTED);
        boardingTransitions.add(OperationStatus.DELAYED);
        boardingTransitions.add(OperationStatus.CANCELLED);
        VALID_TRANSITIONS.put(OperationStatus.BOARDING, boardingTransitions);
        
        // DEPARTED can transition to: ARRIVED, DELAYED
        Set<OperationStatus> departedTransitions = new HashSet<>();
        departedTransitions.add(OperationStatus.ARRIVED);
        departedTransitions.add(OperationStatus.DELAYED);
        VALID_TRANSITIONS.put(OperationStatus.DEPARTED, departedTransitions);
        
        // ARRIVED can transition to: COMPLETED
        Set<OperationStatus> arrivedTransitions = new HashSet<>();
        arrivedTransitions.add(OperationStatus.COMPLETED);
        VALID_TRANSITIONS.put(OperationStatus.ARRIVED, arrivedTransitions);
        
        // DELAYED can transition to: CANCELLED or any operational state (handled specially)
        Set<OperationStatus> delayedTransitions = new HashSet<>();
        delayedTransitions.add(OperationStatus.CANCELLED);
        VALID_TRANSITIONS.put(OperationStatus.DELAYED, delayedTransitions);
        
        // CANCELLED is a terminal state - no transitions
        VALID_TRANSITIONS.put(OperationStatus.CANCELLED, Set.of());
        
        // COMPLETED is a terminal state - no transitions
        VALID_TRANSITIONS.put(OperationStatus.COMPLETED, Set.of());
    }
    
    public static void validateTransition(OperationStatus currentStatus, OperationStatus newStatus) {
        if (currentStatus == newStatus) {
            return; // No change is valid
        }
        
        Set<OperationStatus> allowedTransitions = VALID_TRANSITIONS.get(currentStatus);
        
        if (allowedTransitions == null) {
            throw new InvalidStatusTransitionException(currentStatus, newStatus);
        }
        
        if (allowedTransitions.contains(newStatus)) {
            return;
        }
        
        // Special handling for DELAYED state - can return to any operational state
        if (currentStatus == OperationStatus.DELAYED && isOperationalState(newStatus)) {
            return;
        }
        
        throw new InvalidStatusTransitionException(currentStatus, newStatus);
    }
    
    private static boolean isOperationalState(OperationStatus status) {
        return status == OperationStatus.PLANNED || 
               status == OperationStatus.CHECK_IN_OPEN || 
               status == OperationStatus.BOARDING || 
               status == OperationStatus.DEPARTED ||
               status == OperationStatus.ARRIVED;
    }
}
