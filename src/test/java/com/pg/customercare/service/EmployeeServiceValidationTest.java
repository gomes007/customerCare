package com.pg.customercare.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pg.customercare.model.Employee;
import com.pg.customercare.model.PositionSalary;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceValidationTest {

    private Validator validator;

    private Employee employee;
    private PositionSalary positionSalary;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        positionSalary = new PositionSalary();
        positionSalary.setId(1L);
        positionSalary.setPosition("Developer");
        positionSalary.setSalary(5000.0);

        employee = new Employee();
        employee.setId(1L);
        employee.setName("John Doe");
        employee.setPositionSalary(positionSalary);
        employee.setBirthDate(LocalDate.of(1990, 1, 1));
        employee.setHireDate(LocalDate.of(2020, 1, 1));
    }

    @Test
    void shouldThrowConstraintViolationExceptionForInvalidHireDate() {
        // ARRANGE
        employee.setBirthDate(LocalDate.of(1990, 1, 1));
        employee.setHireDate(LocalDate.of(1989, 12, 31));

        // ACT
        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        // ASSERT
        assertFalse(violations.isEmpty());

        boolean hireDateViolationFound = violations.stream()
                .anyMatch(violation -> "Hire date must be after birth date***".equals(violation.getMessage()));

        assertTrue(hireDateViolationFound, "Expected violation for hire date not found");
    }

    @Test
    void shouldPassValidationForValidHireDate() {
        // ARRANGE
        employee.setBirthDate(LocalDate.of(1990, 1, 1));
        employee.setHireDate(LocalDate.of(2020, 1, 1));

        // ACT
        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        // ASSERT
        assertTrue(violations.isEmpty());
    }
}
