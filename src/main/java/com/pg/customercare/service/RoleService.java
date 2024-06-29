package com.pg.customercare.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pg.customercare.dto.PermissionDTO;
import com.pg.customercare.dto.RoleNameDTO;
import com.pg.customercare.exception.impl.NotFoundException;
import com.pg.customercare.model.Permission;
import com.pg.customercare.model.Role;
import com.pg.customercare.repository.PermissionRepository;
import com.pg.customercare.repository.RoleRepository;
import com.pg.customercare.util.Response;

import jakarta.transaction.Transactional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public Response<RoleNameDTO> getAllRoles(Pageable pageable) {
        Page<Role> page = roleRepository.findAll(pageable);

        List<RoleNameDTO> roles = page.getContent().stream()
                .map(role -> {
                    List<PermissionDTO> permissions = role.getPermissions().stream()
                            .map(permission -> new PermissionDTO(permission.getId(), permission.getName()))
                            .collect(Collectors.toList());
                    return new RoleNameDTO(role.getId(), role.getName(), permissions);
                })
                .collect(Collectors.toList());

        return Response.<RoleNameDTO>builder()
                .items(roles)
                .itemsPerPage((long) pageable.getPageSize())
                .currentPage((long) pageable.getPageNumber())
                .totalRecordsQuantity(page.getTotalElements())
                .build();
    }

    public Response<RoleNameDTO> getRolesByName(String name, Pageable pageable) {
        Page<Role> page = roleRepository.findByNameContainingIgnoreCase(name, pageable);
        return convertPageToResponse(page, pageable);
    }

    public Response<RoleNameDTO> getRolesByPermissionName(String permissionName, Pageable pageable) {
        Page<Role> page = roleRepository.findByPermissionName(permissionName, pageable);
        return convertPageToResponse(page, pageable);
    }

    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role not found with id " + id));
    }

    @Transactional
    public Role saveRole(Role role) {

        if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
            // Extract the permission IDs from the `Role` object
            List<Long> permissionIds = role.getPermissions().stream()
                    .map(Permission::getId)
                    .toList();
            // Retrieve the permissions based on the IDs
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
            // Set the permissions in the `Role` object
            role.setPermissions(permissions);
        } else {
            // If no permissions are provided, initialize as an empty set
            role.setPermissions(new HashSet<>());
        }

        return roleRepository.save(role);
    }

    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new NotFoundException("Role not found with id " + id);
        }
        roleRepository.deleteById(id);
    }

    @Transactional
    public Role updateRole(Role role) {
        Role existingRole = roleRepository.findById(role.getId())
                .orElseThrow(() -> new NotFoundException("Role not found with id " + role.getId()));

        // Update the name
        existingRole.setName(role.getName());

        // Clear the existing permissions and add the new ones
        existingRole.getPermissions().clear();
        if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
            List<Long> permissionIds = role.getPermissions().stream()
                    .map(Permission::getId)
                    .toList();
            Set<Permission> newPermissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
            existingRole.getPermissions().addAll(newPermissions);
        }

        return roleRepository.save(existingRole);
    }

    // auxiliary method
    private Response<RoleNameDTO> convertPageToResponse(Page<Role> page, Pageable pageable) {
        List<RoleNameDTO> roles = page.getContent().stream()
                .map(role -> {
                    List<PermissionDTO> permissions = role.getPermissions().stream()
                            .map(permission -> new PermissionDTO(permission.getId(), permission.getName()))
                            .collect(Collectors.toList());
                    return new RoleNameDTO(role.getId(), role.getName(), permissions);
                })
                .collect(Collectors.toList());

        return Response.<RoleNameDTO>builder()
                .items(roles)
                .itemsPerPage((long) pageable.getPageSize())
                .currentPage((long) pageable.getPageNumber())
                .totalRecordsQuantity(page.getTotalElements())
                .build();
    }

}
