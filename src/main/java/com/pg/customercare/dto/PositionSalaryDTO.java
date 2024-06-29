package com.pg.customercare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PositionSalaryDTO {
    private Long id;
    private String position;
    private Double salary;
    private Double commission;
    private String roleName;
}
