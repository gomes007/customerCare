package com.pg.customercare.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

import com.pg.customercare.exception.impl.NotFoundException;
import com.pg.customercare.model.Dependent;
import com.pg.customercare.model.Employee;
import com.pg.customercare.model.PositionSalary;
import com.pg.customercare.repository.EmployeeRepository;
import com.pg.customercare.repository.PositionSalaryRepository;

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
        validateEmployee(employee);

        PositionSalary positionSalary = getPositionSalary(employee.getPositionSalary()); // create or find it
        employee.setPositionSalary(positionSalary); // set it to employee (merge or persist)

        setDependentsAndValidate(employee);

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

        validateEmployee(employee);

        PositionSalary positionSalary = getPositionSalary(employee.getPositionSalary());
        employee.setPositionSalary(positionSalary);

        setDependentsAndValidate(employee);

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

    // auxiliary methods
    private void validateEmployee(Employee employee) {
        if (employee.getPositionSalary() == null) {
            throw new ValidationException("Position salary is required", new HashMap<>());
        }

        LocalDate birthDate = employee.getBirthDate();
        LocalDate hireDate = employee.getHireDate();

        if (birthDate == null || !birthDate.isBefore(LocalDate.now())) {
            throw new ValidationException("Birth date must be in the past", new HashMap<>());
        }

        if (hireDate.isBefore(birthDate)) {
            throw new ValidationException("Hire date must be after birth date", new HashMap<>());
        }
    }

    private PositionSalary getPositionSalary(PositionSalary positionSalary) {
        if (positionSalary.getId() == null) {
            return positionSalaryRepository.save(positionSalary);
        } else {
            return positionSalaryRepository.findById(positionSalary.getId())
                    .orElseThrow(() -> new NotFoundException("PositionSalary not found"));
        }
    }

    private void validateDependent(Dependent dependent) {
        if (dependent.getRelationship() == null) {
            throw new ValidationException("Relationship is required for dependent", new HashMap<>());
        }
    }

    private void setDependentsAndValidate(Employee employee) {
        if (employee.getDependents() != null) {
            employee.getDependents().forEach(dependent -> {
                dependent.setEmployee(employee);
                validateDependent(dependent);
            });
        }
    }

}
