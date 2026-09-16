package com.airline.operations.repository;

import com.airline.operations.entity.FlightOperation;
import com.airline.operations.model.OperationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FlightOperationRepository extends JpaRepository<FlightOperation, UUID> {

    boolean existsByOperationReference(String operationReference);

    List<FlightOperation> findByFlightId(UUID flightId);

    List<FlightOperation> findByOperationReferenceContainingIgnoreCase(String operationReference);

    List<FlightOperation> findByStatus(OperationStatus status);

    List<FlightOperation> findByAirportCode(String airportCode);

    List<FlightOperation> findByFlightIdAndStatus(UUID flightId, OperationStatus status);

    List<FlightOperation> findByFlightIdAndAirportCode(UUID flightId, String airportCode);

    List<FlightOperation> findByStatusAndAirportCode(OperationStatus status, String airportCode);

    List<FlightOperation> findByFlightIdAndStatusAndAirportCode(UUID flightId, OperationStatus status, String airportCode);
}
