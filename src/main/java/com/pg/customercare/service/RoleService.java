package com.pg.customercare.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.pg.customercare.exception.impl.NotFoundException;
import com.pg.customercare.model.Role;
import com.pg.customercare.repository.RoleRepository;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role not found with id " + id));
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new NotFoundException("Role not found with id " + id);
        }
        roleRepository.deleteById(id);
    }

    public Role updateRole(Role role) {
        if (!roleRepository.existsById(role.getId())) {
            throw new NotFoundException("Role not found with id " + role.getId());
        }
        return roleRepository.save(role);
    }

}
