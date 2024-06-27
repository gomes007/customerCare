package com.pg.customercare.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pg.customercare.exception.impl.NotFoundException;
import com.pg.customercare.model.Permission;
import com.pg.customercare.repository.PermissionRepository;
import com.pg.customercare.repository.RoleRepository;
import com.pg.customercare.util.Response;

import jakarta.transaction.Transactional;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public PermissionService(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new NotFoundException("Permission not found with id " + id);
        }
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Permission not found with id " + id));
        boolean isPermissionAssignedToRole = roleRepository.existsByPermissionsContaining(permission);
        if (isPermissionAssignedToRole) {
            throw new IllegalStateException("Cannot delete permission assigned to a role.");
        }
        permissionRepository.deleteById(id);
    }

    public Permission updatePermission(Permission permission) {
        if (!permissionRepository.existsById(permission.getId())) {
            throw new NotFoundException("Permission not found with id " + permission.getId());
        }
        return permissionRepository.save(permission);
    }

    public Permission savePermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    public Permission getPermissionById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Permission not found with id " + id));
    }

    public Response<Permission> getAllPermissions(Pageable pageable) {
        Page<Permission> page = permissionRepository.findAll(pageable);

        // Transforming to ensure that the return is consistent with the expected
        // structure
        List<Permission> permissions = page.getContent().stream().map(permission -> {
            Permission transformedPermission = new Permission();
            transformedPermission.setId(permission.getId());
            transformedPermission.setName(permission.getName());

            // Removing the 'roles' structure to keep the response as needed
            transformedPermission.setRoles(Collections.emptySet());
            return transformedPermission;
        }).collect(Collectors.toList());

        return Response.<Permission>builder()
                .items(permissions)
                .itemsPerPage((long) pageable.getPageSize())
                .currentPage((long) pageable.getPageNumber())
                .totalRecordsQuantity(page.getTotalElements())
                .build();
    }

}
