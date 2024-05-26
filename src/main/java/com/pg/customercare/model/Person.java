package com.pg.customercare.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDate;

import com.pg.customercare.model.ENUM.Gender;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;
    private String privateEmail;
    private String cpf;
    private String phone;
    private LocalDate birthDate;
    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = true)
    private Gender gender;

    private String otherInformation;

}
