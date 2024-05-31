package com.pg.customercare.model;

import java.time.LocalDate;

import javax.validation.constraints.Past;

import com.pg.customercare.model.ENUM.CustomerType;
import com.pg.customercare.model.ENUM.Situation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "customer")
public class Customer extends Person {

  @Column(name = "contract_number")
  private String contractNumber;

  @Past(message = "The contract date must be in the past")
  private LocalDate contractDate;

  private String corporateEmail;
  private String cnpj;
  private String tradeName;

  @Enumerated(EnumType.STRING)
  @Column(name = "situation", nullable = true)
  private Situation situation;

  @Enumerated(EnumType.STRING)
  @Column(name = "customer_type", nullable = false)
  private CustomerType customerType;
}
