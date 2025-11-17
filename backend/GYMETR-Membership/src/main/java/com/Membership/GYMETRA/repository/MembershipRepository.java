package com.Membership.GYMETRA.repository;

import com.Membership.GYMETRA.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Integer> {
    // Puedes agregar consultas personalizadas si lo necesitas
}
