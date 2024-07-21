package com.pg.customercare.model;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.Email;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pg.customercare.annotation.ValidHireDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "employee")
@ValidHireDate
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

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true, optional = true, fetch = FetchType.LAZY)
    private User user;
}
