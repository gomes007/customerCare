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

import com.pg.customercare.model.Permission;
import com.pg.customercare.service.PermissionService;
import com.pg.customercare.util.PaginationUtil;
import com.pg.customercare.util.Response;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    public final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping
    public ResponseEntity<Permission> createPermission(@RequestBody Permission permission) {
        Permission savedPermission = permissionService.savePermission(permission);
        return ResponseEntity.ok(savedPermission);
    }

    @GetMapping
    public ResponseEntity<Response<Permission>> getAllPermissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PaginationUtil.createPageRequest(page, size);
        Response<Permission> permissions = permissionService.getAllPermissions(pageable);
        return ResponseEntity.ok(permissions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Permission> updatePermission(@RequestBody Permission permission, @PathVariable Long id) {
        permission.setId(id);
        Permission updatedPermission = permissionService.updatePermission(permission);
        return ResponseEntity.ok(updatedPermission);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }

}
