package com.pg.customercare.model;

import java.time.LocalDate;

import com.pg.customercare.model.ENUM.Situation;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "customer")
public class Customer extends Person {

    @Column(name = "contract_number")
    private String contractNumber;

    private LocalDate contractDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "situation", nullable = true)
    private Situation situation;

}
