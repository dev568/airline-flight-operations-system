package com.airline.crew.service;

import com.airline.crew.dto.CreateCrewMemberRequest;
import com.airline.crew.dto.CrewMemberResponse;
import com.airline.crew.dto.UpdateCrewMemberRequest;
import com.airline.crew.entity.CrewMember;
import com.airline.crew.exception.CrewMemberNotFoundException;
import com.airline.crew.model.CrewRole;
import com.airline.crew.model.CrewStatus;
import com.airline.crew.repository.CrewMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CrewMemberService {

    private final CrewMemberRepository crewMemberRepository;

    public CrewMemberService(CrewMemberRepository crewMemberRepository) {
        this.crewMemberRepository = crewMemberRepository;
    }

    public CrewMemberResponse createCrewMember(CreateCrewMemberRequest request) {
        if (crewMemberRepository.existsByEmployeeId(request.employeeId())) {
            throw new IllegalArgumentException("Employee ID already exists: " + request.employeeId());
        }
        if (crewMemberRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists: " + request.email());
        }

        CrewMember crewMember = new CrewMember();
        crewMember.setEmployeeId(request.employeeId());
        crewMember.setFirstName(request.firstName());
        crewMember.setLastName(request.lastName());
        crewMember.setEmail(request.email());
        crewMember.setRole(request.role());
        crewMember.setStatus(request.status());
        crewMember.setBaseAirport(request.baseAirport());
        crewMember.setHireDate(request.hireDate());

        CrewMember saved = crewMemberRepository.save(crewMember);
        return mapToResponse(saved);
    }

    public CrewMemberResponse getCrewMemberById(UUID id) {
        CrewMember crewMember = crewMemberRepository.findById(id)
                .orElseThrow(() -> new CrewMemberNotFoundException(id));
        return mapToResponse(crewMember);
    }

    public List<CrewMemberResponse> getAllCrewMembers() {
        return crewMemberRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<CrewMemberResponse> searchCrewMembers(String employeeId, String email, CrewRole role, CrewStatus status, String baseAirport) {
        List<CrewMember> results;

        if (employeeId != null && !employeeId.isBlank()) {
            if (role != null && status != null && baseAirport != null) {
                results = crewMemberRepository.findByEmployeeIdContainingIgnoreCaseAndRoleAndStatusAndBaseAirport(employeeId, role, status, baseAirport);
            } else if (role != null && status != null) {
                results = crewMemberRepository.findByEmployeeIdContainingIgnoreCaseAndRoleAndStatus(employeeId, role, status);
            } else if (role != null && baseAirport != null) {
                results = crewMemberRepository.findByEmployeeIdContainingIgnoreCaseAndRoleAndBaseAirport(employeeId, role, baseAirport);
            } else if (status != null && baseAirport != null) {
                results = crewMemberRepository.findByEmployeeIdContainingIgnoreCaseAndStatusAndBaseAirport(employeeId, status, baseAirport);
            } else if (role != null) {
                results = crewMemberRepository.findByEmployeeIdContainingIgnoreCaseAndRole(employeeId, role);
            } else if (status != null) {
                results = crewMemberRepository.findByEmployeeIdContainingIgnoreCaseAndStatus(employeeId, status);
            } else if (baseAirport != null) {
                results = crewMemberRepository.findByEmployeeIdContainingIgnoreCaseAndBaseAirport(employeeId, baseAirport);
            } else {
                results = crewMemberRepository.findByEmployeeIdContainingIgnoreCase(employeeId);
            }
        } else if (email != null && !email.isBlank()) {
            results = crewMemberRepository.findAll().stream()
                    .filter(cm -> cm.getEmail().toLowerCase().contains(email.toLowerCase()))
                    .toList();
        } else if (role != null && status != null && baseAirport != null) {
            results = crewMemberRepository.findByRoleAndStatusAndBaseAirport(role, status, baseAirport);
        } else if (role != null && status != null) {
            results = crewMemberRepository.findByRoleAndStatus(role, status);
        } else if (role != null && baseAirport != null) {
            results = crewMemberRepository.findByRoleAndBaseAirport(role, baseAirport);
        } else if (status != null && baseAirport != null) {
            results = crewMemberRepository.findByStatusAndBaseAirport(status, baseAirport);
        } else if (role != null) {
            results = crewMemberRepository.findByRole(role);
        } else if (status != null) {
            results = crewMemberRepository.findByStatus(status);
        } else if (baseAirport != null) {
            results = crewMemberRepository.findByBaseAirport(baseAirport);
        } else {
            results = crewMemberRepository.findAll();
        }

        return results.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CrewMemberResponse updateCrewMember(UUID id, UpdateCrewMemberRequest request) {
        CrewMember crewMember = crewMemberRepository.findById(id)
                .orElseThrow(() -> new CrewMemberNotFoundException(id));

        if (request.employeeId() != null && !request.employeeId().equals(crewMember.getEmployeeId())) {
            if (crewMemberRepository.existsByEmployeeId(request.employeeId())) {
                throw new IllegalArgumentException("Employee ID already exists: " + request.employeeId());
            }
            crewMember.setEmployeeId(request.employeeId());
        }

        if (request.email() != null && !request.email().equals(crewMember.getEmail())) {
            if (crewMemberRepository.existsByEmail(request.email())) {
                throw new IllegalArgumentException("Email already exists: " + request.email());
            }
            crewMember.setEmail(request.email());
        }

        if (request.firstName() != null) {
            crewMember.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            crewMember.setLastName(request.lastName());
        }
        if (request.role() != null) {
            crewMember.setRole(request.role());
        }
        if (request.status() != null) {
            crewMember.setStatus(request.status());
        }
        if (request.baseAirport() != null) {
            crewMember.setBaseAirport(request.baseAirport());
        }
        if (request.hireDate() != null) {
            crewMember.setHireDate(request.hireDate());
        }

        CrewMember updated = crewMemberRepository.save(crewMember);
        return mapToResponse(updated);
    }

    public void deleteCrewMember(UUID id) {
        if (!crewMemberRepository.existsById(id)) {
            throw new CrewMemberNotFoundException(id);
        }
        crewMemberRepository.deleteById(id);
    }

    private CrewMemberResponse mapToResponse(CrewMember crewMember) {
        return new CrewMemberResponse(
                crewMember.getId(),
                crewMember.getEmployeeId(),
                crewMember.getFirstName(),
                crewMember.getLastName(),
                crewMember.getEmail(),
                crewMember.getRole(),
                crewMember.getStatus(),
                crewMember.getBaseAirport(),
                crewMember.getHireDate(),
                crewMember.getCreatedAt(),
                crewMember.getUpdatedAt()
        );
    }
}
