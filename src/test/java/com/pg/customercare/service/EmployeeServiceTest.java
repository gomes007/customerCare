package com.pg.customercare.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.pg.customercare.exception.impl.NotFoundException;
import com.pg.customercare.exception.impl.ValidationException;
import com.pg.customercare.model.Dependent;
import com.pg.customercare.model.Employee;
import com.pg.customercare.model.PositionSalary;
import com.pg.customercare.model.ENUM.RelationshipType;
import com.pg.customercare.repository.EmployeeRepository;
import com.pg.customercare.repository.PositionSalaryRepository;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PositionSalaryRepository positionSalaryRepository;

    @Captor
    private ArgumentCaptor<Employee> employeeCaptor;

    private Employee employee;
    private PositionSalary positionSalary;

    private Validator validator;

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

        Dependent dependent = new Dependent();
        dependent.setId(2L);
        dependent.setName("Jane Doe");
        dependent.setBirthDate(LocalDate.of(1992, 2, 2));
        dependent.setRelationship(RelationshipType.SPOUSE);
        dependent.setEmployee(employee);
        employee.setDependents(new ArrayList<>());
        employee.getDependents().add(dependent);
    }

    @Test
    void shouldSaveEmployee() throws Exception {
        // ARRANGE
        given(positionSalaryRepository.findById(positionSalary.getId())).willReturn(Optional.of(positionSalary));
        given(employeeRepository.save(any(Employee.class))).willReturn(employee);

        // Mock do arquivo do funcionário
        MultipartFile mockEmployeeFile = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                "test image content".getBytes());

        // Mock do arquivo do dependente
        MultipartFile mockDependentFile = new MockMultipartFile(
                "dependents[0].file",
                "dependent_photo.jpg",
                "image/jpeg",
                "dependent test image content".getBytes());

        // Criação do mapa de arquivos
        Map<String, MultipartFile> files = new HashMap<>();
        files.put("dependents[0].file", mockDependentFile);

        // ACT
        Employee savedEmployee = employeeService.saveEmployee(employee, mockEmployeeFile, files);

        // ASSERT
        assertNotNull(savedEmployee);
        assertEquals("John Doe", savedEmployee.getName());
        then(employeeRepository).should().save(employeeCaptor.capture());
        Employee capturedEmployee = employeeCaptor.getValue();
        assertEquals("John Doe", capturedEmployee.getName());
        assertEquals(positionSalary, capturedEmployee.getPositionSalary());

        // Verifica se as informações da foto do funcionário foram atribuídas
        // corretamente
        assertNotNull(capturedEmployee.getPhotoName());
        assertNotNull(capturedEmployee.getPhotoAddress());

        // Verifica se as informações da foto do dependente foram atribuídas
        // corretamente
        Dependent savedDependent = capturedEmployee.getDependents().get(0);
        assertNotNull(savedDependent.getPhotoName());
        assertNotNull(savedDependent.getPhotoAddress());

        // Se necessário, adicionar verificações específicas para o conteúdo ou caminho
        // das fotos
    }

    @Test
    void shouldThrowNotFoundExceptionWhenDeleteNonExistentEmployee() {
        // ARRANGE
        Long employeeId = 1L;
        given(employeeRepository.existsById(employeeId)).willReturn(false);

        // ACT & ASSERT
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            employeeService.deleteEmployee(employeeId);
        });

        assertEquals("Employee not found with id " + employeeId, exception.getMessage());
    }

    @Test
    void shouldDeleteEmployee() {
        // ARRANGE
        Long employeeId = 1L;
        given(employeeRepository.existsById(employeeId)).willReturn(true);

        // ACT
        employeeService.deleteEmployee(employeeId);

        // ASSERT
        then(employeeRepository).should().deleteById(employeeId);
    }

    @Test
    void shouldGetAllEmployees() {
        // ARRANGE
        List<Employee> employees = Arrays.asList(employee);
        given(employeeRepository.findAll()).willReturn(employees);

        // ACT
        List<Employee> result = employeeService.getAllEmployees();

        // ASSERT
        assertEquals(1, result.size());
        assertEquals(employee, result.get(0));
    }

    @Test
    void shouldGetEmployeeById() {
        // ARRANGE
        Long employeeId = 1L;
        given(employeeRepository.findById(employeeId)).willReturn(Optional.of(employee));

        // ACT
        Employee result = employeeService.getEmployeeById(employeeId);

        // ASSERT
        assertEquals(employee, result);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenGetEmployeeByIdNonExistent() {
        // ARRANGE
        Long employeeId = 1L;
        given(employeeRepository.findById(employeeId)).willReturn(Optional.empty());

        // ACT & ASSERT
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            employeeService.getEmployeeById(employeeId);
        });

        assertEquals("Employee not found with id " + employeeId, exception.getMessage());
    }

    @Test
    void shouldUpdateEmployee() {
        // ARRANGE
        given(employeeRepository.existsById(employee.getId())).willReturn(true);
        given(positionSalaryRepository.findById(positionSalary.getId())).willReturn(Optional.of(positionSalary));
        given(employeeRepository.save(employee)).willReturn(employee);

        // ACT
        Employee updatedEmployee = employeeService.updateEmployee(employee);

        // ASSERT
        assertNotNull(updatedEmployee);
        assertEquals("John Doe", updatedEmployee.getName());
        then(employeeRepository).should().save(employeeCaptor.capture());
        Employee capturedEmployee = employeeCaptor.getValue();
        assertEquals("John Doe", capturedEmployee.getName());
        assertEquals(positionSalary, capturedEmployee.getPositionSalary());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdateNonExistentEmployee() {
        // ARRANGE
        given(employeeRepository.existsById(employee.getId())).willReturn(false);

        // ACT & ASSERT
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            employeeService.updateEmployee(employee);
        });

        assertEquals("Employee not found with id " + employee.getId(), exception.getMessage());
    }

    @Test
    void shouldGetEmployeesByPosition() {
        // ARRANGE
        String position = "Developer";
        List<Employee> employees = Arrays.asList(employee);
        given(employeeRepository.findByPosition(position)).willReturn(employees);

        // ACT
        List<Employee> result = employeeService.getEmployeesByPosition(position);

        // ASSERT
        assertEquals(1, result.size());
        assertEquals(employee, result.get(0));
    }

    @Test
    void shouldThrowValidationExceptionWhenNoEmployeesByPosition() {
        // ARRANGE
        String position = "NonExistent";
        given(employeeRepository.findByPosition(position)).willReturn(Collections.emptyList());

        // ACT & ASSERT
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            employeeService.getEmployeesByPosition(position);
        });

        assertEquals("No employees found with position: " + position, exception.getMessage());
    }

    @Test
    void shouldThrowConstraintViolationExceptionForFutureBirthDate() {
        // ARRANGE
        employee.setBirthDate(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);

        // ASSERT
        assertFalse(violations.isEmpty());

        boolean birthDateViolationFound = violations.stream()
                .anyMatch(violation -> "Birth date must be in the past".equals(violation.getMessage()));

        assertTrue(birthDateViolationFound, "Expected violation for birth date in the past not found");
    }

}
