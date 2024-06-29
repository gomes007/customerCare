package com.pg.customercare.service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pg.customercare.dto.PositionSalaryDTO;
import com.pg.customercare.exception.impl.NotFoundException;
import com.pg.customercare.exception.impl.ValidationException;
import com.pg.customercare.model.PositionSalary;
import com.pg.customercare.model.Role;
import com.pg.customercare.repository.PositionSalaryRepository;
import com.pg.customercare.repository.RoleRepository;
import com.pg.customercare.util.Response;

import jakarta.transaction.Transactional;

@Service
public class PositionSalaryService {

    private final PositionSalaryRepository positionSalaryRepository;
    private final RoleRepository roleRepository;

    public PositionSalaryService(PositionSalaryRepository positionSalaryRepository, RoleRepository roleRepository) {
        this.positionSalaryRepository = positionSalaryRepository;
        this.roleRepository = roleRepository;
    }

    public Response<PositionSalaryDTO> getAllPositionSalaries(Pageable pageable) {
        Page<PositionSalary> page = positionSalaryRepository.findAll(pageable);
        return convertPageToResponse(page, pageable);
    }

    public Response<PositionSalaryDTO> getPositionSalariesByPosition(String position, Pageable pageable) {
        Page<PositionSalary> page = positionSalaryRepository.findByPositionContainingIgnoreCase(position, pageable);
        return convertPageToResponse(page, pageable);
    }

    public Response<PositionSalaryDTO> getPositionSalariesByRoleName(String roleName, Pageable pageable) {
        Page<PositionSalary> page = positionSalaryRepository.findByRoleName(roleName, pageable);
        return convertPageToResponse(page, pageable);
    }

    public PositionSalary getPositionSalaryById(Long id) {
        return positionSalaryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Position Salary not found with id " + id));
    }

    @Transactional
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
                    .orElseThrow(() -> new NotFoundException("Role not found"));
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

    @Transactional
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
                    .orElseThrow(() -> new NotFoundException("Role not found"));
        }
        positionSalary.setRole(role);

        return positionSalaryRepository.save(positionSalary);
    }

    // auxiliary methods
    private Response<PositionSalaryDTO> convertPageToResponse(Page<PositionSalary> page, Pageable pageable) {
        List<PositionSalaryDTO> positionSalaries = page.getContent().stream()
                .map(positionSalary -> new PositionSalaryDTO(
                        positionSalary.getId(),
                        positionSalary.getPosition(),
                        positionSalary.getSalary(),
                        positionSalary.getCommission(),
                        positionSalary.getRole().getName()))
                .collect(Collectors.toList());

        return Response.<PositionSalaryDTO>builder()
                .items(positionSalaries)
                .itemsPerPage((long) pageable.getPageSize())
                .currentPage((long) pageable.getPageNumber())
                .totalRecordsQuantity(page.getTotalElements())
                .build();
    }

}
