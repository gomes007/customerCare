package com.pg.customercare.annotation;

import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.pg.customercare.model.Employee;

public class HireDateValidator implements ConstraintValidator<ValidHireDate, Employee> {

    @Override
    public void initialize(ValidHireDate constraintAnnotation) {
        // Initialization code, if needed
    }

    @Override
    public boolean isValid(Employee employee, ConstraintValidatorContext context) {
        if (employee == null) {
            return true; // Assume valid if null to let other constraints handle null checks
        }
        LocalDate birthDate = employee.getBirthDate();
        LocalDate hireDate = employee.getHireDate();

        if (birthDate == null || hireDate == null) {
            return true; // Assume valid if either date is null to let other constraints handle null
                         // checks
        }

        return !hireDate.isBefore(birthDate); // hireDate should be equal or after birthDate
    }
}
