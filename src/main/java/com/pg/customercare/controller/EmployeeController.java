package com.pg.customercare.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pg.customercare.model.Employee;
import com.pg.customercare.service.EmployeeService;

/**
 * EmployeeController is a REST controller that handles HTTP requests for
 * managing employees.
 * It provides endpoints for creating, updating, retrieving, and deleting
 * employees.
 * 
 * Endpoints:
 * 
 * - POST /api/employees: Creates a new employee.
 * - DELETE /api/employees/{id}: Deletes an employee by their ID.
 * - GET /api/employees/{id}: Retrieves an employee by their ID.
 * - GET /api/employees/position/{position}: Retrieves employees by their
 * position.
 * - POST /api/employees/{id}: Updates an existing employee by their ID.
 * 
 * The controller uses EmployeeService to perform the actual operations.
 * 
 * @author Paulo
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping()
    public ResponseEntity<Employee> createEmployee(
            @RequestBody Employee employee,
            @RequestParam MultipartFile file,
            @RequestParam Map<String, MultipartFile> files) throws Exception {
        Employee savedEmployee = employeeService.saveEmployee(employee, file, files);
        return ResponseEntity.ok(savedEmployee);
    }

    @GetMapping()
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves an employee by their ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/position/{position}")
    public ResponseEntity<List<Employee>> getEmployeesByPosition(
            @PathVariable String position) {
        List<Employee> employees = employeeService.getEmployeesByPosition(position);
        return ResponseEntity.ok(employees);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody Employee employee) {
        employee.setId(id);
        Employee updatedEmployee = employeeService.updateEmployee(employee);
        return ResponseEntity.ok(updatedEmployee);
    }
}