package com.pg.customercare.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pg.customercare.model.Customer;
import com.pg.customercare.model.Ticket;
import com.pg.customercare.model.ENUM.Classification;
import com.pg.customercare.model.ENUM.Priority;
import com.pg.customercare.model.ENUM.Status;
import com.pg.customercare.service.TicketService;

@WebMvcTest(TicketController.class)
public class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    @Autowired
    private ObjectMapper objectMapper;

    private Ticket ticket;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");

        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setCustomer(customer);
        ticket.setClassification(Classification.OTHERS);
        ticket.setPriority(Priority.LOW);
        ticket.setStatus(Status.OPEN);
        ticket.setOpeningDate(LocalDate.of(2023, 6, 6));
    }

    





}
