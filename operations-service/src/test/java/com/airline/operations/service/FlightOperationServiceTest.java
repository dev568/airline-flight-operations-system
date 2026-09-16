package com.airline.operations.service;

import com.airline.operations.dto.CreateFlightOperationRequest;
import com.airline.operations.dto.FlightOperationResponse;
import com.airline.operations.dto.UpdateFlightOperationRequest;
import com.airline.operations.entity.FlightOperation;
import com.airline.operations.exception.FlightOperationNotFoundException;
import com.airline.operations.model.OperationStatus;
import com.airline.operations.repository.FlightOperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightOperationServiceTest {

    @Mock
    private FlightOperationRepository flightOperationRepository;

    @InjectMocks
    private FlightOperationService flightOperationService;

    private FlightOperation testFlightOperation;
    private UUID testId;
    private UUID testFlightId;
    private Instant testScheduledAt;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testFlightId = UUID.randomUUID();
        testScheduledAt = Instant.now().plusSeconds(3600);
        testFlightOperation = new FlightOperation();
        testFlightOperation.setId(testId);
        testFlightOperation.setFlightId(testFlightId);
        testFlightOperation.setOperationReference("OP001");
        testFlightOperation.setStatus(OperationStatus.PLANNED);
        testFlightOperation.setScheduledAt(testScheduledAt);
        testFlightOperation.setActualAt(null);
        testFlightOperation.setAirportCode("JFK");
        testFlightOperation.setRemarks("Test operation");
        testFlightOperation.setCreatedAt(Instant.now());
        testFlightOperation.setUpdatedAt(Instant.now());
    }

    @Test
    void createFlightOperation_WithValidRequest_ShouldReturnResponse() {
        CreateFlightOperationRequest request = new CreateFlightOperationRequest(
                testFlightId,
                "OP001",
                OperationStatus.PLANNED,
                testScheduledAt,
                null,
                "JFK",
                "Test operation"
        );

        when(flightOperationRepository.existsByOperationReference("OP001")).thenReturn(false);
        when(flightOperationRepository.save(any(FlightOperation.class))).thenReturn(testFlightOperation);

        FlightOperationResponse response = flightOperationService.createFlightOperation(request);

        assertNotNull(response);
        assertEquals("OP001", response.operationReference());
        assertEquals(testFlightId, response.flightId());
        verify(flightOperationRepository, times(1)).save(any(FlightOperation.class));
    }

    @Test
    void createFlightOperation_WithDuplicateOperationReference_ShouldThrowException() {
        CreateFlightOperationRequest request = new CreateFlightOperationRequest(
                testFlightId,
                "OP001",
                OperationStatus.PLANNED,
                testScheduledAt,
                null,
                "JFK",
                "Test operation"
        );

        when(flightOperationRepository.existsByOperationReference("OP001")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> flightOperationService.createFlightOperation(request)
        );

        assertTrue(exception.getMessage().contains("Operation reference already exists"));
        verify(flightOperationRepository, never()).save(any(FlightOperation.class));
    }

    @Test
    void getFlightOperationById_WhenExists_ShouldReturnResponse() {
        when(flightOperationRepository.findById(testId)).thenReturn(java.util.Optional.of(testFlightOperation));

        FlightOperationResponse response = flightOperationService.getFlightOperationById(testId);

        assertNotNull(response);
        assertEquals(testId, response.id());
        assertEquals("OP001", response.operationReference());
        verify(flightOperationRepository, times(1)).findById(testId);
    }

    @Test
    void getFlightOperationById_WhenNotExists_ShouldThrowException() {
        when(flightOperationRepository.findById(testId)).thenReturn(java.util.Optional.empty());

        assertThrows(
                FlightOperationNotFoundException.class,
                () -> flightOperationService.getFlightOperationById(testId)
        );

        verify(flightOperationRepository, times(1)).findById(testId);
    }

    @Test
    void getAllFlightOperations_ShouldReturnList() {
        when(flightOperationRepository.findAll()).thenReturn(List.of(testFlightOperation));

        List<FlightOperationResponse> responses = flightOperationService.getAllFlightOperations();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("OP001", responses.get(0).operationReference());
        verify(flightOperationRepository, times(1)).findAll();
    }

    @Test
    void searchFlightOperations_WithoutParameters_ShouldReturnAll() {
        when(flightOperationRepository.findAll()).thenReturn(List.of(testFlightOperation));

        List<FlightOperationResponse> responses = flightOperationService.searchFlightOperations(null, null, null, null);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(flightOperationRepository, times(1)).findAll();
    }

    @Test
    void searchFlightOperations_ByFlightId_ShouldReturnMatching() {
        when(flightOperationRepository.findByFlightId(testFlightId)).thenReturn(List.of(testFlightOperation));

        List<FlightOperationResponse> responses = flightOperationService.searchFlightOperations(testFlightId, null, null, null);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(flightOperationRepository, times(1)).findByFlightId(testFlightId);
    }

    @Test
    void searchFlightOperations_ByOperationReference_ShouldReturnMatching() {
        when(flightOperationRepository.findByOperationReferenceContainingIgnoreCase("OP")).thenReturn(List.of(testFlightOperation));

        List<FlightOperationResponse> responses = flightOperationService.searchFlightOperations(null, "OP", null, null);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(flightOperationRepository, times(1)).findByOperationReferenceContainingIgnoreCase("OP");
    }

    @Test
    void searchFlightOperations_ByStatus_ShouldReturnMatching() {
        when(flightOperationRepository.findByStatus(OperationStatus.PLANNED)).thenReturn(List.of(testFlightOperation));

        List<FlightOperationResponse> responses = flightOperationService.searchFlightOperations(null, null, OperationStatus.PLANNED, null);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(flightOperationRepository, times(1)).findByStatus(OperationStatus.PLANNED);
    }

    @Test
    void searchFlightOperations_ByAirportCode_ShouldReturnMatching() {
        when(flightOperationRepository.findByAirportCode("JFK")).thenReturn(List.of(testFlightOperation));

        List<FlightOperationResponse> responses = flightOperationService.searchFlightOperations(null, null, null, "JFK");

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(flightOperationRepository, times(1)).findByAirportCode("JFK");
    }

    @Test
    void searchFlightOperations_ByFlightIdAndStatus_ShouldReturnMatching() {
        when(flightOperationRepository.findByFlightIdAndStatus(testFlightId, OperationStatus.PLANNED)).thenReturn(List.of(testFlightOperation));

        List<FlightOperationResponse> responses = flightOperationService.searchFlightOperations(testFlightId, null, OperationStatus.PLANNED, null);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(flightOperationRepository, times(1)).findByFlightIdAndStatus(testFlightId, OperationStatus.PLANNED);
    }

    @Test
    void updateFlightOperation_WhenExists_ShouldReturnUpdated() {
        UpdateFlightOperationRequest request = new UpdateFlightOperationRequest(
                OperationStatus.BOARDING,
                testScheduledAt,
                Instant.now(),
                "LAX",
                "Updated remarks"
        );

        when(flightOperationRepository.findById(testId)).thenReturn(java.util.Optional.of(testFlightOperation));
        when(flightOperationRepository.save(any(FlightOperation.class))).thenReturn(testFlightOperation);

        FlightOperationResponse response = flightOperationService.updateFlightOperation(testId, request);

        assertNotNull(response);
        verify(flightOperationRepository, times(1)).save(any(FlightOperation.class));
    }

    @Test
    void updateFlightOperation_WhenNotExists_ShouldThrowException() {
        UUID nonExistentId = UUID.randomUUID();
        UpdateFlightOperationRequest request = new UpdateFlightOperationRequest(
                OperationStatus.BOARDING,
                testScheduledAt,
                Instant.now(),
                "LAX",
                "Updated remarks"
        );

        when(flightOperationRepository.findById(nonExistentId)).thenReturn(java.util.Optional.empty());

        assertThrows(
                FlightOperationNotFoundException.class,
                () -> flightOperationService.updateFlightOperation(nonExistentId, request)
        );

        verify(flightOperationRepository, never()).save(any(FlightOperation.class));
    }

    @Test
    void deleteFlightOperation_WhenExists_ShouldDelete() {
        when(flightOperationRepository.existsById(testId)).thenReturn(true);
        doNothing().when(flightOperationRepository).deleteById(testId);

        flightOperationService.deleteFlightOperation(testId);

        verify(flightOperationRepository, times(1)).deleteById(testId);
    }

    @Test
    void deleteFlightOperation_WhenNotExists_ShouldThrowException() {
        UUID nonExistentId = UUID.randomUUID();
        when(flightOperationRepository.existsById(nonExistentId)).thenReturn(false);

        assertThrows(
                FlightOperationNotFoundException.class,
                () -> flightOperationService.deleteFlightOperation(nonExistentId)
        );

        verify(flightOperationRepository, never()).deleteById(nonExistentId);
    }
}
