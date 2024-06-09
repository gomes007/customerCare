package com.pg.customercare.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pg.customercare.model.TicketFiles;

public interface TicketFilesRepository extends JpaRepository<TicketFiles, Long> {

}
