package com.pg.customercare.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
import org.springframework.test.web.servlet.MockMvc;

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

    @Test
    void shouldCreateTicket() throws Exception {
        // ARRANGE
        given(ticketService.createTicket(any(Ticket.class))).willReturn(ticket);

        // ACT & ASSERT
        mockMvc.perform(post("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticket)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticket.getId()))
                .andExpect(jsonPath("$.customer.id").value(customer.getId()))
                .andExpect(jsonPath("$.classification").value(ticket.getClassification().name()))
                .andExpect(jsonPath("$.priority").value(ticket.getPriority().name()))
                .andExpect(jsonPath("$.status").value(ticket.getStatus().name()))
                .andExpect(jsonPath("$.openingDate").value(ticket.getOpeningDate().toString()));
    }

    @Test
    void shouldUpdateTicket() throws Exception {
        // ARRANGE
        given(ticketService.updateTicket(any(Ticket.class))).willReturn(ticket);

        // ACT & ASSERT
        mockMvc.perform(post("/api/tickets/{id}", ticket.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticket)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticket.getId()))
                .andExpect(jsonPath("$.customer.id").value(customer.getId()))
                .andExpect(jsonPath("$.classification").value(ticket.getClassification().name()))
                .andExpect(jsonPath("$.priority").value(ticket.getPriority().name()))
                .andExpect(jsonPath("$.status").value(ticket.getStatus().name()))
                .andExpect(jsonPath("$.openingDate").value(ticket.getOpeningDate().toString()));
    }

    @Test
    void shouldDeleteTicket() throws Exception {
        // ARRANGE
        willDoNothing().given(ticketService).deleteTicket(ticket.getId());

        // ACT & ASSERT
        mockMvc.perform(delete("/api/tickets/{id}", ticket.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldGetAllTickets() throws Exception {
        // ARRANGE
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);
        given(ticketService.getAllTickets()).willReturn(tickets);

        // ACT & ASSERT
        mockMvc.perform(get("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ticket.getId()))
                .andExpect(jsonPath("$[0].customer.id").value(customer.getId()))
                .andExpect(jsonPath("$[0].classification").value(ticket.getClassification().name()))
                .andExpect(jsonPath("$[0].priority").value(ticket.getPriority().name()))
                .andExpect(jsonPath("$[0].status").value(ticket.getStatus().name()))
                .andExpect(jsonPath("$[0].openingDate").value(ticket.getOpeningDate().toString()));
    }

    @Test
    void shouldGetTicketById() throws Exception {
        // ARRANGE
        Long id = 1L;
        given(ticketService.getTicketById(id)).willReturn(ticket);

        // ACT & ASSERT
        mockMvc.perform(get("/api/tickets/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticket.getId()))
                .andExpect(jsonPath("$.customer.id").value(customer.getId()))
                .andExpect(jsonPath("$.classification").value(ticket.getClassification().name()))
                .andExpect(jsonPath("$.priority").value(ticket.getPriority().name()))
                .andExpect(jsonPath("$.status").value(ticket.getStatus().name()))
                .andExpect(jsonPath("$.openingDate").value(ticket.getOpeningDate().toString()));
    }
}
