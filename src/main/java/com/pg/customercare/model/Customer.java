package com.pg.customercare.model;

import com.pg.customercare.model.ENUM.CustomerType;
import com.pg.customercare.model.ENUM.Situation;
import jakarta.persistence.*;
import java.time.LocalDate;
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
