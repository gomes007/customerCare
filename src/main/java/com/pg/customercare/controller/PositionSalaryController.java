package com.pg.customercare.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pg.customercare.model.PositionSalary;
import com.pg.customercare.service.PositionSalaryService;

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
    public ResponseEntity<List<PositionSalary>> getAllPositionSalaries() {
        List<PositionSalary> positionSalaries = positionSalaryService.getAllPositionSalaries();
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

    @PostMapping("/{id}")
    public ResponseEntity<PositionSalary> updatePositionSalary(@RequestBody PositionSalary positionSalary) {
        PositionSalary updatedPositionSalary = positionSalaryService.updatePositionSalary(positionSalary);
        return ResponseEntity.ok(updatedPositionSalary);
    }


    
}
