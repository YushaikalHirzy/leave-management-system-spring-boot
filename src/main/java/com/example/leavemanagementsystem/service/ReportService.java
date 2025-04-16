package com.example.leavemanagementsystem.service;

import com.example.leavemanagementsystem.dto.DepartmentDto;
import com.example.leavemanagementsystem.dto.EmployeeDto;
import com.example.leavemanagementsystem.enums.LeaveStatus;
import com.example.leavemanagementsystem.repository.DepartmentRepository;
import com.example.leavemanagementsystem.repository.EmployeeRepository;
import com.example.leavemanagementsystem.repository.LeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EmployeeService employeeService;

    public Map<String, Long> getEmployeeCountByDepartment() {
        Map<String, Long> employeeCount = new HashMap<>();
        List<DepartmentDto> departments = departmentService.getAllDepartments();

        for (DepartmentDto dept : departments) {
            employeeCount.put(dept.getName(), (long) dept.getEmployeeCount());
        }

        return employeeCount;
    }

    public Map<String, Object> getLeaveStatusReport() {
        Map<String, Object> report = new HashMap<>();
        List<Object[]> statusCounts = leaveRepository.countLeavesByStatus();

        // Total leave counts by status
        Map<String, Long> statusCountMap = new HashMap<>();
        for (Object[] result : statusCounts) {
            LeaveStatus status = (LeaveStatus) result[1];
            Long count = (Long) result[0];
            statusCountMap.put(status.name(), count);
        }
        report.put("leaveStatusCounts", statusCountMap);

        // Current month pending leaves
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        // Additional metrics could be added here

        return report;
    }

    public Map<String, List<EmployeeDto>> getCurrentlyOnLeaveEmployees() {
        Map<String, List<EmployeeDto>> result = new HashMap<>();
        List<DepartmentDto> departments = departmentService.getAllDepartments();
        LocalDate today = LocalDate.now();

        for (DepartmentDto dept : departments) {
            List<EmployeeDto> employeesOnLeave = employeeService.getEmployeesByDepartment(dept.getId())
                    .stream()
                    .filter(emp -> isEmployeeOnLeave(emp.getId(), today))
                    .collect(Collectors.toList());

            result.put(dept.getName(), employeesOnLeave);
        }

        return result;
    }

    private boolean isEmployeeOnLeave(Long employeeId, LocalDate date) {
        // Implementation would check if the employee has an approved leave that includes the given date
        // This is a simplified version
        return !leaveRepository.findOverlappingLeaves(employeeId, date, date).isEmpty();
    }
}
