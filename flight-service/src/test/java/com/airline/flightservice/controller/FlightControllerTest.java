package com.airline.flightservice.controller;

import com.airline.flightservice.dto.CreateFlightRequest;
import com.airline.flightservice.dto.FlightResponse;
import com.airline.flightservice.dto.UpdateFlightRequest;
import com.airline.flightservice.exception.FlightNotFoundException;
import com.airline.flightservice.model.FlightStatus;
import com.airline.flightservice.service.FlightService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlightController.class)
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FlightService flightService;

    private FlightResponse testFlightResponse;
    private UUID testId;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        now = LocalDateTime.now();
        testFlightResponse = new FlightResponse(
                testId,
                "AA123",
                "JFK",
                "LAX",
                now.plusHours(2),
                now.plusHours(5),
                FlightStatus.SCHEDULED,
                "Boeing 737",
                now,
                now
        );
    }

    @Test
    void createFlight_WithValidRequest_ShouldReturn201() throws Exception {
        CreateFlightRequest request = new CreateFlightRequest(
                "AA123",
                "JFK",
                "LAX",
                now.plusHours(2),
                now.plusHours(5),
                FlightStatus.SCHEDULED,
                "Boeing 737"
        );

        when(flightService.createFlight(any(CreateFlightRequest.class))).thenReturn(testFlightResponse);

        mockMvc.perform(post("/api/v1/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.flightNumber").value("AA123"))
                .andExpect(jsonPath("$.departureAirport").value("JFK"))
                .andExpect(jsonPath("$.arrivalAirport").value("LAX"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));

        verify(flightService, times(1)).createFlight(any(CreateFlightRequest.class));
    }

    @Test
    void createFlight_WithInvalidFlightNumber_ShouldReturn400() throws Exception {
        CreateFlightRequest request = new CreateFlightRequest(
                "INVALID", // Invalid format
                "JFK",
                "LAX",
                now.plusHours(2),
                now.plusHours(5),
                FlightStatus.SCHEDULED,
                "Boeing 737"
        );

        mockMvc.perform(post("/api/v1/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(flightService, never()).createFlight(any(CreateFlightRequest.class));
    }

    @Test
    void createFlight_WithInvalidAirportCode_ShouldReturn400() throws Exception {
        CreateFlightRequest request = new CreateFlightRequest(
                "AA123",
                "INVALID", // Invalid airport code
                "LAX",
                now.plusHours(2),
                now.plusHours(5),
                FlightStatus.SCHEDULED,
                "Boeing 737"
        );

        mockMvc.perform(post("/api/v1/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(flightService, never()).createFlight(any(CreateFlightRequest.class));
    }

    @Test
    void createFlight_WithArrivalBeforeDeparture_ShouldReturn400() throws Exception {
        CreateFlightRequest request = new CreateFlightRequest(
                "AA123",
                "JFK",
                "LAX",
                now.plusHours(5),
                now.plusHours(2), // Arrival before departure
                FlightStatus.SCHEDULED,
                "Boeing 737"
        );

        mockMvc.perform(post("/api/v1/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(flightService, never()).createFlight(any(CreateFlightRequest.class));
    }

    @Test
    void createFlight_WithMissingRequiredField_ShouldReturn400() throws Exception {
        CreateFlightRequest request = new CreateFlightRequest(
                null, // Missing flight number
                "JFK",
                "LAX",
                now.plusHours(2),
                now.plusHours(5),
                FlightStatus.SCHEDULED,
                "Boeing 737"
        );

        mockMvc.perform(post("/api/v1/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(flightService, never()).createFlight(any(CreateFlightRequest.class));
    }

    @Test
    void getFlightById_WhenFlightExists_ShouldReturn200() throws Exception {
        when(flightService.getFlightById(testId)).thenReturn(testFlightResponse);

        mockMvc.perform(get("/api/v1/flights/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.flightNumber").value("AA123"));

        verify(flightService, times(1)).getFlightById(testId);
    }

    @Test
    void getFlightById_WhenFlightNotFound_ShouldReturn404() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        when(flightService.getFlightById(nonExistentId))
                .thenThrow(new FlightNotFoundException(nonExistentId));

        mockMvc.perform(get("/api/v1/flights/{id}", nonExistentId))
                .andExpect(status().isNotFound());

        verify(flightService, times(1)).getFlightById(nonExistentId);
    }

    @Test
    void getAllFlights_ShouldReturn200() throws Exception {
        when(flightService.getAllFlights()).thenReturn(List.of(testFlightResponse));

        mockMvc.perform(get("/api/v1/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testId.toString()))
                .andExpect(jsonPath("$[0].flightNumber").value("AA123"));

        verify(flightService, times(1)).getAllFlights();
    }

    @Test
    void updateFlight_WhenFlightExists_ShouldReturn200() throws Exception {
        UpdateFlightRequest request = new UpdateFlightRequest(
                "AA456",
                "SFO",
                "ORD",
                now.plusHours(3),
                now.plusHours(6),
                FlightStatus.DEPARTED,
                "Airbus A320"
        );

        when(flightService.updateFlight(eq(testId), any(UpdateFlightRequest.class)))
                .thenReturn(testFlightResponse);

        mockMvc.perform(put("/api/v1/flights/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(flightService, times(1)).updateFlight(eq(testId), any(UpdateFlightRequest.class));
    }

    @Test
    void deleteFlight_WhenFlightExists_ShouldReturn204() throws Exception {
        doNothing().when(flightService).deleteFlight(testId);

        mockMvc.perform(delete("/api/v1/flights/{id}", testId))
                .andExpect(status().isNoContent());

        verify(flightService, times(1)).deleteFlight(testId);
    }

    @Test
    void deleteFlight_WhenFlightNotFound_ShouldReturn404() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        doThrow(new FlightNotFoundException(nonExistentId))
                .when(flightService).deleteFlight(nonExistentId);

        mockMvc.perform(delete("/api/v1/flights/{id}", nonExistentId))
                .andExpect(status().isNotFound());

        verify(flightService, times(1)).deleteFlight(nonExistentId);
    }

    @Test
    void searchFlights_WithFlightNumber_ShouldReturn200() throws Exception {
        when(flightService.searchFlights(eq("AA123"), isNull(), isNull(), isNull()))
                .thenReturn(List.of(testFlightResponse));

        mockMvc.perform(get("/api/v1/flights/search")
                        .param("flightNumber", "AA123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].flightNumber").value("AA123"));

        verify(flightService, times(1)).searchFlights(eq("AA123"), isNull(), isNull(), isNull());
    }

    @Test
    void searchFlights_WithStatus_ShouldReturn200() throws Exception {
        when(flightService.searchFlights(isNull(), eq(FlightStatus.SCHEDULED), isNull(), isNull()))
                .thenReturn(List.of(testFlightResponse));

        mockMvc.perform(get("/api/v1/flights/search")
                        .param("status", "SCHEDULED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("SCHEDULED"));

        verify(flightService, times(1)).searchFlights(isNull(), eq(FlightStatus.SCHEDULED), isNull(), isNull());
    }

    @Test
    void getFlightById_WithInvalidUuid_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/v1/flights/invalid-uuid"))
                .andExpect(status().isBadRequest());

        verify(flightService, never()).getFlightById(any());
    }

    @Test
    void searchFlights_WithoutParameters_ShouldReturn200() throws Exception {
        when(flightService.searchFlights(isNull(), isNull(), isNull(), isNull()))
                .thenReturn(List.of(testFlightResponse));

        mockMvc.perform(get("/api/v1/flights/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].flightNumber").value("AA123"));

        verify(flightService, times(1)).searchFlights(isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void createFlight_WithInvalidSchedule_ArrivalBeforeDeparture_ShouldReturn400() throws Exception {
        CreateFlightRequest request = new CreateFlightRequest(
                "AA123",
                "JFK",
                "LAX",
                now.plusHours(5),
                now.plusHours(2), // Arrival before departure
                FlightStatus.SCHEDULED,
                "Boeing 737"
        );

        mockMvc.perform(post("/api/v1/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(flightService, never()).createFlight(any(CreateFlightRequest.class));
    }

    @Test
    void updateFlight_WithInvalidSchedule_ArrivalBeforeDeparture_ShouldReturn400() throws Exception {
        UpdateFlightRequest request = new UpdateFlightRequest(
                "AA123",
                "JFK",
                "LAX",
                now.plusHours(5),
                now.plusHours(2), // Arrival before departure
                FlightStatus.SCHEDULED,
                "Boeing 737"
        );

        mockMvc.perform(put("/api/v1/flights/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(flightService, never()).updateFlight(eq(testId), any(UpdateFlightRequest.class));
    }

    @Test
    void createFlight_WithValidSchedule_ArrivalAfterDeparture_ShouldReturn201() throws Exception {
        CreateFlightRequest request = new CreateFlightRequest(
                "AA123",
                "JFK",
                "LAX",
                now.plusHours(2),
                now.plusHours(5), // Valid: arrival after departure
                FlightStatus.SCHEDULED,
                "Boeing 737"
        );

        when(flightService.createFlight(any(CreateFlightRequest.class))).thenReturn(testFlightResponse);

        mockMvc.perform(post("/api/v1/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(flightService, times(1)).createFlight(any(CreateFlightRequest.class));
    }

    @Test
    void correlationId_WithHeader_ShouldPreserveValue() throws Exception {
        String correlationId = "550e8400-e29b-41d4-a716-446655440000";
        when(flightService.getFlightById(testId)).thenReturn(testFlightResponse);

        mockMvc.perform(get("/api/v1/flights/{id}", testId)
                        .header("X-Correlation-ID", correlationId))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Correlation-ID"))
                .andExpect(header().string("X-Correlation-ID", correlationId));
    }

    @Test
    void correlationId_WithoutHeader_ShouldGenerateValue() throws Exception {
        when(flightService.getFlightById(testId)).thenReturn(testFlightResponse);

        mockMvc.perform(get("/api/v1/flights/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Correlation-ID"));
    }

    @Test
    void correlationId_WithErrorResponse_ShouldContainCorrelationId() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        String correlationId = "550e8400-e29b-41d4-a716-446655440000";
        when(flightService.getFlightById(nonExistentId))
                .thenThrow(new FlightNotFoundException(nonExistentId));

        mockMvc.perform(get("/api/v1/flights/{id}", nonExistentId)
                        .header("X-Correlation-ID", correlationId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.correlationId").value(correlationId));
    }
}
