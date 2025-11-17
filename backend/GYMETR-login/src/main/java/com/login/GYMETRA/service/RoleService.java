package com.login.GYMETRA.service;

import com.login.GYMETRA.dto.RoleRequest;
import com.login.GYMETRA.dto.RoleResponse;
import com.login.GYMETRA.entity.Role;
import com.login.GYMETRA.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    // ==============================================================
    // CREAR ROL
    // ==============================================================
    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        if (roleRepository.findByRoleName(request.getRoleName()).isPresent()) {
            throw new IllegalArgumentException("El rol ya existe");
        }

        Role role = Role.builder()
                .roleName(request.getRoleName())
                .description(request.getDescription())
                .build();

        Role savedRole = roleRepository.save(role);
        return mapToResponse(savedRole);
    }

    // ==============================================================
    // OBTENER TODOS LOS ROLES
    // ==============================================================
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll(Sort.by(Sort.Direction.ASC, "roleId"))
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==============================================================
    // OBTENER ROL POR ID
    // ==============================================================
    public Optional<RoleResponse> getRoleById(Long roleId) {
        return roleRepository.findById(roleId).map(this::mapToResponse);
    }

    // ==============================================================
    // ACTUALIZAR ROL
    // ==============================================================
    @Transactional
    public RoleResponse updateRole(Long roleId, RoleRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));

        // Verificar si el nombre ya está en uso por otro rol
        Optional<Role> existingRole = roleRepository.findByRoleName(request.getRoleName());
        if (existingRole.isPresent() && !existingRole.get().getRoleId().equals(roleId)) {
            throw new IllegalArgumentException("El nombre del rol ya está en uso");
        }

        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());

        Role updatedRole = roleRepository.save(role);
        return mapToResponse(updatedRole);
    }

    // ==============================================================
    // ELIMINAR ROL
    // ==============================================================
    @Transactional
    public boolean deleteRole(Long roleId) {
        try {
            if (roleRepository.existsById(roleId)) {
                roleRepository.deleteById(roleId);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // ==============================================================
    // UTILIDADES
    // ==============================================================
    private RoleResponse mapToResponse(Role role) {
        return RoleResponse.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .build();
    }
}