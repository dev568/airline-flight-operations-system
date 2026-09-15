CREATE TABLE crew_members (
    id UUID PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    base_airport VARCHAR(3) NOT NULL,
    hire_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_crew_members_employee_id ON crew_members(employee_id);
CREATE INDEX idx_crew_members_email ON crew_members(email);
CREATE INDEX idx_crew_members_role ON crew_members(role);
CREATE INDEX idx_crew_members_status ON crew_members(status);
CREATE INDEX idx_crew_members_base_airport ON crew_members(base_airport);
CREATE INDEX idx_crew_members_hire_date ON crew_members(hire_date);

ALTER TABLE crew_members ADD CONSTRAINT chk_crew_role 
    CHECK (role IN ('PILOT', 'COPILOT', 'CABIN_CREW', 'PURSER', 'FLIGHT_ENGINEER'));

ALTER TABLE crew_members ADD CONSTRAINT chk_crew_status 
    CHECK (status IN ('ACTIVE', 'INACTIVE', 'ON_LEAVE', 'SUSPENDED'));

ALTER TABLE crew_members ADD CONSTRAINT chk_base_airport 
    CHECK (base_airport ~ '^[A-Z]{3}$');
