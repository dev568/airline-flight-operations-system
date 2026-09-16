CREATE TABLE flight_operations (
    id UUID PRIMARY KEY,
    flight_id UUID NOT NULL,
    operation_reference VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    scheduled_at TIMESTAMP NOT NULL,
    actual_at TIMESTAMP,
    airport_code VARCHAR(3) NOT NULL,
    remarks VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_flight_operations_flight_id ON flight_operations(flight_id);
CREATE INDEX idx_flight_operations_status ON flight_operations(status);
CREATE INDEX idx_flight_operations_airport_code ON flight_operations(airport_code);
CREATE INDEX idx_flight_operations_scheduled_at ON flight_operations(scheduled_at);

ALTER TABLE flight_operations ADD CONSTRAINT chk_operation_status 
    CHECK (status IN ('PLANNED', 'CHECK_IN_OPEN', 'BOARDING', 'DEPARTED', 'ARRIVED', 'DELAYED', 'CANCELLED', 'COMPLETED'));

ALTER TABLE flight_operations ADD CONSTRAINT chk_airport_code 
    CHECK (airport_code ~ '^[A-Z]{3}$');
