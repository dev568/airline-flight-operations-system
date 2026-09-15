package com.airline.crew.controller;

import com.airline.crew.CrewServiceApplication;
import com.airline.crew.dto.CreateCrewMemberRequest;
import com.airline.crew.dto.CrewMemberResponse;
import com.airline.crew.dto.UpdateCrewMemberRequest;
import com.airline.crew.exception.CrewMemberNotFoundException;
import com.airline.crew.model.CrewRole;
import com.airline.crew.model.CrewStatus;
import com.airline.crew.service.CrewMemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CrewMemberController.class)
class CrewMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CrewMemberService crewMemberService;

    private CrewMemberResponse testCrewMemberResponse;
    private UUID testId;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testDate = LocalDate.of(2020, 1, 1);
        testCrewMemberResponse = new CrewMemberResponse(
                testId,
                "EMP001",
                "John",
                "Doe",
                "john.doe@example.com",
                CrewRole.PILOT,
                CrewStatus.ACTIVE,
                "JFK",
                testDate,
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    void createCrewMember_WithValidRequest_ShouldReturn201() throws Exception {
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

        when(crewMemberService.createCrewMember(any(CreateCrewMemberRequest.class))).thenReturn(testCrewMemberResponse);

        mockMvc.perform(post("/api/v1/crew-members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.employeeId").value("EMP001"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.role").value("PILOT"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(crewMemberService, times(1)).createCrewMember(any(CreateCrewMemberRequest.class));
    }

    @Test
    void createCrewMember_WithInvalidEmployeeId_ShouldReturn400() throws Exception {
        CreateCrewMemberRequest request = new CreateCrewMemberRequest(
                "", // Empty employee ID
                "John",
                "Doe",
                "john.doe@example.com",
                CrewRole.PILOT,
                CrewStatus.ACTIVE,
                "JFK",
                testDate
        );

        mockMvc.perform(post("/api/v1/crew-members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(crewMemberService, never()).createCrewMember(any(CreateCrewMemberRequest.class));
    }

    @Test
    void createCrewMember_WithInvalidEmail_ShouldReturn400() throws Exception {
        CreateCrewMemberRequest request = new CreateCrewMemberRequest(
                "EMP001",
                "John",
                "Doe",
                "invalid-email", // Invalid email
                CrewRole.PILOT,
                CrewStatus.ACTIVE,
                "JFK",
                testDate
        );

        mockMvc.perform(post("/api/v1/crew-members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(crewMemberService, never()).createCrewMember(any(CreateCrewMemberRequest.class));
    }

    @Test
    void createCrewMember_WithInvalidAirportCode_ShouldReturn400() throws Exception {
        CreateCrewMemberRequest request = new CreateCrewMemberRequest(
                "EMP001",
                "John",
                "Doe",
                "john.doe@example.com",
                CrewRole.PILOT,
                CrewStatus.ACTIVE,
                "INVALID", // Invalid airport code
                testDate
        );

        mockMvc.perform(post("/api/v1/crew-members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(crewMemberService, never()).createCrewMember(any(CreateCrewMemberRequest.class));
    }

    @Test
    void createCrewMember_WithFutureHireDate_ShouldReturn400() throws Exception {
        CreateCrewMemberRequest request = new CreateCrewMemberRequest(
                "EMP001",
                "John",
                "Doe",
                "john.doe@example.com",
                CrewRole.PILOT,
                CrewStatus.ACTIVE,
                "JFK",
                LocalDate.now().plusDays(1) // Future date
        );

        mockMvc.perform(post("/api/v1/crew-members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(crewMemberService, never()).createCrewMember(any(CreateCrewMemberRequest.class));
    }

    @Test
    void createCrewMember_WithMissingRequiredField_ShouldReturn400() throws Exception {
        CreateCrewMemberRequest request = new CreateCrewMemberRequest(
                null, // Missing employee ID
                "John",
                "Doe",
                "john.doe@example.com",
                CrewRole.PILOT,
                CrewStatus.ACTIVE,
                "JFK",
                testDate
        );

        mockMvc.perform(post("/api/v1/crew-members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(crewMemberService, never()).createCrewMember(any(CreateCrewMemberRequest.class));
    }

    @Test
    void getCrewMemberById_WhenExists_ShouldReturn200() throws Exception {
        when(crewMemberService.getCrewMemberById(testId)).thenReturn(testCrewMemberResponse);

        mockMvc.perform(get("/api/v1/crew-members/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.employeeId").value("EMP001"));

        verify(crewMemberService, times(1)).getCrewMemberById(testId);
    }

    @Test
    void getCrewMemberById_WhenNotExists_ShouldReturn404() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        when(crewMemberService.getCrewMemberById(nonExistentId))
                .thenThrow(new CrewMemberNotFoundException(nonExistentId));

        mockMvc.perform(get("/api/v1/crew-members/{id}", nonExistentId))
                .andExpect(status().isNotFound());

        verify(crewMemberService, times(1)).getCrewMemberById(nonExistentId);
    }

    @Test
    void getCrewMemberById_WithInvalidUuid_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/v1/crew-members/invalid-uuid"))
                .andExpect(status().isBadRequest());

        verify(crewMemberService, never()).getCrewMemberById(any());
    }

    @Test
    void getAllCrewMembers_ShouldReturn200() throws Exception {
        when(crewMemberService.getAllCrewMembers()).thenReturn(List.of(testCrewMemberResponse));

        mockMvc.perform(get("/api/v1/crew-members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testId.toString()))
                .andExpect(jsonPath("$[0].employeeId").value("EMP001"));

        verify(crewMemberService, times(1)).getAllCrewMembers();
    }

    @Test
    void searchCrewMembers_WithoutParameters_ShouldReturn200() throws Exception {
        when(crewMemberService.searchCrewMembers(isNull(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(List.of(testCrewMemberResponse));

        mockMvc.perform(get("/api/v1/crew-members/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeId").value("EMP001"));

        verify(crewMemberService, times(1)).searchCrewMembers(isNull(), isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void searchCrewMembers_ByEmployeeId_ShouldReturn200() throws Exception {
        when(crewMemberService.searchCrewMembers(eq("EMP"), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(List.of(testCrewMemberResponse));

        mockMvc.perform(get("/api/v1/crew-members/search")
                        .param("employeeId", "EMP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeId").value("EMP001"));

        verify(crewMemberService, times(1)).searchCrewMembers(eq("EMP"), isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void searchCrewMembers_ByEmail_ShouldReturn200() throws Exception {
        when(crewMemberService.searchCrewMembers(isNull(), eq("john"), isNull(), isNull(), isNull()))
                .thenReturn(List.of(testCrewMemberResponse));

        mockMvc.perform(get("/api/v1/crew-members/search")
                        .param("email", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));

        verify(crewMemberService, times(1)).searchCrewMembers(isNull(), eq("john"), isNull(), isNull(), isNull());
    }

    @Test
    void searchCrewMembers_ByRole_ShouldReturn200() throws Exception {
        when(crewMemberService.searchCrewMembers(isNull(), isNull(), eq(CrewRole.PILOT), isNull(), isNull()))
                .thenReturn(List.of(testCrewMemberResponse));

        mockMvc.perform(get("/api/v1/crew-members/search")
                        .param("role", "PILOT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role").value("PILOT"));

        verify(crewMemberService, times(1)).searchCrewMembers(isNull(), isNull(), eq(CrewRole.PILOT), isNull(), isNull());
    }

    @Test
    void searchCrewMembers_ByStatus_ShouldReturn200() throws Exception {
        when(crewMemberService.searchCrewMembers(isNull(), isNull(), isNull(), eq(CrewStatus.ACTIVE), isNull()))
                .thenReturn(List.of(testCrewMemberResponse));

        mockMvc.perform(get("/api/v1/crew-members/search")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(crewMemberService, times(1)).searchCrewMembers(isNull(), isNull(), isNull(), eq(CrewStatus.ACTIVE), isNull());
    }

    @Test
    void searchCrewMembers_ByBaseAirport_ShouldReturn200() throws Exception {
        when(crewMemberService.searchCrewMembers(isNull(), isNull(), isNull(), isNull(), eq("JFK")))
                .thenReturn(List.of(testCrewMemberResponse));

        mockMvc.perform(get("/api/v1/crew-members/search")
                        .param("baseAirport", "JFK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].baseAirport").value("JFK"));

        verify(crewMemberService, times(1)).searchCrewMembers(isNull(), isNull(), isNull(), isNull(), eq("JFK"));
    }

    @Test
    void updateCrewMember_WhenExists_ShouldReturn200() throws Exception {
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

        when(crewMemberService.updateCrewMember(eq(testId), any(UpdateCrewMemberRequest.class)))
                .thenReturn(testCrewMemberResponse);

        mockMvc.perform(put("/api/v1/crew-members/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(crewMemberService, times(1)).updateCrewMember(eq(testId), any(UpdateCrewMemberRequest.class));
    }

    @Test
    void updateCrewMember_WhenNotExists_ShouldReturn404() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
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

        when(crewMemberService.updateCrewMember(eq(nonExistentId), any(UpdateCrewMemberRequest.class)))
                .thenThrow(new CrewMemberNotFoundException(nonExistentId));

        mockMvc.perform(put("/api/v1/crew-members/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(crewMemberService, times(1)).updateCrewMember(eq(nonExistentId), any(UpdateCrewMemberRequest.class));
    }

    @Test
    void deleteCrewMember_WhenExists_ShouldReturn204() throws Exception {
        doNothing().when(crewMemberService).deleteCrewMember(testId);

        mockMvc.perform(delete("/api/v1/crew-members/{id}", testId))
                .andExpect(status().isNoContent());

        verify(crewMemberService, times(1)).deleteCrewMember(testId);
    }

    @Test
    void deleteCrewMember_WhenNotExists_ShouldReturn404() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        doThrow(new CrewMemberNotFoundException(nonExistentId))
                .when(crewMemberService).deleteCrewMember(nonExistentId);

        mockMvc.perform(delete("/api/v1/crew-members/{id}", nonExistentId))
                .andExpect(status().isNotFound());

        verify(crewMemberService, times(1)).deleteCrewMember(nonExistentId);
    }

    @Test
    void correlationId_WithHeader_ShouldPreserveValue() throws Exception {
        String correlationId = "550e8400-e29b-41d4-a716-446655440000";
        when(crewMemberService.getCrewMemberById(testId)).thenReturn(testCrewMemberResponse);

        mockMvc.perform(get("/api/v1/crew-members/{id}", testId)
                        .header("X-Correlation-ID", correlationId))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Correlation-ID"))
                .andExpect(header().string("X-Correlation-ID", correlationId));
    }

    @Test
    void correlationId_WithoutHeader_ShouldGenerateValue() throws Exception {
        when(crewMemberService.getCrewMemberById(testId)).thenReturn(testCrewMemberResponse);

        mockMvc.perform(get("/api/v1/crew-members/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Correlation-ID"));
    }

    @Test
    void correlationId_WithErrorResponse_ShouldContainCorrelationId() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        String correlationId = "550e8400-e29b-41d4-a716-446655440000";
        when(crewMemberService.getCrewMemberById(nonExistentId))
                .thenThrow(new CrewMemberNotFoundException(nonExistentId));

        mockMvc.perform(get("/api/v1/crew-members/{id}", nonExistentId)
                        .header("X-Correlation-ID", correlationId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.correlationId").value(correlationId));
    }
}
