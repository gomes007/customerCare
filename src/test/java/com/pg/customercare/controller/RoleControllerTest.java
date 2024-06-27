package com.pg.customercare.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pg.customercare.dto.PermissionDTO;
import com.pg.customercare.dto.RoleNameDTO;
import com.pg.customercare.model.Role;
import com.pg.customercare.service.RoleService;
import com.pg.customercare.util.Response;

@WebMvcTest(RoleController.class)
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
    }

    @Test
    void shouldCreateRole() throws Exception {
        // ARRANGE
        given(roleService.saveRole(any(Role.class))).willReturn(role);

        // ACT & ASSERT
        mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(role.getId()))
                .andExpect(jsonPath("$.name").value(role.getName()));
    }

    @Test
    void shouldUpdateRole() throws Exception {
        // ARRANGE
        given(roleService.updateRole(any(Role.class))).willReturn(role);

        // ACT & ASSERT
        mockMvc.perform(put("/api/roles/{id}", role.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(role.getId()))
                .andExpect(jsonPath("$.name").value(role.getName()));
    }

    @Test
    void shouldDeleteRole() throws Exception {
        // ARRANGE
        willDoNothing().given(roleService).deleteRole(role.getId());

        // ACT & ASSERT
        mockMvc.perform(delete("/api/roles/{id}", role.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldGetAllRoles() throws Exception {
        // ARRANGE
        List<RoleNameDTO> roleNameDTOs = new ArrayList<>();
        List<PermissionDTO> permissionDTOs = new ArrayList<>();
        PermissionDTO permissionDTO = new PermissionDTO(1L, "PermissionName");
        permissionDTOs.add(permissionDTO);
        RoleNameDTO roleNameDTO = new RoleNameDTO(1L, "RoleName", permissionDTOs);
        roleNameDTOs.add(roleNameDTO);

        Pageable pageable = PageRequest.of(0, 20);
        Response<RoleNameDTO> response = Response.<RoleNameDTO>builder()
                .items(roleNameDTOs)
                .itemsPerPage((long) pageable.getPageSize())
                .currentPage((long) pageable.getPageNumber())
                .totalRecordsQuantity((long) roleNameDTOs.size())
                .build();

        given(roleService.getAllRoles(pageable)).willReturn(response);

        // ACT & ASSERT
        mockMvc.perform(get("/api/roles")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(roleNameDTO.getId()))
                .andExpect(jsonPath("$.items[0].name").value(roleNameDTO.getName()))
                .andExpect(jsonPath("$.items[0].permissions[0].id").value(permissionDTO.getId()))
                .andExpect(jsonPath("$.items[0].permissions[0].name").value(permissionDTO.getName()))
                .andExpect(jsonPath("$.itemsPerPage").value(20))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalRecordsQuantity").value(1));
    }

    @Test
    void shouldGetRoleById() throws Exception {
        // ARRANGE
        Long id = 1L;
        given(roleService.getRoleById(id)).willReturn(role);

        // ACT & ASSERT
        mockMvc.perform(get("/api/roles/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(role.getId()))
                .andExpect(jsonPath("$.name").value(role.getName()));
    }
}
