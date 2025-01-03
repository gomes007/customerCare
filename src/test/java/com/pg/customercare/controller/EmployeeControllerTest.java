package com.pg.customercare.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pg.customercare.model.Dependent;
import com.pg.customercare.model.Employee;
import com.pg.customercare.model.PositionSalary;
import com.pg.customercare.model.ENUM.RelationshipType;
import com.pg.customercare.service.EmployeeService;

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
