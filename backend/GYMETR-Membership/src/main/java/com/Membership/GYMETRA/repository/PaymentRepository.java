package com.Membership.GYMETRA.repository;

import com.Membership.GYMETRA.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    List<Payment> findByUserMembershipId(Integer userMembershipId); // Obtener todos los pagos de un UserMembership
}
