package com.airline.crew.service;

import com.airline.crew.dto.CreateCrewMemberRequest;
import com.airline.crew.dto.CrewMemberResponse;
import com.airline.crew.dto.UpdateCrewMemberRequest;
import com.airline.crew.entity.CrewMember;
import com.airline.crew.exception.CrewMemberNotFoundException;
import com.airline.crew.model.CrewRole;
import com.airline.crew.model.CrewStatus;
import com.airline.crew.repository.CrewMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrewMemberServiceTest {

    @Mock
    private CrewMemberRepository crewMemberRepository;

    @InjectMocks
    private CrewMemberService crewMemberService;

    private CrewMember testCrewMember;
    private UUID testId;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testDate = LocalDate.of(2020, 1, 1);
        testCrewMember = new CrewMember();
        testCrewMember.setId(testId);
        testCrewMember.setEmployeeId("EMP001");
        testCrewMember.setFirstName("John");
        testCrewMember.setLastName("Doe");
        testCrewMember.setEmail("john.doe@example.com");
        testCrewMember.setRole(CrewRole.PILOT);
        testCrewMember.setStatus(CrewStatus.ACTIVE);
        testCrewMember.setBaseAirport("JFK");
        testCrewMember.setHireDate(testDate);
        testCrewMember.setCreatedAt(Instant.now());
        testCrewMember.setUpdatedAt(Instant.now());
    }

    @Test
    void createCrewMember_WithValidRequest_ShouldReturnResponse() {
        CreateCrewMemberRequest request = new CreateCrewMemberRequest(
                "EMP001",
                "John",
                "Doe",
                "john.doe@example.com",
                CrewRole.PILOT,
                CrewStatus.ACTIVE,
                "JFK",
                testDate
        );

        when(crewMemberRepository.existsByEmployeeId("EMP001")).thenReturn(false);
        when(crewMemberRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(crewMemberRepository.save(any(CrewMember.class))).thenReturn(testCrewMember);

        CrewMemberResponse response = crewMemberService.createCrewMember(request);

        assertNotNull(response);
        assertEquals("EMP001", response.employeeId());
        assertEquals("John", response.firstName());
        verify(crewMemberRepository, times(1)).save(any(CrewMember.class));
    }

    @Test
    void createCrewMember_WithDuplicateEmployeeId_ShouldThrowException() {
        CreateCrewMemberRequest request = new CreateCrewMemberRequest(
                "EMP001",
                "John",
                "Doe",
                "john.doe@example.com",
                CrewRole.PILOT,
                CrewStatus.ACTIVE,
                "JFK",
                testDate
        );

        when(crewMemberRepository.existsByEmployeeId("EMP001")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> crewMemberService.createCrewMember(request)
        );

        assertTrue(exception.getMessage().contains("Employee ID already exists"));
        verify(crewMemberRepository, never()).save(any(CrewMember.class));
    }

    @Test
    void createCrewMember_WithDuplicateEmail_ShouldThrowException() {
        CreateCrewMemberRequest request = new CreateCrewMemberRequest(
                "EMP001",
                "John",
                "Doe",
                "john.doe@example.com",
                CrewRole.PILOT,
                CrewStatus.ACTIVE,
                "JFK",
                testDate
        );

        when(crewMemberRepository.existsByEmployeeId("EMP001")).thenReturn(false);
        when(crewMemberRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> crewMemberService.createCrewMember(request)
        );

        assertTrue(exception.getMessage().contains("Email already exists"));
        verify(crewMemberRepository, never()).save(any(CrewMember.class));
    }

    @Test
    void getCrewMemberById_WhenExists_ShouldReturnResponse() {
        when(crewMemberRepository.findById(testId)).thenReturn(Optional.of(testCrewMember));

        CrewMemberResponse response = crewMemberService.getCrewMemberById(testId);

        assertNotNull(response);
        assertEquals(testId, response.id());
        assertEquals("EMP001", response.employeeId());
        verify(crewMemberRepository, times(1)).findById(testId);
    }

    @Test
    void getCrewMemberById_WhenNotExists_ShouldThrowException() {
        when(crewMemberRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(
                CrewMemberNotFoundException.class,
                () -> crewMemberService.getCrewMemberById(testId)
        );

        verify(crewMemberRepository, times(1)).findById(testId);
    }

    @Test
    void getAllCrewMembers_ShouldReturnList() {
        when(crewMemberRepository.findAll()).thenReturn(List.of(testCrewMember));

        List<CrewMemberResponse> responses = crewMemberService.getAllCrewMembers();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("EMP001", responses.get(0).employeeId());
        verify(crewMemberRepository, times(1)).findAll();
    }

    @Test
    void searchCrewMembers_WithoutParameters_ShouldReturnAll() {
        when(crewMemberRepository.findAll()).thenReturn(List.of(testCrewMember));

        List<CrewMemberResponse> responses = crewMemberService.searchCrewMembers(null, null, null, null, null);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(crewMemberRepository, times(1)).findAll();
    }

    @Test
    void searchCrewMembers_ByEmployeeId_ShouldReturnMatching() {
        when(crewMemberRepository.findByEmployeeIdContainingIgnoreCase("EMP")).thenReturn(List.of(testCrewMember));

        List<CrewMemberResponse> responses = crewMemberService.searchCrewMembers("EMP", null, null, null, null);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(crewMemberRepository, times(1)).findByEmployeeIdContainingIgnoreCase("EMP");
    }

    @Test
    void searchCrewMembers_ByRole_ShouldReturnMatching() {
        when(crewMemberRepository.findByRole(CrewRole.PILOT)).thenReturn(List.of(testCrewMember));

        List<CrewMemberResponse> responses = crewMemberService.searchCrewMembers(null, null, CrewRole.PILOT, null, null);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(crewMemberRepository, times(1)).findByRole(CrewRole.PILOT);
    }

    @Test
    void searchCrewMembers_ByStatus_ShouldReturnMatching() {
        when(crewMemberRepository.findByStatus(CrewStatus.ACTIVE)).thenReturn(List.of(testCrewMember));

        List<CrewMemberResponse> responses = crewMemberService.searchCrewMembers(null, null, null, CrewStatus.ACTIVE, null);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(crewMemberRepository, times(1)).findByStatus(CrewStatus.ACTIVE);
    }

    @Test
    void searchCrewMembers_ByBaseAirport_ShouldReturnMatching() {
        when(crewMemberRepository.findByBaseAirport("JFK")).thenReturn(List.of(testCrewMember));

        List<CrewMemberResponse> responses = crewMemberService.searchCrewMembers(null, null, null, null, "JFK");

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(crewMemberRepository, times(1)).findByBaseAirport("JFK");
    }

    @Test
    void updateCrewMember_WhenExists_ShouldReturnUpdated() {
        UpdateCrewMemberRequest request = new UpdateCrewMemberRequest(
                "EMP002",
                "Jane",
                "Smith",
                "jane.smith@example.com",
                CrewRole.COPILOT,
                CrewStatus.ACTIVE,
                "LAX",
                testDate
        );

        when(crewMemberRepository.findById(testId)).thenReturn(Optional.of(testCrewMember));
        when(crewMemberRepository.existsByEmployeeId("EMP002")).thenReturn(false);
        when(crewMemberRepository.existsByEmail("jane.smith@example.com")).thenReturn(false);
        when(crewMemberRepository.save(any(CrewMember.class))).thenReturn(testCrewMember);

        CrewMemberResponse response = crewMemberService.updateCrewMember(testId, request);

        assertNotNull(response);
        verify(crewMemberRepository, times(1)).save(any(CrewMember.class));
    }

    @Test
    void updateCrewMember_WhenNotExists_ShouldThrowException() {
        UpdateCrewMemberRequest request = new UpdateCrewMemberRequest(
                "EMP002",
                "Jane",
                "Smith",
                "jane.smith@example.com",
                CrewRole.COPILOT,
                CrewStatus.ACTIVE,
                "LAX",
                testDate
        );

        when(crewMemberRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(
                CrewMemberNotFoundException.class,
                () -> crewMemberService.updateCrewMember(testId, request)
        );

        verify(crewMemberRepository, never()).save(any(CrewMember.class));
    }

    @Test
    void updateCrewMember_WithDuplicateEmployeeId_ShouldThrowException() {
        UpdateCrewMemberRequest request = new UpdateCrewMemberRequest(
                "EMP002",
                "Jane",
                "Smith",
                "jane.smith@example.com",
                CrewRole.COPILOT,
                CrewStatus.ACTIVE,
                "LAX",
                testDate
        );

        when(crewMemberRepository.findById(testId)).thenReturn(Optional.of(testCrewMember));
        when(crewMemberRepository.existsByEmployeeId("EMP002")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> crewMemberService.updateCrewMember(testId, request)
        );

        assertTrue(exception.getMessage().contains("Employee ID already exists"));
        verify(crewMemberRepository, never()).save(any(CrewMember.class));
    }

    @Test
    void deleteCrewMember_WhenExists_ShouldDelete() {
        when(crewMemberRepository.existsById(testId)).thenReturn(true);
        doNothing().when(crewMemberRepository).deleteById(testId);

        crewMemberService.deleteCrewMember(testId);

        verify(crewMemberRepository, times(1)).deleteById(testId);
    }

    @Test
    void deleteCrewMember_WhenNotExists_ShouldThrowException() {
        when(crewMemberRepository.existsById(testId)).thenReturn(false);

        assertThrows(
                CrewMemberNotFoundException.class,
                () -> crewMemberService.deleteCrewMember(testId)
        );

        verify(crewMemberRepository, never()).deleteById(testId);
    }
}
