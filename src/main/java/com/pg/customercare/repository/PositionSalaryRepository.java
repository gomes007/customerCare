package com.pg.customercare.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pg.customercare.model.PositionSalary;

public interface PositionSalaryRepository extends JpaRepository<PositionSalary, Long>{
    
}
