package com.pg.customercare.service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.pg.customercare.exception.impl.NotFoundException;
import com.pg.customercare.model.Ticket;
import com.pg.customercare.model.ENUM.Status;
import com.pg.customercare.repository.TicketRepository;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket createTicket(Ticket ticket) {
        ticket.setStatus(Status.OPEN);
        validateTicket(ticket);
        return ticketRepository.save(ticket);
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket not found with id " + id));
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new NotFoundException("Ticket not found with id " + id);
        }
        ticketRepository.deleteById(id);
    }

    public Ticket updateTicket(Ticket ticket) {
        if (!ticketRepository.existsById(ticket.getId())) {
            throw new NotFoundException("Ticket not found with id " + ticket.getId());
        }
        validateTicket(ticket);
        return ticketRepository.save(ticket);
    }

    // Auxiliary method
    private void validateTicket(Ticket ticket) {
        Map<Boolean, String> validations = new LinkedHashMap<>();
        validations.put(ticket.getCustomer() == null, "Customer is required");
        validations.put(ticket.getClassification() == null, "Classification is required");
        validations.put(ticket.getPriority() == null, "Priority is required");
        validations.put(ticket.getStatus() == null, "Status is required");
        validations.put(ticket.getOpeningDate() == null, "Opening date is required");
        validations.put(ticket.getOpeningDate() != null && ticket.getOpeningDate().isAfter(LocalDate.now()),
                "Opening date must be in the past");

        validations.forEach((condition, errorMessage) -> {
            if (condition) {
                throw new IllegalArgumentException(errorMessage);
            }
        });
    }

}
