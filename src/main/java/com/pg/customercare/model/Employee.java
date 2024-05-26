package com.pg.customercare.model;

import jakarta.persistence.*;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "employee")
public class Employee extends Person {

    @OneToOne
    @JoinColumn(name = "positionSalary_id")
    private PositionSalary positionSalary;

}
