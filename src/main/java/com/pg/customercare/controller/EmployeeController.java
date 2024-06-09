package com.pg.customercare.controller;

import com.pg.customercare.model.Employee;
import com.pg.customercare.service.EmployeeService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

  private final EmployeeService employeeService;

  public EmployeeController(EmployeeService employeeService) {
    this.employeeService = employeeService;
  }

    
 @PostMapping
    public ResponseEntity<Employee> createEmployee(
            @Valid @ModelAttribute Employee employee,
            @RequestParam("file") MultipartFile file,
            @RequestParam Map<String, MultipartFile> files
    ) {
        try {
            Employee savedEmployee = employeeService.saveEmployee(employee, file, files);
            return ResponseEntity.ok(savedEmployee);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }






  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
    employeeService.deleteEmployee(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<Employee>> getAllEmployees() {
    List<Employee> employees = employeeService.getAllEmployees();
    return ResponseEntity.ok(employees);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
    Employee employee = employeeService.getEmployeeById(id);
    return ResponseEntity.ok(employee);
  }

  @GetMapping("/position/{position}")
  public ResponseEntity<List<Employee>> getEmployeesByPosition(
    @PathVariable String position
  ) {
    List<Employee> employees = employeeService.getEmployeesByPosition(position);
    return ResponseEntity.ok(employees);
  }

  @PostMapping("/{id}")
  public ResponseEntity<Employee> updateEmployee(
    @Valid @RequestBody Employee employee
  ) {
    Employee updatedEmployee = employeeService.updateEmployee(employee);
    return ResponseEntity.ok(updatedEmployee);
  }
}
