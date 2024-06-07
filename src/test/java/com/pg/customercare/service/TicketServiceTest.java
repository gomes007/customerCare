package com.pg.customercare.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pg.customercare.exception.impl.NotFoundException;
import com.pg.customercare.model.Customer;
import com.pg.customercare.model.Ticket;
import com.pg.customercare.model.ENUM.Classification;
import com.pg.customercare.model.ENUM.Priority;
import com.pg.customercare.model.ENUM.Status;
import com.pg.customercare.repository.TicketRepository;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @InjectMocks
    private TicketService ticketService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private Ticket ticket;

    @Mock
    private Customer customer;

    @Captor
    private ArgumentCaptor<Ticket> ticketCaptor;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");

        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setCustomer(customer);
        ticket.setClassification(Classification.OTHERS);
        ticket.setPriority(Priority.HIGH);
        ticket.setStatus(Status.OPEN);
        ticket.setOpeningDate(LocalDate.of(2023, 6, 6));
    }

    @Test
    void shouldCreateTicket() {
        // ARRANGE
        given(ticketRepository.save(ticket)).willReturn(ticket);

        // ACT
        Ticket result = ticketService.createTicket(ticket);

        // ASSERT
        assertNotNull(result);
        assertEquals(Status.OPEN, result.getStatus());
        then(ticketRepository).should().save(ticketCaptor.capture());
        assertEquals(ticket, ticketCaptor.getValue());
    }

    @Test
    void shouldGetTicketById() {
        // ARRANGE
        Long id = 1L;
        given(ticketRepository.findById(id)).willReturn(Optional.of(ticket));

        // ACT
        Ticket result = ticketService.getTicketById(id);

        // ASSERT
        assertNotNull(result);
        assertEquals(ticket, result);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenGettingTicketByIdNotFound() {
        // ARRANGE
        Long id = 1L;
        given(ticketRepository.findById(id)).willReturn(Optional.empty());

        // ACT & ASSERT
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            ticketService.getTicketById(id);
        });
        assertEquals("Ticket not found with id " + id, exception.getMessage());
    }

    @Test
    void shouldGetAllTickets() {
        // ARRANGE
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);
        given(ticketRepository.findAll()).willReturn(tickets);

        // ACT
        List<Ticket> result = ticketService.getAllTickets();

        // ASSERT
        assertEquals(1, result.size());
        assertEquals(ticket, result.get(0));
    }

    @Test
    void shouldDeleteTicket() {
        // ARRANGE
        Long id = 1L;
        given(ticketRepository.existsById(id)).willReturn(true);

        // ACT
        ticketService.deleteTicket(id);

        // ASSERT
        then(ticketRepository).should().deleteById(id);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenDeletingNonExistentTicket() {
        // ARRANGE
        Long id = 1L;
        given(ticketRepository.existsById(id)).willReturn(false);

        // ACT & ASSERT
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            ticketService.deleteTicket(id);
        });
        assertEquals("Ticket not found with id " + id, exception.getMessage());
    }

    @Test
    void shouldUpdateTicket() {
        // ARRANGE
        given(ticketRepository.existsById(ticket.getId())).willReturn(true);
        given(ticketRepository.save(ticket)).willReturn(ticket);

        // ACT
        Ticket result = ticketService.updateTicket(ticket);

        // ASSERT
        assertNotNull(result);
        assertEquals(ticket, result);
        then(ticketRepository).should().save(ticketCaptor.capture());
        assertEquals(ticket, ticketCaptor.getValue());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdatingNonExistentTicket() {
        // ARRANGE
        given(ticketRepository.existsById(ticket.getId())).willReturn(false);

        // ACT & ASSERT
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            ticketService.updateTicket(ticket);
        });
        assertEquals("Ticket not found with id " + ticket.getId(), exception.getMessage());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenCreatingTicketWithNullCustomer() {
        // ARRANGE
        ticket.setCustomer(null);

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ticketService.createTicket(ticket);
        });
        assertEquals("Customer is required", exception.getMessage());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenCreatingTicketWithFutureOpeningDate() {
        // ARRANGE
        ticket.setOpeningDate(LocalDate.now().plusDays(1));

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ticketService.createTicket(ticket);
        });
        assertEquals("Opening date must be in the past", exception.getMessage());
    }
}
