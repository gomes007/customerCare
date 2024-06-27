package com.pg.customercare.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleNameDTO {
    private Long id;
    private String name;
    private List<PermissionDTO> permissions;
}
