package com.pg.customercare.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pg.customercare.model.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long>{

    
}
