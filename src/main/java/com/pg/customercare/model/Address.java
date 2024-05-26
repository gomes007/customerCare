package com.pg.customercare.model;

import jakarta.persistence.*;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

        private String street;
        private String number;
        private String neighborhood;
        private String zipCode;
        private String complement;
        private String city;
        private String state;

}
