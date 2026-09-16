package com.airline.operations.controller;

import com.airline.operations.OperationsServiceApplication;
import com.airline.operations.dto.CreateFlightOperationRequest;
import com.airline.operations.dto.FlightOperationResponse;
import com.airline.operations.dto.UpdateFlightOperationRequest;
import com.airline.operations.exception.FlightOperationNotFoundException;
import com.airline.operations.exception.InvalidStatusTransitionException;
import com.airline.operations.model.OperationStatus;
import com.airline.operations.service.FlightOperationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlightOperationController.class)
class FlightOperationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FlightOperationService flightOperationService;

    private FlightOperationResponse testFlightOperationResponse;
    private UUID testId;
    private UUID testFlightId;
    private Instant testScheduledAt;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testFlightId = UUID.randomUUID();
        testScheduledAt = Instant.now().plusSeconds(3600);
        testFlightOperationResponse = new FlightOperationResponse(
                testId,
                testFlightId,
                "OP001",
                OperationStatus.PLANNED,
                testScheduledAt,
                null,
                "JFK",
                "Test operation",
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    void createFlightOperation_WithValidRequest_ShouldReturn201() throws Exception {
        CreateFlightOperationRequest request = new CreateFlightOperationRequest(
                testFlightId,
                "OP001",
                OperationStatus.PLANNED,
                testScheduledAt,
                null,
                "JFK",
                "Test operation"
        );

        when(flightOperationService.createFlightOperation(any(CreateFlightOperationRequest.class))).thenReturn(testFlightOperationResponse);

        mockMvc.perform(post("/api/v1/flight-operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.operationReference").value("OP001"))
                .andExpect(jsonPath("$.flightId").value(testFlightId.toString()))
                .andExpect(jsonPath("$.status").value("PLANNED"));

        verify(flightOperationService, times(1)).createFlightOperation(any(CreateFlightOperationRequest.class));
    }

    @Test
    void createFlightOperation_WithInvalidFlightId_ShouldReturn400() throws Exception {
        CreateFlightOperationRequest request = new CreateFlightOperationRequest(
                null, // Missing flight ID
                "OP001",
                OperationStatus.PLANNED,
                testScheduledAt,
                null,
                "JFK",
                "Test operation"
        );

        mockMvc.perform(post("/api/v1/flight-operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(flightOperationService, never()).createFlightOperation(any(CreateFlightOperationRequest.class));
    }

    @Test
    void createFlightOperation_WithInvalidOperationReference_ShouldReturn400() throws Exception {
        CreateFlightOperationRequest request = new CreateFlightOperationRequest(
                testFlightId,
                "", // Empty operation reference
                OperationStatus.PLANNED,
                testScheduledAt,
                null,
                "JFK",
                "Test operation"
        );

        mockMvc.perform(post("/api/v1/flight-operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(flightOperationService, never()).createFlightOperation(any(CreateFlightOperationRequest.class));
    }

    @Test
    void createFlightOperation_WithInvalidAirportCode_ShouldReturn400() throws Exception {
        CreateFlightOperationRequest request = new CreateFlightOperationRequest(
                testFlightId,
                "OP001",
                OperationStatus.PLANNED,
                testScheduledAt,
                null,
                "INVALID", // Invalid airport code
                "Test operation"
        );

        mockMvc.perform(post("/api/v1/flight-operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(flightOperationService, never()).createFlightOperation(any(CreateFlightOperationRequest.class));
    }

    @Test
    void createFlightOperation_WithPastScheduledTime_ShouldReturn400() throws Exception {
        CreateFlightOperationRequest request = new CreateFlightOperationRequest(
                testFlightId,
                "OP001",
                OperationStatus.PLANNED,
                Instant.now().minusSeconds(3600), // Past time
                null,
                "JFK",
                "Test operation"
        );

        mockMvc.perform(post("/api/v1/flight-operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(flightOperationService, never()).createFlightOperation(any(CreateFlightOperationRequest.class));
    }

    @Test
    void createFlightOperation_WithMissingRequiredField_ShouldReturn400() throws Exception {
        CreateFlightOperationRequest request = new CreateFlightOperationRequest(
                testFlightId,
                "OP001",
                null, // Missing status
                testScheduledAt,
                null,
                "JFK",
                "Test operation"
        );

        mockMvc.perform(post("/api/v1/flight-operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(flightOperationService, never()).createFlightOperation(any(CreateFlightOperationRequest.class));
    }

    @Test
    void getFlightOperationById_WhenExists_ShouldReturn200() throws Exception {
        when(flightOperationService.getFlightOperationById(testId)).thenReturn(testFlightOperationResponse);

        mockMvc.perform(get("/api/v1/flight-operations/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.operationReference").value("OP001"));

        verify(flightOperationService, times(1)).getFlightOperationById(testId);
    }

    @Test
    void getFlightOperationById_WhenNotExists_ShouldReturn404() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        when(flightOperationService.getFlightOperationById(nonExistentId))
                .thenThrow(new FlightOperationNotFoundException(nonExistentId));

        mockMvc.perform(get("/api/v1/flight-operations/{id}", nonExistentId))
                .andExpect(status().isNotFound());

        verify(flightOperationService, times(1)).getFlightOperationById(nonExistentId);
    }

    @Test
    void getFlightOperationById_WithInvalidUuid_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/v1/flight-operations/invalid-uuid"))
                .andExpect(status().isBadRequest());

        verify(flightOperationService, never()).getFlightOperationById(any());
    }

    @Test
    void getAllFlightOperations_ShouldReturn200() throws Exception {
        when(flightOperationService.getAllFlightOperations()).thenReturn(List.of(testFlightOperationResponse));

        mockMvc.perform(get("/api/v1/flight-operations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testId.toString()))
                .andExpect(jsonPath("$[0].operationReference").value("OP001"));

        verify(flightOperationService, times(1)).getAllFlightOperations();
    }

    @Test
    void searchFlightOperations_WithoutParameters_ShouldReturn200() throws Exception {
        when(flightOperationService.searchFlightOperations(isNull(), isNull(), isNull(), isNull()))
                .thenReturn(List.of(testFlightOperationResponse));

        mockMvc.perform(get("/api/v1/flight-operations/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].operationReference").value("OP001"));

        verify(flightOperationService, times(1)).searchFlightOperations(isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void searchFlightOperations_ByFlightId_ShouldReturn200() throws Exception {
        when(flightOperationService.searchFlightOperations(eq(testFlightId), isNull(), isNull(), isNull()))
                .thenReturn(List.of(testFlightOperationResponse));

        mockMvc.perform(get("/api/v1/flight-operations/search")
                        .param("flightId", testFlightId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].flightId").value(testFlightId.toString()));

        verify(flightOperationService, times(1)).searchFlightOperations(eq(testFlightId), isNull(), isNull(), isNull());
    }

    @Test
    void searchFlightOperations_ByOperationReference_ShouldReturn200() throws Exception {
        when(flightOperationService.searchFlightOperations(isNull(), eq("OP"), isNull(), isNull()))
                .thenReturn(List.of(testFlightOperationResponse));

        mockMvc.perform(get("/api/v1/flight-operations/search")
                        .param("operationReference", "OP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].operationReference").value("OP001"));

        verify(flightOperationService, times(1)).searchFlightOperations(isNull(), eq("OP"), isNull(), isNull());
    }

    @Test
    void searchFlightOperations_ByStatus_ShouldReturn200() throws Exception {
        when(flightOperationService.searchFlightOperations(isNull(), isNull(), eq(OperationStatus.PLANNED), isNull()))
                .thenReturn(List.of(testFlightOperationResponse));

        mockMvc.perform(get("/api/v1/flight-operations/search")
                        .param("status", "PLANNED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PLANNED"));

        verify(flightOperationService, times(1)).searchFlightOperations(isNull(), isNull(), eq(OperationStatus.PLANNED), isNull());
    }

    @Test
    void searchFlightOperations_ByAirportCode_ShouldReturn200() throws Exception {
        when(flightOperationService.searchFlightOperations(isNull(), isNull(), isNull(), eq("JFK")))
                .thenReturn(List.of(testFlightOperationResponse));

        mockMvc.perform(get("/api/v1/flight-operations/search")
                        .param("airportCode", "JFK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].airportCode").value("JFK"));

        verify(flightOperationService, times(1)).searchFlightOperations(isNull(), isNull(), isNull(), eq("JFK"));
    }

    @Test
    void updateFlightOperation_WhenExists_ShouldReturn200() throws Exception {
        UpdateFlightOperationRequest request = new UpdateFlightOperationRequest(
                OperationStatus.BOARDING,
                testScheduledAt,
                Instant.now(),
                "LAX",
                "Updated remarks"
        );

        when(flightOperationService.updateFlightOperation(eq(testId), any(UpdateFlightOperationRequest.class)))
                .thenReturn(testFlightOperationResponse);

        mockMvc.perform(put("/api/v1/flight-operations/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(flightOperationService, times(1)).updateFlightOperation(eq(testId), any(UpdateFlightOperationRequest.class));
    }

    @Test
    void updateFlightOperation_WhenNotExists_ShouldReturn404() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        UpdateFlightOperationRequest request = new UpdateFlightOperationRequest(
                OperationStatus.BOARDING,
                testScheduledAt,
                Instant.now(),
                "LAX",
                "Updated remarks"
        );

        when(flightOperationService.updateFlightOperation(eq(nonExistentId), any(UpdateFlightOperationRequest.class)))
                .thenThrow(new FlightOperationNotFoundException(nonExistentId));

        mockMvc.perform(put("/api/v1/flight-operations/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(flightOperationService, times(1)).updateFlightOperation(eq(nonExistentId), any(UpdateFlightOperationRequest.class));
    }

    @Test
    void updateFlightOperation_WithInvalidStatusTransition_ShouldReturn400() throws Exception {
        UpdateFlightOperationRequest request = new UpdateFlightOperationRequest(
                OperationStatus.CANCELLED,
                testScheduledAt,
                Instant.now(),
                "LAX",
                "Updated remarks"
        );

        when(flightOperationService.updateFlightOperation(eq(testId), any(UpdateFlightOperationRequest.class)))
                .thenThrow(new InvalidStatusTransitionException(OperationStatus.PLANNED, OperationStatus.BOARDING));

        mockMvc.perform(put("/api/v1/flight-operations/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());

        verify(flightOperationService, times(1)).updateFlightOperation(eq(testId), any(UpdateFlightOperationRequest.class));
    }

    @Test
    void deleteFlightOperation_WhenExists_ShouldReturn204() throws Exception {
        doNothing().when(flightOperationService).deleteFlightOperation(testId);

        mockMvc.perform(delete("/api/v1/flight-operations/{id}", testId))
                .andExpect(status().isNoContent());

        verify(flightOperationService, times(1)).deleteFlightOperation(testId);
    }

    @Test
    void deleteFlightOperation_WhenNotExists_ShouldReturn404() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        doThrow(new FlightOperationNotFoundException(nonExistentId))
                .when(flightOperationService).deleteFlightOperation(nonExistentId);

        mockMvc.perform(delete("/api/v1/flight-operations/{id}", nonExistentId))
                .andExpect(status().isNotFound());

        verify(flightOperationService, times(1)).deleteFlightOperation(nonExistentId);
    }

    @Test
    void correlationId_WithHeader_ShouldPreserveValue() throws Exception {
        String correlationId = "550e8400-e29b-41d4-a716-446655440000";
        when(flightOperationService.getFlightOperationById(testId)).thenReturn(testFlightOperationResponse);

        mockMvc.perform(get("/api/v1/flight-operations/{id}", testId)
                        .header("X-Correlation-ID", correlationId))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Correlation-ID"))
                .andExpect(header().string("X-Correlation-ID", correlationId));
    }

    @Test
    void correlationId_WithoutHeader_ShouldGenerateValue() throws Exception {
        when(flightOperationService.getFlightOperationById(testId)).thenReturn(testFlightOperationResponse);

        mockMvc.perform(get("/api/v1/flight-operations/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Correlation-ID"));
    }

    @Test
    void correlationId_WithErrorResponse_ShouldContainCorrelationId() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        String correlationId = "550e8400-e29b-41d4-a716-446655440000";
        when(flightOperationService.getFlightOperationById(nonExistentId))
                .thenThrow(new FlightOperationNotFoundException(nonExistentId));

        mockMvc.perform(get("/api/v1/flight-operations/{id}", nonExistentId)
                        .header("X-Correlation-ID", correlationId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.correlationId").value(correlationId));
    }
}
