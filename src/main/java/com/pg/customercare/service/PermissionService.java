package com.pg.customercare.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pg.customercare.exception.impl.NotFoundException;
import com.pg.customercare.model.Permission;
import com.pg.customercare.repository.PermissionRepository;
import com.pg.customercare.util.Response;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public void deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new NotFoundException("Permission not found with id " + id);
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

        return Response.<Permission>builder()
                .items(page.getContent())
                .itemsPerPage((long) pageable.getPageSize())
                .currentPage((long) pageable.getPageNumber())
                .totalRecordsQuantity(page.getTotalElements())
                .build();
    }

}
