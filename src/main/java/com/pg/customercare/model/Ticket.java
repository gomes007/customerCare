package com.pg.customercare.model;


import java.time.LocalDate;

import com.pg.customercare.model.ENUM.Classification;
import com.pg.customercare.model.ENUM.Priority;
import com.pg.customercare.model.ENUM.Status;

import jakarta.persistence.*;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ticket")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name = "contact_name")
    private String contactName;

    private String email;
    private String phone;
    private String subject;
    private String description;

    @Column(name = "opening_date")
    private LocalDate openingDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "classification", nullable = false)
    private Classification classification;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee ticketOwner;


}
