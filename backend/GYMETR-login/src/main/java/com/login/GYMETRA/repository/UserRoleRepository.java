package com.login.GYMETRA.repository;

import com.login.GYMETRA.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    // Buscar todos los roles de un usuario
    List<UserRole> findByUser_UserId(Long userId);

    // Buscar si un usuario tiene un rol específico
    Optional<UserRole> findByUser_UserIdAndRole_RoleName(Long userId, String roleName);
}
