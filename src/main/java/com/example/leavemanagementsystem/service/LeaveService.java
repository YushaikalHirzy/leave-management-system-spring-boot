package com.example.leavemanagementsystem.service;

import com.example.leavemanagementsystem.dto.LeaveRequestDto;
import com.example.leavemanagementsystem.model.Employee;
import com.example.leavemanagementsystem.model.Leave;
import com.example.leavemanagementsystem.enums.LeaveStatus;
import com.example.leavemanagementsystem.exception.ResourceNotFoundException;
import com.example.leavemanagementsystem.repository.EmployeeRepository;
import com.example.leavemanagementsystem.repository.LeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<LeaveRequestDto> getAllLeaves() {
        List<Leave> leaves = leaveRepository.findAll();
        return leaves.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public LeaveRequestDto getLeaveById(Long id) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));
        return convertToDto(leave);
    }

    public List<LeaveRequestDto> getLeavesByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        List<Leave> leaves = leaveRepository.findByEmployee(employee);
        return leaves.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<LeaveRequestDto> getPendingLeavesByManager(Long managerId) {
        List<Leave> leaves = leaveRepository.findByManagerAndStatus(managerId, LeaveStatus.PENDING);
        return leaves.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<LeaveRequestDto> getLeavesByDepartment(Long departmentId, LeaveStatus status) {
        List<Leave> leaves = leaveRepository.findByDepartmentAndStatus(departmentId, status);
        return leaves.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public LeaveRequestDto applyForLeave(LeaveRequestDto leaveRequestDto) {
        Employee employee = employeeRepository.findById(leaveRequestDto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + leaveRequestDto.getEmployeeId()));

        // Check for overlapping leaves
        List<Leave> overlappingLeaves = leaveRepository.findOverlappingLeaves(
                employee.getId(), leaveRequestDto.getStartDate(), leaveRequestDto.getEndDate());

        if (!overlappingLeaves.isEmpty()) {
            throw new RuntimeException("You already have a leave request for this period");
        }

        Leave leave = new Leave();
        leave.setEmployee(employee);
        leave.setStartDate(leaveRequestDto.getStartDate());
        leave.setEndDate(leaveRequestDto.getEndDate());
        leave.setLeaveType(leaveRequestDto.getLeaveType());
        leave.setReason(leaveRequestDto.getReason());
        leave.setStatus(LeaveStatus.PENDING);
        leave.setRequestDate(LocalDateTime.now());

        Leave savedLeave = leaveRepository.save(leave);
        return convertToDto(savedLeave);
    }

    public LeaveRequestDto updateLeaveRequest(Long id, LeaveRequestDto leaveRequestDto) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));

        // Only allow updates if the leave is still pending
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Can only update pending leave requests");
        }

        leave.setStartDate(leaveRequestDto.getStartDate());
        leave.setEndDate(leaveRequestDto.getEndDate());
        leave.setLeaveType(leaveRequestDto.getLeaveType());
        leave.setReason(leaveRequestDto.getReason());

        Leave updatedLeave = leaveRepository.save(leave);
        return convertToDto(updatedLeave);
    }

    public LeaveRequestDto processLeaveRequest(Long id, LeaveStatus status, String comment, Long approverId) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));

        // Only process pending leaves
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("This leave request has already been processed");
        }

        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("Approver not found with id: " + approverId));

        leave.setStatus(status);
        leave.setComment(comment);
        leave.setApprovedBy(approver);
        leave.setResponseDate(LocalDateTime.now());

        Leave processedLeave = leaveRepository.save(leave);
        return convertToDto(processedLeave);
    }

    public void deleteLeaveRequest(Long id) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));

        // Only allow deletion of pending leaves
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Can only delete pending leave requests");
        }

        leaveRepository.delete(leave);
    }

    public Map<String, Long> getLeaveStatistics() {
        Map<String, Long> statistics = new HashMap<>();

        // Get counts by status
        List<Object[]> statusCounts = leaveRepository.countLeavesByStatus();
        for (Object[] result : statusCounts) {
            LeaveStatus status = (LeaveStatus) result[1];
            Long count = (Long) result[0];
            statistics.put(status.name(), count);
        }

        // Get counts by type
        List<Object[]> typeCounts = leaveRepository.countLeavesByType();
        for (Object[] result : typeCounts) {
            String leaveType = ((Enum<?>) result[1]).name();
            Long count = (Long) result[0];
            statistics.put(leaveType, count);
        }

        return statistics;
    }

    private LeaveRequestDto convertToDto(Leave leave) {
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setId(leave.getId());
        dto.setEmployeeId(leave.getEmployee().getId());
        dto.setEmployeeName(leave.getEmployee().getFirstName() + " " + leave.getEmployee().getLastName());
        dto.setStartDate(leave.getStartDate());
        dto.setEndDate(leave.getEndDate());
        dto.setLeaveType(leave.getLeaveType());
        dto.setReason(leave.getReason());
        dto.setStatus(leave.getStatus());
        dto.setComment(leave.getComment());
        dto.setRequestDate(leave.getRequestDate());
        dto.setResponseDate(leave.getResponseDate());

        if (leave.getApprovedBy() != null) {
            dto.setApprovedById(leave.getApprovedBy().getId());
            dto.setApprovedByName(leave.getApprovedBy().getFirstName() + " " + leave.getApprovedBy().getLastName());
        }

        return dto;
    }
}
