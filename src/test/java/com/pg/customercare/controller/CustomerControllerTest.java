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
import com.pg.customercare.model.Customer;
import com.pg.customercare.model.ENUM.CustomerType;
import com.pg.customercare.model.ENUM.Gender;
import com.pg.customercare.service.CustomerService;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setCustomerType(CustomerType.INDIVIDUAL);
        customer.setGender(Gender.FEMALE);
    }

    @Test
    void shouldCreateCustomer() throws Exception {
        // ARRANGE
        given(customerService.saveCustomer(any(Customer.class))).willReturn(customer);

        // ACT & ASSERT
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customer.getId()))
                .andExpect(jsonPath("$.name").value(customer.getName()))
                .andExpect(jsonPath("$.customerType").value(customer.getCustomerType().name()))
                .andExpect(jsonPath("$.gender").value(customer.getGender().name()));
    }

    @Test
    void shouldUpdateCustomer() throws Exception {
        // ARRANGE
        given(customerService.updateCustomer(any(Customer.class))).willReturn(customer);

        // ACT & ASSERT
        mockMvc.perform(post("/api/customers/{id}", customer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customer.getId()))
                .andExpect(jsonPath("$.name").value(customer.getName()))
                .andExpect(jsonPath("$.customerType").value(customer.getCustomerType().name()))
                .andExpect(jsonPath("$.gender").value(customer.getGender().name()));
    }

    @Test
    void shouldDeleteCustomer() throws Exception {
        // ARRANGE
        willDoNothing().given(customerService).deleteCustomer(customer.getId());

        // ACT & ASSERT
        mockMvc.perform(delete("/api/customers/{id}", customer.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldGetAllCustomers() throws Exception {
        // ARRANGE
        List<Customer> customers = new ArrayList<>();
        customers.add(customer);
        given(customerService.getAllCustomers()).willReturn(customers);

        // ACT & ASSERT
        mockMvc.perform(get("/api/customers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(customer.getId()))
                .andExpect(jsonPath("$[0].name").value(customer.getName()))
                .andExpect(jsonPath("$[0].customerType").value(customer.getCustomerType().name()))
                .andExpect(jsonPath("$[0].gender").value(customer.getGender().name()));
    }

    @Test
    void shouldGetCustomerById() throws Exception {
        // ARRANGE
        Long id = 1L;
        given(customerService.getCustomerById(id)).willReturn(customer);

        // ACT & ASSERT
        mockMvc.perform(get("/api/customers/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customer.getId()))
                .andExpect(jsonPath("$.name").value(customer.getName()))
                .andExpect(jsonPath("$.customerType").value(customer.getCustomerType().name()))
                .andExpect(jsonPath("$.gender").value(customer.getGender().name()));
    }
}
