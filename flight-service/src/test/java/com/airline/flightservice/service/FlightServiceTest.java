package com.airline.flightservice.service;

import com.airline.flightservice.dto.CreateFlightRequest;
import com.airline.flightservice.dto.FlightResponse;
import com.airline.flightservice.dto.UpdateFlightRequest;
import com.airline.flightservice.entity.Flight;
import com.airline.flightservice.exception.FlightNotFoundException;
import com.airline.flightservice.model.FlightStatus;
import com.airline.flightservice.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightService flightService;

    private Flight testFlight;
    private UUID testId;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        now = LocalDateTime.now();
        testFlight = new Flight(
                "AA123",
                "JFK",
                "LAX",
                now.plusHours(2),
                now.plusHours(5),
                FlightStatus.SCHEDULED,
                "Boeing 737"
        );
        testFlight.setId(testId);
    }

    @Test
    void createFlight_ShouldReturnFlightResponse() {
        CreateFlightRequest request = new CreateFlightRequest(
                "AA123",
                "JFK",
                "LAX",
                now.plusHours(2),
                now.plusHours(5),
                FlightStatus.SCHEDULED,
                "Boeing 737"
        );

        when(flightRepository.save(any(Flight.class))).thenReturn(testFlight);

        FlightResponse response = flightService.createFlight(request);

        assertNotNull(response);
        assertEquals("AA123", response.flightNumber());
        assertEquals("JFK", response.departureAirport());
        assertEquals("LAX", response.arrivalAirport());
        assertEquals(FlightStatus.SCHEDULED, response.status());
        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    void getFlightById_WhenFlightExists_ShouldReturnFlightResponse() {
        when(flightRepository.findById(testId)).thenReturn(Optional.of(testFlight));

        FlightResponse response = flightService.getFlightById(testId);

        assertNotNull(response);
        assertEquals(testId, response.id());
        assertEquals("AA123", response.flightNumber());
        verify(flightRepository, times(1)).findById(testId);
    }

    @Test
    void getFlightById_WhenFlightNotFound_ShouldThrowException() {
        UUID nonExistentId = UUID.randomUUID();
        when(flightRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(FlightNotFoundException.class, () -> flightService.getFlightById(nonExistentId));
        verify(flightRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void updateFlight_WhenFlightExists_ShouldReturnUpdatedFlightResponse() {
        UpdateFlightRequest request = new UpdateFlightRequest(
                "AA456",
                "SFO",
                "ORD",
                now.plusHours(3),
                now.plusHours(6),
                FlightStatus.DEPARTED,
                "Airbus A320"
        );

        when(flightRepository.findById(testId)).thenReturn(Optional.of(testFlight));
        when(flightRepository.save(any(Flight.class))).thenReturn(testFlight);

        FlightResponse response = flightService.updateFlight(testId, request);

        assertNotNull(response);
        verify(flightRepository, times(1)).findById(testId);
        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    void updateFlight_WhenFlightNotFound_ShouldThrowException() {
        UUID nonExistentId = UUID.randomUUID();
        UpdateFlightRequest request = new UpdateFlightRequest(
                "AA456",
                "SFO",
                "ORD",
                now.plusHours(3),
                now.plusHours(6),
                FlightStatus.DEPARTED,
                "Airbus A320"
        );

        when(flightRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(FlightNotFoundException.class, () -> flightService.updateFlight(nonExistentId, request));
        verify(flightRepository, times(1)).findById(nonExistentId);
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void deleteFlight_WhenFlightExists_ShouldDeleteFlight() {
        when(flightRepository.findById(testId)).thenReturn(Optional.of(testFlight));
        doNothing().when(flightRepository).delete(testFlight);

        flightService.deleteFlight(testId);

        verify(flightRepository, times(1)).findById(testId);
        verify(flightRepository, times(1)).delete(testFlight);
    }

    @Test
    void deleteFlight_WhenFlightNotFound_ShouldThrowException() {
        UUID nonExistentId = UUID.randomUUID();
        when(flightRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(FlightNotFoundException.class, () -> flightService.deleteFlight(nonExistentId));
        verify(flightRepository, times(1)).findById(nonExistentId);
        verify(flightRepository, never()).delete(any(Flight.class));
    }
}
