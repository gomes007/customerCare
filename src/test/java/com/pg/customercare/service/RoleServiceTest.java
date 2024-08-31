package com.pg.customercare.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.pg.customercare.dto.RoleNameDTO;
import com.pg.customercare.exception.impl.NotFoundException;
import com.pg.customercare.model.Permission;
import com.pg.customercare.model.Role;
import com.pg.customercare.repository.RoleRepository;
import com.pg.customercare.util.Response;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @InjectMocks
    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private Role role;

    @Captor
    private ArgumentCaptor<Role> roleCaptor;

    @SuppressWarnings("unused")
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
    }

    @Test
    void shouldCreateRole() {
        // ARRANGE
        given(roleRepository.save(role)).willReturn(role);

        // ACT
        Role roleCreated = roleService.saveRole(role);

        // ASSERT
        assertNotNull(roleCreated);
        assertEquals("ROLE_USER", roleCreated.getName());
        then(roleRepository).should().save(roleCaptor.capture());
        Role capturedRole = roleCaptor.getValue();
        assertEquals("ROLE_USER", capturedRole.getName());
    }

    @Test
    void shouldFindRoleById() {
        // ARRANGE
        Long roleId = 1L;
        given(roleRepository.findById(roleId)).willReturn(Optional.of(role));

        // ACT
        Role roleFound = roleService.getRoleById(roleId);

        // ASSERT
        assertNotNull(roleFound);
        assertEquals(role, roleFound);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenFindByIdNonExistentRole() {
        // ARRANGE
        Long roleId = 1L;
        given(roleRepository.findById(roleId)).willReturn(Optional.empty());

        // ACT & ASSERT
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            roleService.getRoleById(roleId);
        });
        assertEquals("Role not found with id " + roleId, exception.getMessage());
    }

    @Test
    void shouldGetAllRoles() {
        // ARRANGE
        List<Role> roles = new ArrayList<>();
        Role role = new Role();
        role.setId(1L);
        role.setName("RoleName");

        Set<Permission> permissions = new HashSet<>();
        Permission permission = new Permission();
        permission.setId(1L);
        permission.setName("PermissionName");
        permissions.add(permission);

        role.setPermissions(permissions);
        roles.add(role);

        Pageable pageable = PageRequest.of(0, 20);
        Page<Role> rolePage = new PageImpl<>(roles, pageable, roles.size());

        given(roleRepository.findAll(pageable)).willReturn(rolePage);

        // ACT
        Response<RoleNameDTO> response = roleService.getAllRoles(pageable);

        // ASSERT
        assertEquals(1, response.getItems().size());
        assertEquals(role.getId(), response.getItems().get(0).getId());
        assertEquals(role.getName(), response.getItems().get(0).getName());
        assertEquals(1, response.getItems().get(0).getPermissions().size());
        assertEquals(permission.getId(), response.getItems().get(0).getPermissions().get(0).getId());
        assertEquals(permission.getName(), response.getItems().get(0).getPermissions().get(0).getName());
        assertEquals(roles.size(), response.getTotalRecordsQuantity());
        assertEquals(pageable.getPageSize(), response.getItemsPerPage());
        assertEquals(pageable.getPageNumber(), response.getCurrentPage());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdateNonExistentRole() {
        // ARRANGE
        given(roleRepository.existsById(role.getId())).willReturn(false);

        // ACT & ASSERT
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            roleService.updateRole(role);
        });
        assertEquals("Role not found with id " + role.getId(), exception.getMessage());
    }

    @Test
    void shouldDeleteRole() {
        // ARRANGE
        Long roleId = 1L;
        given(roleRepository.existsById(roleId)).willReturn(true);

        // ACT
        roleService.deleteRole(roleId);

        // ASSERT
        then(roleRepository).should().deleteById(roleId);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenDeleteNonExistentRole() {
        // ARRANGE
        Long roleId = 1L;
        given(roleRepository.existsById(roleId)).willReturn(false);

        // ACT & ASSERT
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            roleService.deleteRole(roleId);
        });
        assertEquals("Role not found with id " + roleId, exception.getMessage());
    }

}
