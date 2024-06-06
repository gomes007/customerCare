package com.pg.customercare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pg.customercare.model.Employee;
import com.pg.customercare.model.PositionSalary;
import com.pg.customercare.service.EmployeeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
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
    }

    @Test
    void shouldCreateEmployee() throws Exception {
        given(employeeService.saveEmployee(employee)).willReturn(employee);

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(employee.getName()));
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
