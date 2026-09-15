package com.airline.flightservice.repository;

import com.airline.flightservice.entity.Flight;
import com.airline.flightservice.model.FlightStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FlightRepository extends JpaRepository<Flight, UUID> {

    Optional<Flight> findByFlightNumber(String flightNumber);

    List<Flight> findByStatus(FlightStatus status);

    List<Flight> findByDepartureAirport(String departureAirport);

    List<Flight> findByArrivalAirport(String arrivalAirport);

    List<Flight> findByFlightNumberContainingIgnoreCase(String flightNumber);

    boolean existsByFlightNumber(String flightNumber);
}
