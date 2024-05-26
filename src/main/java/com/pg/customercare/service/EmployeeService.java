package com.pg.customercare.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.pg.customercare.exception.impl.NotFoundException;
import com.pg.customercare.model.Employee;
import com.pg.customercare.repository.EmployeeRepository;
import com.pg.customercare.exception.impl.ValidationException;

@Service
public class EmployeeService {

    private EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new NotFoundException("Employee not found with id " + id);
        }
        employeeRepository.deleteById(id);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found with id " + id));
    }

    public Employee updateEmployee(Employee employee) {
        if (!employeeRepository.existsById(employee.getId())) {
            throw new NotFoundException("Employee not found with id " + employee.getId());
        }
        return employeeRepository.save(employee);
    }

    public List<Employee> getEmployeesByPosition(String position) {
        List<Employee> employees = employeeRepository.findByPosition(position);
        if (employees.isEmpty()) {
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("position", position);
            throw new ValidationException("No employees found with position: " + position, errorDetails);
        }
        return employees;
    }

}
