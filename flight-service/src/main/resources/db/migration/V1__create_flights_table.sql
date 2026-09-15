CREATE TABLE flights (
    id UUID PRIMARY KEY,
    flight_number VARCHAR(20) NOT NULL,
    departure_airport VARCHAR(3) NOT NULL,
    arrival_airport VARCHAR(3) NOT NULL,
    scheduled_departure TIMESTAMP NOT NULL,
    scheduled_arrival TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    aircraft_type VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_flight_number ON flights(flight_number);
CREATE INDEX idx_status ON flights(status);
CREATE INDEX idx_scheduled_departure ON flights(scheduled_departure);
CREATE INDEX idx_departure_airport ON flights(departure_airport);
CREATE INDEX idx_arrival_airport ON flights(arrival_airport);

ALTER TABLE flights ADD CONSTRAINT chk_scheduled_arrival_after_departure 
    CHECK (scheduled_arrival > scheduled_departure);

ALTER TABLE flights ADD CONSTRAINT chk_status 
    CHECK (status IN ('SCHEDULED', 'BOARDING', 'DEPARTED', 'ARRIVED', 'DELAYED', 'CANCELLED'));
