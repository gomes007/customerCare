package com.pg.customercare.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import com.pg.customercare.exception.impl.NotFoundException;
import com.pg.customercare.model.Role;
import com.pg.customercare.repository.RoleRepository;

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
        roles.add(role);
        given(roleRepository.findAll()).willReturn(roles);

        // ACT
        List<Role> rolesFound = roleService.getAllRoles();

        // ASSERT
        assertEquals(1, rolesFound.size());
        assertEquals(role, rolesFound.get(0));
    }

    @Test
    void shouldUpdateRole() {
        // ARRANGE
        given(roleRepository.existsById(role.getId())).willReturn(true);
        given(roleRepository.save(role)).willReturn(role);

        // ACT
        Role roleUpdated = roleService.updateRole(role);

        // ASSERT
        assertNotNull(roleUpdated);
        assertEquals("ROLE_USER", roleUpdated.getName());
        then(roleRepository).should().save(roleCaptor.capture());
        Role capturedRole = roleCaptor.getValue();
        assertEquals("ROLE_USER", capturedRole.getName());
    }

}
