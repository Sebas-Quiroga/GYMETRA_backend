package com.Membership.GYMETRA.repository;

import com.Membership.GYMETRA.entity.UserMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserMembershipRepository extends JpaRepository<UserMembership, Integer> {

    // Obtener todas las memberships de un usuario
    List<UserMembership> findByUserId(Integer userId);

    // Obtener todas las memberships de un usuario ordenadas por fecha de fin descendente
    List<UserMembership> findByUserIdOrderByEndDateDesc(Integer userId);

    // Comprobar si existe una membresía pendiente (u otro estado) para un usuario
    boolean existsByUserIdAndStatus(Integer userId, UserMembership.Status status);

    // Encontrar todas las membresías con un estado específico creadas antes de cierta fecha (para limpiar PENDING)
    List<UserMembership> findByStatusAndCreatedAtBefore(UserMembership.Status status, LocalDateTime dateTime);
}
