package com.Membership.GYMETRA.service;

import com.Membership.GYMETRA.entity.Membership;
import com.Membership.GYMETRA.entity.UserMembership;
import com.Membership.GYMETRA.repository.MembershipRepository;
import com.Membership.GYMETRA.repository.UserMembershipRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final UserMembershipRepository userMembershipRepository;

    public MembershipService(MembershipRepository membershipRepository, UserMembershipRepository userMembershipRepository) {
        this.membershipRepository = membershipRepository;
        this.userMembershipRepository = userMembershipRepository;
    }

    // Listar todas las membresías
    public List<Membership> getAllMemberships() {
        return membershipRepository.findAll();
    }

    // Obtener una membresía por ID
    public Optional<Membership> getMembershipById(Integer id) {
        return membershipRepository.findById(id);
    }

    // Crear o actualizar una membresía
    public Membership saveMembership(Membership membership) {
        return membershipRepository.save(membership);
    }

    // Eliminar una membresía
    public void deleteMembership(Integer id) {
        membershipRepository.deleteById(id);
    }

    // Obtener todas las membresías de usuario
    public List<UserMembership> getAllUserMemberships() {
        return userMembershipRepository.findAll();
    }
}
