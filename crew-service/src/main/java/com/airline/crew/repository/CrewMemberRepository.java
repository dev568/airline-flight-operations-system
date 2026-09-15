package com.airline.crew.repository;

import com.airline.crew.entity.CrewMember;
import com.airline.crew.model.CrewRole;
import com.airline.crew.model.CrewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CrewMemberRepository extends JpaRepository<CrewMember, UUID> {

    Optional<CrewMember> findByEmployeeId(String employeeId);

    Optional<CrewMember> findByEmail(String email);

    boolean existsByEmployeeId(String employeeId);

    boolean existsByEmail(String email);

    List<CrewMember> findByEmployeeIdContainingIgnoreCase(String employeeId);

    List<CrewMember> findByRole(CrewRole role);

    List<CrewMember> findByStatus(CrewStatus status);

    List<CrewMember> findByBaseAirport(String baseAirport);

    List<CrewMember> findByEmployeeIdContainingIgnoreCaseAndRole(String employeeId, CrewRole role);

    List<CrewMember> findByEmployeeIdContainingIgnoreCaseAndStatus(String employeeId, CrewStatus status);

    List<CrewMember> findByEmployeeIdContainingIgnoreCaseAndBaseAirport(String employeeId, String baseAirport);

    List<CrewMember> findByEmployeeIdContainingIgnoreCaseAndRoleAndStatusAndBaseAirport(String employeeId, CrewRole role, CrewStatus status, String baseAirport);

    List<CrewMember> findByEmployeeIdContainingIgnoreCaseAndRoleAndStatus(String employeeId, CrewRole role, CrewStatus status);

    List<CrewMember> findByEmployeeIdContainingIgnoreCaseAndRoleAndBaseAirport(String employeeId, CrewRole role, String baseAirport);

    List<CrewMember> findByEmployeeIdContainingIgnoreCaseAndStatusAndBaseAirport(String employeeId, CrewStatus status, String baseAirport);

    List<CrewMember> findByRoleAndStatus(CrewRole role, CrewStatus status);

    List<CrewMember> findByRoleAndBaseAirport(CrewRole role, String baseAirport);

    List<CrewMember> findByStatusAndBaseAirport(CrewStatus status, String baseAirport);

    List<CrewMember> findByRoleAndStatusAndBaseAirport(CrewRole role, CrewStatus status, String baseAirport);
}
