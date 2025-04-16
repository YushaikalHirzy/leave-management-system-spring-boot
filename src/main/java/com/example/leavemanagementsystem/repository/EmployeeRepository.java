package com.example.leavemanagementsystem.repository;

import com.example.leavemanagementsystem.model.Department;
import com.example.leavemanagementsystem.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByDepartment(Department department);

    List<Employee> findByManager(Employee manager);

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByUserId(Long userId);

    Long countByDepartment(Department department);

    @Query("SELECT e FROM Employee e JOIN FETCH e.department")
    List<Employee> findAllWithDepartment();
}