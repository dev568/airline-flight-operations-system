package com.airline.operations.service;

import com.airline.operations.dto.CreateFlightOperationRequest;
import com.airline.operations.dto.FlightOperationResponse;
import com.airline.operations.dto.UpdateFlightOperationRequest;
import com.airline.operations.entity.FlightOperation;
import com.airline.operations.exception.FlightOperationNotFoundException;
import com.airline.operations.model.OperationStatus;
import com.airline.operations.repository.FlightOperationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FlightOperationService {

    private final FlightOperationRepository flightOperationRepository;

    public FlightOperationService(FlightOperationRepository flightOperationRepository) {
        this.flightOperationRepository = flightOperationRepository;
    }

    public FlightOperationResponse createFlightOperation(CreateFlightOperationRequest request) {
        if (flightOperationRepository.existsByOperationReference(request.operationReference())) {
            throw new IllegalArgumentException("Operation reference already exists: " + request.operationReference());
        }

        FlightOperation flightOperation = new FlightOperation();
        flightOperation.setFlightId(request.flightId());
        flightOperation.setOperationReference(request.operationReference());
        flightOperation.setStatus(request.status());
        flightOperation.setScheduledAt(request.scheduledAt());
        flightOperation.setActualAt(request.actualAt());
        flightOperation.setAirportCode(request.airportCode());
        flightOperation.setRemarks(request.remarks());

        FlightOperation saved = flightOperationRepository.save(flightOperation);
        return mapToResponse(saved);
    }

    public FlightOperationResponse getFlightOperationById(UUID id) {
        FlightOperation flightOperation = flightOperationRepository.findById(id)
                .orElseThrow(() -> new FlightOperationNotFoundException(id));
        return mapToResponse(flightOperation);
    }

    public List<FlightOperationResponse> getAllFlightOperations() {
        return flightOperationRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<FlightOperationResponse> searchFlightOperations(UUID flightId, String operationReference, 
                                                                 OperationStatus status, String airportCode) {
        List<FlightOperation> results;

        if (flightId != null && status != null && airportCode != null) {
            results = flightOperationRepository.findByFlightIdAndStatusAndAirportCode(flightId, status, airportCode);
        } else if (flightId != null && status != null) {
            results = flightOperationRepository.findByFlightIdAndStatus(flightId, status);
        } else if (flightId != null && airportCode != null) {
            results = flightOperationRepository.findByFlightIdAndAirportCode(flightId, airportCode);
        } else if (status != null && airportCode != null) {
            results = flightOperationRepository.findByStatusAndAirportCode(status, airportCode);
        } else if (flightId != null) {
            results = flightOperationRepository.findByFlightId(flightId);
        } else if (operationReference != null && !operationReference.isBlank()) {
            results = flightOperationRepository.findByOperationReferenceContainingIgnoreCase(operationReference);
        } else if (status != null) {
            results = flightOperationRepository.findByStatus(status);
        } else if (airportCode != null) {
            results = flightOperationRepository.findByAirportCode(airportCode);
        } else {
            results = flightOperationRepository.findAll();
        }

        return results.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public FlightOperationResponse updateFlightOperation(UUID id, UpdateFlightOperationRequest request) {
        FlightOperation flightOperation = flightOperationRepository.findById(id)
                .orElseThrow(() -> new FlightOperationNotFoundException(id));

        if (request.status() != null) {
            flightOperation.setStatus(request.status());
        }
        if (request.scheduledAt() != null) {
            flightOperation.setScheduledAt(request.scheduledAt());
        }
        if (request.actualAt() != null) {
            flightOperation.setActualAt(request.actualAt());
        }
        if (request.airportCode() != null) {
            flightOperation.setAirportCode(request.airportCode());
        }
        if (request.remarks() != null) {
            flightOperation.setRemarks(request.remarks());
        }

        FlightOperation updated = flightOperationRepository.save(flightOperation);
        return mapToResponse(updated);
    }

    public void deleteFlightOperation(UUID id) {
        if (!flightOperationRepository.existsById(id)) {
            throw new FlightOperationNotFoundException(id);
        }
        flightOperationRepository.deleteById(id);
    }

    private FlightOperationResponse mapToResponse(FlightOperation flightOperation) {
        return new FlightOperationResponse(
                flightOperation.getId(),
                flightOperation.getFlightId(),
                flightOperation.getOperationReference(),
                flightOperation.getStatus(),
                flightOperation.getScheduledAt(),
                flightOperation.getActualAt(),
                flightOperation.getAirportCode(),
                flightOperation.getRemarks(),
                flightOperation.getCreatedAt(),
                flightOperation.getUpdatedAt()
        );
    }
}
