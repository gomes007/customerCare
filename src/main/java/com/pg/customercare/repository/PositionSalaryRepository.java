package com.pg.customercare.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pg.customercare.model.PositionSalary;

public interface PositionSalaryRepository extends JpaRepository<PositionSalary, Long> {

    Page<PositionSalary> findByPositionContainingIgnoreCase(String position, Pageable pageable);

    @Query("SELECT ps FROM PositionSalary ps JOIN ps.role r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :roleName, '%'))")
    Page<PositionSalary> findByRoleName(@Param("roleName") String roleName, Pageable pageable);
}
