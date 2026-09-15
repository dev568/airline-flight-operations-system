package com.airline.flightservice.service;

import com.airline.flightservice.dto.CreateFlightRequest;
import com.airline.flightservice.dto.FlightResponse;
import com.airline.flightservice.dto.UpdateFlightRequest;
import com.airline.flightservice.entity.Flight;
import com.airline.flightservice.exception.FlightNotFoundException;
import com.airline.flightservice.model.FlightStatus;
import com.airline.flightservice.repository.FlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public FlightResponse createFlight(CreateFlightRequest request) {
        Flight flight = new Flight(
                request.flightNumber(),
                request.departureAirport(),
                request.arrivalAirport(),
                request.scheduledDeparture(),
                request.scheduledArrival(),
                request.status(),
                request.aircraftType()
        );
        Flight savedFlight = flightRepository.save(flight);
        return mapToResponse(savedFlight);
    }

    public FlightResponse getFlightById(UUID id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new FlightNotFoundException(id));
        return mapToResponse(flight);
    }

    public List<FlightResponse> getAllFlights() {
        return flightRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public FlightResponse updateFlight(UUID id, UpdateFlightRequest request) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new FlightNotFoundException(id));

        if (request.flightNumber() != null) {
            flight.setFlightNumber(request.flightNumber());
        }
        if (request.departureAirport() != null) {
            flight.setDepartureAirport(request.departureAirport());
        }
        if (request.arrivalAirport() != null) {
            flight.setArrivalAirport(request.arrivalAirport());
        }
        if (request.scheduledDeparture() != null) {
            flight.setScheduledDeparture(request.scheduledDeparture());
        }
        if (request.scheduledArrival() != null) {
            flight.setScheduledArrival(request.scheduledArrival());
        }
        if (request.status() != null) {
            flight.setStatus(request.status());
        }
        if (request.aircraftType() != null) {
            flight.setAircraftType(request.aircraftType());
        }

        Flight updatedFlight = flightRepository.save(flight);
        return mapToResponse(updatedFlight);
    }

    public void deleteFlight(UUID id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new FlightNotFoundException(id));
        flightRepository.delete(flight);
    }

    public List<FlightResponse> searchFlights(String flightNumber, FlightStatus status,
                                               String departureAirport, String arrivalAirport) {
        List<Flight> flights;

        if (flightNumber != null && !flightNumber.isEmpty()) {
            flights = flightRepository.findByFlightNumberContainingIgnoreCase(flightNumber);
        } else if (status != null) {
            flights = flightRepository.findByStatus(status);
        } else if (departureAirport != null && !departureAirport.isEmpty()) {
            flights = flightRepository.findByDepartureAirport(departureAirport);
        } else if (arrivalAirport != null && !arrivalAirport.isEmpty()) {
            flights = flightRepository.findByArrivalAirport(arrivalAirport);
        } else {
            flights = flightRepository.findAll();
        }

        return flights.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private FlightResponse mapToResponse(Flight flight) {
        return new FlightResponse(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getDepartureAirport(),
                flight.getArrivalAirport(),
                flight.getScheduledDeparture(),
                flight.getScheduledArrival(),
                flight.getStatus(),
                flight.getAircraftType(),
                flight.getCreatedAt(),
                flight.getUpdatedAt()
        );
    }
}
