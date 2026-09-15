package com.airline.crew.controller;

import com.airline.crew.dto.CreateCrewMemberRequest;
import com.airline.crew.dto.CrewMemberResponse;
import com.airline.crew.dto.UpdateCrewMemberRequest;
import com.airline.crew.model.CrewRole;
import com.airline.crew.model.CrewStatus;
import com.airline.crew.service.CrewMemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/crew-members")
public class CrewMemberController {

    private final CrewMemberService crewMemberService;

    public CrewMemberController(CrewMemberService crewMemberService) {
        this.crewMemberService = crewMemberService;
    }

    @PostMapping
    public ResponseEntity<CrewMemberResponse> createCrewMember(@Valid @RequestBody CreateCrewMemberRequest request) {
        CrewMemberResponse response = crewMemberService.createCrewMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CrewMemberResponse> getCrewMemberById(@PathVariable UUID id) {
        CrewMemberResponse response = crewMemberService.getCrewMemberById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CrewMemberResponse>> getAllCrewMembers() {
        List<CrewMemberResponse> responses = crewMemberService.getAllCrewMembers();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CrewMemberResponse>> searchCrewMembers(
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) CrewRole role,
            @RequestParam(required = false) CrewStatus status,
            @RequestParam(required = false) String baseAirport) {
        List<CrewMemberResponse> responses = crewMemberService.searchCrewMembers(
                employeeId, email, role, status, baseAirport);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CrewMemberResponse> updateCrewMember(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCrewMemberRequest request) {
        CrewMemberResponse response = crewMemberService.updateCrewMember(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCrewMember(@PathVariable UUID id) {
        crewMemberService.deleteCrewMember(id);
        return ResponseEntity.noContent().build();
    }
}
