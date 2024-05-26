package com.pg.customercare.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.pg.customercare.exception.impl.NotFoundException;
import com.pg.customercare.model.Employee;
import com.pg.customercare.model.PositionSalary;
import com.pg.customercare.repository.EmployeeRepository;
import com.pg.customercare.repository.PositionSalaryRepository;

import jakarta.persistence.EntityNotFoundException;


import com.pg.customercare.exception.impl.ValidationException;

@Service
public class EmployeeService {

    private EmployeeRepository employeeRepository;
    private PositionSalaryRepository positionSalaryRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
            PositionSalaryRepository positionSalaryRepository) {
        this.employeeRepository = employeeRepository;
        this.positionSalaryRepository = positionSalaryRepository;
    }

    public Employee saveEmployee(Employee employee) {
        if (employee.getPositionSalary() == null) {
            throw new ValidationException("Position salary is required", new HashMap<>());
        }

        PositionSalary positionSalary = employee.getPositionSalary();
        if (positionSalary.getId() == null) {
            // If PositionSalary does not have an ID, persist it first
            positionSalary = positionSalaryRepository.save(positionSalary);
        } else {
            // If PositionSalary already has an ID, merge it to ensure it is managed
            positionSalary = positionSalaryRepository.findById(positionSalary.getId())
                    .orElseThrow(() -> new EntityNotFoundException("PositionSalary not found"));
        }
        employee.setPositionSalary(positionSalary);

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
