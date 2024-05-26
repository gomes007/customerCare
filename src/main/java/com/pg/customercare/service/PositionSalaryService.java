package com.pg.customercare.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pg.customercare.exception.impl.NotFoundException;
import com.pg.customercare.exception.impl.ValidationException;
import com.pg.customercare.model.PositionSalary;
import com.pg.customercare.model.Role;
import com.pg.customercare.repository.PositionSalaryRepository;
import com.pg.customercare.repository.RoleRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PositionSalaryService {

    private final PositionSalaryRepository positionSalaryRepository;
    private final RoleRepository roleRepository;

    public PositionSalaryService(PositionSalaryRepository positionSalaryRepository, RoleRepository roleRepository) {
        this.positionSalaryRepository = positionSalaryRepository;
        this.roleRepository = roleRepository;
    }

    public List<PositionSalary> getAllPositionSalaries() {
        return positionSalaryRepository.findAll();
    }

    public PositionSalary getPositionSalaryById(Long id) {
        return positionSalaryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Position not found with id " + id));
    }

    public PositionSalary savePositionSalary(PositionSalary positionSalary) {
        if (positionSalary.getRole() == null) {
            throw new ValidationException("Role is required", new HashMap<>());
        }

        Role role = positionSalary.getRole();
        if (role.getId() == null) {
            // If Role does not have an ID, persist it first
            role = roleRepository.save(role);
        } else {
            // If Role already has an ID, merge it to ensure it is managed
            role = roleRepository.findById(role.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        }
        positionSalary.setRole(role);

        return positionSalaryRepository.save(positionSalary);
    }

    public void deletePositionSalary(Long id) {
        if (!positionSalaryRepository.existsById(id)) {
            throw new NotFoundException("Position not found with id " + id);
        }
        positionSalaryRepository.deleteById(id);
    }

    public PositionSalary updatePositionSalary(PositionSalary positionSalary) {
        if (!positionSalaryRepository.existsById(positionSalary.getId())) {
            throw new NotFoundException("Position not found with id " + positionSalary.getId());
        }
        if (positionSalary.getRole() == null) {
            throw new ValidationException("Role is required", new HashMap<>());
        }

        Role role = positionSalary.getRole();
        if (role.getId() == null) {
            // If Role does not have an ID, persist it first
            role = roleRepository.save(role);
        } else {
            // If Role already has an ID, ensure it exists
            role = roleRepository.findById(role.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        }
        positionSalary.setRole(role);

        return positionSalaryRepository.save(positionSalary);
    }

}
