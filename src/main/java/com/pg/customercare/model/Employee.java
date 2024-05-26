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

    @ManyToOne
    @JoinColumn(name = "positionSalary_id", nullable = false)
    private PositionSalary positionSalary;

    /*
     * @ManyToOne
     * 
     * @JoinColumn(name = "manager_id")
     * private Employee manager;
     * 
     * @ManyToOne
     * 
     * @JoinColumn(name = "supervisor_id")
     * private Employee supervisor;
     */

}
