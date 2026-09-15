package com.airline.flightservice.controller;

import com.airline.flightservice.dto.CreateFlightRequest;
import com.airline.flightservice.dto.FlightResponse;
import com.airline.flightservice.dto.UpdateFlightRequest;
import com.airline.flightservice.model.FlightStatus;
import com.airline.flightservice.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping
    public ResponseEntity<FlightResponse> createFlight(@Valid @RequestBody CreateFlightRequest request) {
        FlightResponse response = flightService.createFlight(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightResponse> getFlightById(@PathVariable UUID id) {
        FlightResponse response = flightService.getFlightById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FlightResponse>> getAllFlights() {
        List<FlightResponse> responses = flightService.getAllFlights();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<FlightResponse>> searchFlights(
            @RequestParam(required = false) String flightNumber,
            @RequestParam(required = false) FlightStatus status,
            @RequestParam(required = false) String departureAirport,
            @RequestParam(required = false) String arrivalAirport) {
        List<FlightResponse> responses = flightService.searchFlights(
                flightNumber, status, departureAirport, arrivalAirport);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightResponse> updateFlight(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateFlightRequest request) {
        FlightResponse response = flightService.updateFlight(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable UUID id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }
}
