package com.pg.customercare.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pg.customercare.model.Permission;
import com.pg.customercare.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END FROM Role r JOIN r.permissions p WHERE p = :permission")
    boolean existsByPermissionsContaining(@Param("permission") Permission permission);

    Page<Role> findByNameContainingIgnoreCase(String name, Pageable pageable);

    
    @Query("SELECT r FROM Role r JOIN r.permissions p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :permissionName, '%'))")
    Page<Role> findByPermissionName(@Param("permissionName") String permissionName, Pageable pageable);

}