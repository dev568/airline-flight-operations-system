-- Create separate logical databases for each service
CREATE DATABASE flight_db;
CREATE DATABASE crew_db;
CREATE DATABASE operations_db;

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE flight_db TO airline_user;
GRANT ALL PRIVILEGES ON DATABASE crew_db TO airline_user;
GRANT ALL PRIVILEGES ON DATABASE operations_db TO airline_user;
