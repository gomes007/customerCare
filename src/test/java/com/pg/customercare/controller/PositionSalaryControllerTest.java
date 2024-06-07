package com.pg.customercare.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pg.customercare.model.PositionSalary;
import com.pg.customercare.model.Role;
import com.pg.customercare.service.PositionSalaryService;

@WebMvcTest(PositionSalaryController.class)
public class PositionSalaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PositionSalaryService positionSalaryService;

    @Autowired
    private ObjectMapper objectMapper;

    private PositionSalary positionSalary;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");

        positionSalary = new PositionSalary();
        positionSalary.setId(1L);
        positionSalary.setRole(role);
        positionSalary.setSalary(50000.00);
    }

    @Test
    void shouldCreatePositionSalary() throws Exception {
        // ARRANGE
        given(positionSalaryService.savePositionSalary(any(PositionSalary.class))).willReturn(positionSalary);

        // ACT & ASSERT
        mockMvc.perform(post("/api/position-salaries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(positionSalary)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(positionSalary.getId()))
                .andExpect(jsonPath("$.role.id").value(role.getId()))
                .andExpect(jsonPath("$.salary").value(positionSalary.getSalary()));
    }

    @Test
    void shouldUpdatePositionSalary() throws Exception {
        // ARRANGE
        given(positionSalaryService.updatePositionSalary(any(PositionSalary.class))).willReturn(positionSalary);

        // ACT & ASSERT
        mockMvc.perform(post("/api/position-salaries/{id}", positionSalary.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(positionSalary)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(positionSalary.getId()))
                .andExpect(jsonPath("$.role.id").value(role.getId()))
                .andExpect(jsonPath("$.salary").value(positionSalary.getSalary()));
    }

    @Test
    void shouldDeletePositionSalary() throws Exception {
        // ARRANGE
        willDoNothing().given(positionSalaryService).deletePositionSalary(positionSalary.getId());

        // ACT & ASSERT
        mockMvc.perform(delete("/api/position-salaries/{id}", positionSalary.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldGetAllPositionSalaries() throws Exception {
        // ARRANGE
        List<PositionSalary> positionSalaries = new ArrayList<>();
        positionSalaries.add(positionSalary);
        given(positionSalaryService.getAllPositionSalaries()).willReturn(positionSalaries);

        // ACT & ASSERT
        mockMvc.perform(get("/api/position-salaries")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(positionSalary.getId()))
                .andExpect(jsonPath("$[0].role.id").value(role.getId()))
                .andExpect(jsonPath("$[0].salary").value(positionSalary.getSalary()));
    }

    @Test
    void shouldGetPositionSalaryById() throws Exception {
        // ARRANGE
        Long id = 1L;
        given(positionSalaryService.getPositionSalaryById(id)).willReturn(positionSalary);

        // ACT & ASSERT
        mockMvc.perform(get("/api/position-salaries/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(positionSalary.getId()))
                .andExpect(jsonPath("$.role.id").value(role.getId()))
                .andExpect(jsonPath("$.salary").value(positionSalary.getSalary()));
    }
}
