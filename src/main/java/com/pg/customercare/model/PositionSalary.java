package com.pg.customercare.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "position_salary")
public class PositionSalary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String position;
    private Double salary;
    private Double commission;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;


}
