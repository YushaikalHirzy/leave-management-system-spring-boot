package com.example.leavemanagementsystem.controller;

import com.example.leavemanagementsystem.dto.LeaveRequestDto;
import com.example.leavemanagementsystem.enums.LeaveStatus;
import com.example.leavemanagementsystem.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/leaves")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<LeaveRequestDto>> getAllLeaves() {
        List<LeaveRequestDto> leaves = leaveService.getAllLeaves();
        return ResponseEntity.ok(leaves);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER') or @userSecurity.isCurrentUserLeave(#id)")
    public ResponseEntity<LeaveRequestDto> getLeaveById(@PathVariable Long id) {
        LeaveRequestDto leave = leaveService.getLeaveById(id);
        return ResponseEntity.ok(leave);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER') or @userSecurity.isCurrentUser(#employeeId)")
    public ResponseEntity<List<LeaveRequestDto>> getLeavesByEmployee(@PathVariable Long employeeId) {
        List<LeaveRequestDto> leaves = leaveService.getLeavesByEmployee(employeeId);
        return ResponseEntity.ok(leaves);
    }

    @GetMapping("/manager/{managerId}/pending")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isCurrentUser(#managerId)")
    public ResponseEntity<List<LeaveRequestDto>> getPendingLeavesByManager(@PathVariable Long managerId) {
        List<LeaveRequestDto> leaves = leaveService.getPendingLeavesByManager(managerId);
        return ResponseEntity.ok(leaves);
    }

    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<LeaveRequestDto>> getLeavesByDepartment(
            @PathVariable Long departmentId,
            @RequestParam(required = false, defaultValue = "PENDING") LeaveStatus status) {
        List<LeaveRequestDto> leaves = leaveService.getLeavesByDepartment(departmentId, status);
        return ResponseEntity.ok(leaves);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<LeaveRequestDto> applyForLeave(@Valid @RequestBody LeaveRequestDto leaveRequestDto) {
        LeaveRequestDto leave = leaveService.applyForLeave(leaveRequestDto);
        return ResponseEntity.ok(leave);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isCurrentUserLeave(#id)")
    public ResponseEntity<LeaveRequestDto> updateLeaveRequest(@PathVariable Long id, @Valid @RequestBody LeaveRequestDto leaveRequestDto) {
        LeaveRequestDto leave = leaveService.updateLeaveRequest(id, leaveRequestDto);
        return ResponseEntity.ok(leave);
    }

    @PutMapping("/{id}/process")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<LeaveRequestDto> processLeaveRequest(
            @PathVariable Long id,
            @RequestParam LeaveStatus status,
            @RequestParam(required = false) String comment,
            @RequestParam Long approverId) {
        LeaveRequestDto leave = leaveService.processLeaveRequest(id, status, comment, approverId);
        return ResponseEntity.ok(leave);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isCurrentUserLeave(#id)")
    public ResponseEntity<?> deleteLeaveRequest(@PathVariable Long id) {
        leaveService.deleteLeaveRequest(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Map<String, Long>> getLeaveStatistics() {
        Map<String, Long> statistics = leaveService.getLeaveStatistics();
        return ResponseEntity.ok(statistics);
    }
}
