package com.example.leavemanagementsystem.repository;

import com.example.leavemanagementsystem.model.Department;
import com.example.leavemanagementsystem.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.department")
    List<Employee> findAllWithDepartment();
    Optional<Employee> findByUserId(Long userId);
    List<Employee> findByDepartment(Department department);
    List<Employee> findByManager(Employee manager);

    // Add this method to count employees by department ID
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department.id = :departmentId")
    Integer countByDepartmentId(@Param("departmentId") Long departmentId);
}