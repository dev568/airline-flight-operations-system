package com.airline.operations.exception;

import com.airline.operations.model.OperationStatus;

public class InvalidStatusTransitionException extends RuntimeException {
    private final OperationStatus currentStatus;
    private final OperationStatus requestedStatus;

    public InvalidStatusTransitionException(OperationStatus currentStatus, OperationStatus requestedStatus) {
        super(String.format("Invalid status transition from %s to %s", currentStatus, requestedStatus));
        this.currentStatus = currentStatus;
        this.requestedStatus = requestedStatus;
    }

    public OperationStatus getCurrentStatus() {
        return currentStatus;
    }

    public OperationStatus getRequestedStatus() {
        return requestedStatus;
    }
}
