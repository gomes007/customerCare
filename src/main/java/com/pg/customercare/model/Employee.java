package com.pg.customercare.model;

import java.time.LocalDate;
import javax.validation.constraints.Email;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "employee")

public class Employee extends Person {

    @ManyToOne
    @JoinColumn(name = "positionSalary_id", nullable = false)
    private PositionSalary positionSalary;

    private LocalDate hireDate;

    @Email(message = "Company email should be valid")
    private String companyEmail;

    private boolean hasDependents;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Dependent> dependents;


}