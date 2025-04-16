package com.example.leavemanagementsystem.controller;

import com.example.leavemanagementsystem.dto.EmployeeDto;
import com.example.leavemanagementsystem.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/employee-by-department")
    public ResponseEntity<Map<String, Long>> getEmployeeCountByDepartment() {
        Map<String, Long> report = reportService.getEmployeeCountByDepartment();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/leave-status")
    public ResponseEntity<Map<String, Object>> getLeaveStatusReport() {
        Map<String, Object> report = reportService.getLeaveStatusReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/currently-on-leave")
    public ResponseEntity<Map<String, List<EmployeeDto>>> getCurrentlyOnLeaveEmployees() {
        Map<String, List<EmployeeDto>> report = reportService.getCurrentlyOnLeaveEmployees();
        return ResponseEntity.ok(report);
    }
}
