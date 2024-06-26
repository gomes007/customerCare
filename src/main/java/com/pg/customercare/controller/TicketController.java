package com.pg.customercare.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pg.customercare.model.Ticket;
import com.pg.customercare.service.TicketService;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<Ticket> createTicket(
            @ModelAttribute Ticket ticket,
            @RequestParam("file") MultipartFile[] files) {
        Ticket savedTicket = ticketService.createTicket(ticket, files);
        return ResponseEntity.ok(savedTicket);
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        var tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        var ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@Valid @RequestBody Ticket ticket) {
        var updatedTicket = ticketService.updateTicket(ticket);
        return ResponseEntity.ok(updatedTicket);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

}
