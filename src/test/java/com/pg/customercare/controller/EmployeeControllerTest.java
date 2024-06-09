package com.pg.customercare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pg.customercare.model.Dependent;
import com.pg.customercare.model.Employee;
import com.pg.customercare.model.PositionSalary;
import com.pg.customercare.model.ENUM.RelationshipType;
import com.pg.customercare.service.EmployeeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee employee;
    private PositionSalary positionSalary;

    private MockMultipartFile file;
    private MockMultipartFile dependentFile;
    private Dependent dependent;

    @BeforeEach
    void setUp() {
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

        dependent = new Dependent();
        dependent.setId(2L);
        dependent.setName("Jane Doe");
        dependent.setBirthDate(LocalDate.of(1992, 2, 2));
        dependent.setRelationship(RelationshipType.SPOUSE);
        dependent.setEmployee(employee);

        List<Dependent> dependents = new ArrayList<>();
        dependents.add(dependent);
        employee.setDependents(dependents);

        file = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                "image content".getBytes());

        dependentFile = new MockMultipartFile(
                "dependents[0].file",
                "dependent_photo.jpg",
                "image/jpeg",
                "dependent image content".getBytes());
    }

    @Test
    void shouldCreateEmployee() throws Exception {
        // ARRANGE
        given(employeeService.saveEmployee(any(Employee.class), any(MultipartFile.class), any(Map.class)))
                .willReturn(employee);

        // ACT & ASSERT
        mockMvc.perform(multipart("/api/employees")
                .file(file)
                .file(dependentFile)
                .param("name", "John Doe")
                .param("birthDate", "1990-01-01")
                .param("hireDate", "2020-01-01")
                .param("positionSalary.id", "1")
                .param("dependents[0].id", "2")
                .param("dependents[0].name", "Jane Doe")
                .param("dependents[0].birthDate", "1992-02-02")
                .param("dependents[0].relationship", "SPOUSE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.positionSalary.position").value("Developer"))
                .andExpect(jsonPath("$.dependents[0].name").value("Jane Doe"));

        // Verificar se o método saveEmployee do serviço foi chamado com os parâmetros
        // corretos
        verify(employeeService).saveEmployee(any(Employee.class), any(MultipartFile.class), any(Map.class));
    }

    @Test
    void shouldDeleteEmployee() throws Exception {
        mockMvc.perform(delete("/api/employees/{id}", 1L))
                .andExpect(status().isNoContent());

        // Additional verification can be added here
    }

    @Test
    void shouldGetAllEmployees() throws Exception {
        List<Employee> employees = Arrays.asList(employee);
        given(employeeService.getAllEmployees()).willReturn(employees);

        mockMvc.perform(get("/api/employees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(employee.getName()));
    }

    @Test
    void shouldGetEmployeeById() throws Exception {
        given(employeeService.getEmployeeById(1L)).willReturn(employee);

        mockMvc.perform(get("/api/employees/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(employee.getName()));
    }

    @Test
    void shouldGetEmployeesByPosition() throws Exception {
        List<Employee> employees = Arrays.asList(employee);
        given(employeeService.getEmployeesByPosition("Developer")).willReturn(employees);

        mockMvc.perform(get("/api/employees/position/{position}", "Developer")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(employee.getName()));
    }

    @Test
    void shouldUpdateEmployee() throws Exception {
        given(employeeService.updateEmployee(employee)).willReturn(employee);

        mockMvc.perform(post("/api/employees/{id}", employee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(employee.getName()));
    }
}
