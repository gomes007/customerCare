package com.pg.customercare.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pg.customercare.exception.impl.NotFoundException;
import com.pg.customercare.exception.impl.ValidationException;
import com.pg.customercare.model.PositionSalary;
import com.pg.customercare.model.Role;
import com.pg.customercare.repository.PositionSalaryRepository;
import com.pg.customercare.repository.RoleRepository;

@ExtendWith(MockitoExtension.class)
public class PositionSalaryServiceTest {

    @InjectMocks
    private PositionSalaryService positionSalaryService;

    @Mock
    private PositionSalaryRepository positionSalaryRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PositionSalary positionSalary;

    @Mock
    private Role role;

    @Captor
    private ArgumentCaptor<PositionSalary> positionSalaryCaptor;

    @Captor
    private ArgumentCaptor<Role> roleCaptor;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");

        positionSalary = new PositionSalary();
        positionSalary.setId(1L);
        positionSalary.setRole(role);
        positionSalary.setSalary(50000.00);
    }

    @Test
    void shouldGetAllPositionSalaries() {
        // ARRANGE
        List<PositionSalary> positionSalaries = new ArrayList<>();
        positionSalaries.add(positionSalary);
        given(positionSalaryRepository.findAll()).willReturn(positionSalaries);

        // ACT
        List<PositionSalary> result = positionSalaryService.getAllPositionSalaries();

        // ASSERT
        assertEquals(1, result.size());
        assertEquals(positionSalary, result.get(0));
    }

    @Test
    void shouldGetPositionSalaryById() {
        // ARRANGE
        Long id = 1L;
        given(positionSalaryRepository.findById(id)).willReturn(Optional.of(positionSalary));

        // ACT
        PositionSalary result = positionSalaryService.getPositionSalaryById(id);

        // ASSERT
        assertNotNull(result);
        assertEquals(positionSalary, result);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenGetPositionSalaryByIdNotFound() {
        // ARRANGE
        Long id = 1L;
        given(positionSalaryRepository.findById(id)).willReturn(Optional.empty());

        // ACT & ASSERT
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            positionSalaryService.getPositionSalaryById(id);
        });
        assertEquals("Position not found with id " + id, exception.getMessage());
    }

    @Test
    void shouldSavePositionSalaryWithNewRole() {
        // ARRANGE
        positionSalary.setRole(null);
        Role newRole = new Role();
        newRole.setName("ROLE_ADMIN");
        positionSalary.setRole(newRole);

        given(roleRepository.save(newRole)).willReturn(newRole);
        given(positionSalaryRepository.save(positionSalary)).willReturn(positionSalary);

        // ACT
        PositionSalary result = positionSalaryService.savePositionSalary(positionSalary);

        // ASSERT
        assertNotNull(result);
        assertEquals(newRole, result.getRole());
        then(roleRepository).should().save(roleCaptor.capture());
        assertEquals("ROLE_ADMIN", roleCaptor.getValue().getName());
        then(positionSalaryRepository).should().save(positionSalaryCaptor.capture());
        assertEquals(positionSalary, positionSalaryCaptor.getValue());
    }

    @Test
    void shouldSavePositionSalaryWithExistingRole() {
        // ARRANGE
        given(roleRepository.findById(role.getId())).willReturn(Optional.of(role));
        given(positionSalaryRepository.save(positionSalary)).willReturn(positionSalary);

        // ACT
        PositionSalary result = positionSalaryService.savePositionSalary(positionSalary);

        // ASSERT
        assertNotNull(result);
        assertEquals(role, result.getRole());
        then(positionSalaryRepository).should().save(positionSalaryCaptor.capture());
        assertEquals(positionSalary, positionSalaryCaptor.getValue());
    }

    @Test
    void shouldThrowValidationExceptionWhenSavingPositionSalaryWithoutRole() {
        // ARRANGE
        positionSalary.setRole(null);

        // ACT & ASSERT
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            positionSalaryService.savePositionSalary(positionSalary);
        });
        assertEquals("Role is required", exception.getMessage());
    }

    @Test
    void shouldDeletePositionSalary() {
        // ARRANGE
        Long id = 1L;
        given(positionSalaryRepository.existsById(id)).willReturn(true);

        // ACT
        positionSalaryService.deletePositionSalary(id);

        // ASSERT
        then(positionSalaryRepository).should().deleteById(id);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenDeletingNonExistentPositionSalary() {
        // ARRANGE
        Long id = 1L;
        given(positionSalaryRepository.existsById(id)).willReturn(false);

        // ACT & ASSERT
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            positionSalaryService.deletePositionSalary(id);
        });
        assertEquals("Position not found with id " + id, exception.getMessage());
    }

    @Test
    void shouldUpdatePositionSalary() {
        // ARRANGE
        given(positionSalaryRepository.existsById(positionSalary.getId())).willReturn(true);
        given(roleRepository.findById(role.getId())).willReturn(Optional.of(role));
        given(positionSalaryRepository.save(positionSalary)).willReturn(positionSalary);

        // ACT
        PositionSalary result = positionSalaryService.updatePositionSalary(positionSalary);

        // ASSERT
        assertNotNull(result);
        assertEquals(positionSalary, result);
        then(positionSalaryRepository).should().save(positionSalaryCaptor.capture());
        assertEquals(positionSalary, positionSalaryCaptor.getValue());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdatingNonExistentPositionSalary() {
        // ARRANGE
        given(positionSalaryRepository.existsById(positionSalary.getId())).willReturn(false);

        // ACT & ASSERT
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            positionSalaryService.updatePositionSalary(positionSalary);
        });
        assertEquals("Position not found with id " + positionSalary.getId(), exception.getMessage());
    }

    @Test
    void shouldThrowValidationExceptionWhenUpdatingPositionSalaryWithoutRole() {
        // ARRANGE
        positionSalary.setRole(null);
        given(positionSalaryRepository.existsById(positionSalary.getId())).willReturn(true);

        // ACT & ASSERT
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            positionSalaryService.updatePositionSalary(positionSalary);
        });

        // ASSERT
        assertEquals("Role is required", exception.getMessage());
    }

}
