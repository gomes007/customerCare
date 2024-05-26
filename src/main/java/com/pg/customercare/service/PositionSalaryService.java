package com.pg.customercare.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pg.customercare.model.PositionSalary;
import com.pg.customercare.repository.PositionSalaryRepository;

@Service
public class PositionSalaryService {

    private final PositionSalaryRepository positionSalaryRepository;

    public PositionSalaryService(PositionSalaryRepository positionSalaryRepository) {
        this.positionSalaryRepository = positionSalaryRepository;
    }

    public List<PositionSalary> getAllPositionSalaries() {
        return positionSalaryRepository.findAll();
    }

    public PositionSalary getPositionSalaryById(Long id) {
        return positionSalaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PositionSalary not found"));
    }

    public PositionSalary savePositionSalary(PositionSalary positionSalary) {
        return positionSalaryRepository.save(positionSalary);
    }

    public void deletePositionSalary(Long id) {
        positionSalaryRepository.deleteById(id);
    }

    public PositionSalary updatePositionSalary(PositionSalary positionSalary) {
        return positionSalaryRepository.save(positionSalary);
    }

}
