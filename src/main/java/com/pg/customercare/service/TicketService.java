package com.pg.customercare.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.pg.customercare.exception.impl.NotFoundException;
import com.pg.customercare.exception.impl.ValidationException;
import com.pg.customercare.model.Ticket;
import com.pg.customercare.model.TicketFiles;
import com.pg.customercare.model.ENUM.Status;
import com.pg.customercare.repository.TicketFilesRepository;
import com.pg.customercare.repository.TicketRepository;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketFilesRepository ticketFilesRepository;

    public TicketService(TicketRepository ticketRepository, TicketFilesRepository ticketFilesRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketFilesRepository = ticketFilesRepository;
    }

    private final String UPLOAD_FOLDER = "C:\\TicketFiles\\";

    @Transactional
    public Ticket createTicket(Ticket ticket, MultipartFile[] files) {

        ticket.setStatus(Status.OPEN);

        validateTicket(ticket);

        var savedTicket = ticketRepository.save(ticket);

        if (files != null && files.length > 0) {
            saveFiles(savedTicket, files);
        }

        return savedTicket;
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

    // save files
    private void saveFiles(Ticket ticket, MultipartFile[] files) {
        final long MAX_SIZE = 10 * 1024 * 1024; // 10MB

        List<TicketFiles> ticketFilesList = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.getSize() > MAX_SIZE) {
                throw new ValidationException(
                        "File size exceeds the maximum limit of " + MAX_SIZE / (1024 * 1024) + "MB.",
                        new HashMap<>());
            }

            try {
                String originalFileName = file.getOriginalFilename();
                String fileName = System.currentTimeMillis() + "_" + originalFileName;
                String fileAddress = UPLOAD_FOLDER + fileName;

                // Create the directory if it does not exist
                Path directoryPath = Paths.get(UPLOAD_FOLDER);
                if (!Files.exists(directoryPath)) {
                    Files.createDirectories(directoryPath);
                }

                // save file to the directory
                Path filePath = Paths.get(fileAddress);
                Files.write(filePath, file.getBytes());

                // create ticket file and associate it with the ticket
                TicketFiles ticketFile = new TicketFiles();
                ticketFile.setFileName(fileName);
                ticketFile.setFilePath(filePath.toString());
                ticketFile.setTicket(ticket);

                ticketFilesList.add(ticketFile);

            } catch (IOException e) {
                throw new IllegalArgumentException("Error saving file: " + file.getOriginalFilename(), e);
            }
        }

        ticketFilesRepository.saveAll(ticketFilesList);
    }

}
