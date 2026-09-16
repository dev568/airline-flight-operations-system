package com.airline.operations.controller;

import com.airline.operations.dto.CreateFlightOperationRequest;
import com.airline.operations.dto.FlightOperationResponse;
import com.airline.operations.dto.UpdateFlightOperationRequest;
import com.airline.operations.model.OperationStatus;
import com.airline.operations.service.FlightOperationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/flight-operations")
public class FlightOperationController {

    private final FlightOperationService flightOperationService;

    public FlightOperationController(FlightOperationService flightOperationService) {
        this.flightOperationService = flightOperationService;
    }

    @PostMapping
    public ResponseEntity<FlightOperationResponse> createFlightOperation(@Valid @RequestBody CreateFlightOperationRequest request) {
        FlightOperationResponse response = flightOperationService.createFlightOperation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightOperationResponse> getFlightOperationById(@PathVariable UUID id) {
        FlightOperationResponse response = flightOperationService.getFlightOperationById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FlightOperationResponse>> getAllFlightOperations() {
        List<FlightOperationResponse> responses = flightOperationService.getAllFlightOperations();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<FlightOperationResponse>> searchFlightOperations(
            @RequestParam(required = false) UUID flightId,
            @RequestParam(required = false) String operationReference,
            @RequestParam(required = false) OperationStatus status,
            @RequestParam(required = false) String airportCode) {
        List<FlightOperationResponse> responses = flightOperationService.searchFlightOperations(
                flightId, operationReference, status, airportCode);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightOperationResponse> updateFlightOperation(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateFlightOperationRequest request) {
        FlightOperationResponse response = flightOperationService.updateFlightOperation(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlightOperation(@PathVariable UUID id) {
        flightOperationService.deleteFlightOperation(id);
        return ResponseEntity.noContent().build();
    }
}
