package com.example.leavemanagementsystem.repository;

import com.example.leavemanagementsystem.model.Employee;
import com.example.leavemanagementsystem.model.Leave;
import com.example.leavemanagementsystem.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findByEmployee(Employee employee);

    List<Leave> findByEmployeeAndStatus(Employee employee, LeaveStatus status);

    @Query("SELECT l FROM Leave l WHERE l.employee.department.id = :departmentId AND l.status = :status")
    List<Leave> findByDepartmentAndStatus(Long departmentId, LeaveStatus status);

    @Query("SELECT l FROM Leave l WHERE l.employee.manager.id = :managerId AND l.status = :status")
    List<Leave> findByManagerAndStatus(Long managerId, LeaveStatus status);

    @Query("SELECT COUNT(l), l.status FROM Leave l GROUP BY l.status")
    List<Object[]> countLeavesByStatus();

    @Query("SELECT COUNT(l), l.leaveType FROM Leave l GROUP BY l.leaveType")
    List<Object[]> countLeavesByType();

    @Query("SELECT l FROM Leave l WHERE l.startDate <= :endDate AND l.endDate >= :startDate AND l.employee.id = :employeeId")
    List<Leave> findOverlappingLeaves(Long employeeId, LocalDate startDate, LocalDate endDate);
}
