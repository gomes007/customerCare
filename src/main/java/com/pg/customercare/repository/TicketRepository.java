package com.pg.customercare.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pg.customercare.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long>{
    
}
