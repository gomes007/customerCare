package com.pg.customercare.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pg.customercare.dto.PositionSalaryDTO;
import com.pg.customercare.model.PositionSalary;
import com.pg.customercare.service.PositionSalaryService;
import com.pg.customercare.util.PaginationUtil;
import com.pg.customercare.util.Response;

@RestController
@RequestMapping("/api/position-salaries")
public class PositionSalaryController {

    private final PositionSalaryService positionSalaryService;

    public PositionSalaryController(PositionSalaryService positionSalaryService) {
        this.positionSalaryService = positionSalaryService;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePositionSalary(@PathVariable Long id) {
        positionSalaryService.deletePositionSalary(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Response<PositionSalaryDTO>> getAllPositionSalaries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PaginationUtil.createPageRequest(page, size);
        Response<PositionSalaryDTO> positionSalaries = positionSalaryService.getAllPositionSalaries(pageable);
        return ResponseEntity.ok(positionSalaries);
    }

    @GetMapping("/search/by-position")
    public ResponseEntity<Response<PositionSalaryDTO>> getPositionSalariesByPosition(
            @RequestParam String position,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PaginationUtil.createPageRequest(page, size);
        Response<PositionSalaryDTO> positionSalaries = positionSalaryService.getPositionSalariesByPosition(position,
                pageable);
        return ResponseEntity.ok(positionSalaries);
    }

    @GetMapping("/search/by-role")
    public ResponseEntity<Response<PositionSalaryDTO>> getPositionSalariesByRoleName(
            @RequestParam String roleName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PaginationUtil.createPageRequest(page, size);
        Response<PositionSalaryDTO> positionSalaries = positionSalaryService.getPositionSalariesByRoleName(roleName,
                pageable);
        return ResponseEntity.ok(positionSalaries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PositionSalary> getPositionSalaryById(@PathVariable Long id) {
        PositionSalary positionSalary = positionSalaryService.getPositionSalaryById(id);
        return ResponseEntity.ok(positionSalary);
    }

    @PostMapping
    public ResponseEntity<PositionSalary> createPositionSalary(@RequestBody PositionSalary positionSalary) {
        PositionSalary savedPositionSalary = positionSalaryService.savePositionSalary(positionSalary);
        return ResponseEntity.ok(savedPositionSalary);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PositionSalary> updatePositionSalary(@RequestBody PositionSalary positionSalary) {
        PositionSalary updatedPositionSalary = positionSalaryService.updatePositionSalary(positionSalary);
        return ResponseEntity.ok(updatedPositionSalary);
    }

}
