package com.Membership.GYMETRA.controller;

import com.Membership.GYMETRA.entity.Membership;
import com.Membership.GYMETRA.entity.UserMembership;
import com.Membership.GYMETRA.service.MembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Membresías", description = "Controlador para gestionar membresías disponibles")
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    // Listar todas las membresías
    @Operation(summary = "Listar membresías", description = "Obtiene una lista de todas las membresías disponibles")
    @GetMapping("/memberships")
    public ResponseEntity<List<Membership>> getAllMemberships() {
        List<Membership> memberships = membershipService.getAllMemberships();
        return ResponseEntity.ok(memberships);
    }

    // Endpoint específico para obtener membresías disponibles (alias)
    @Operation(summary = "Listar membresías disponibles", description = "Obtiene una lista de membresías disponibles para compra")
    @GetMapping("/memberships/available")
    public ResponseEntity<List<Membership>> getAvailableMemberships() {
        List<Membership> memberships = membershipService.getAllMemberships();
        return ResponseEntity.ok(memberships);
    }

    // Endpoint para obtener todas las membresías de usuario
    @Operation(summary = "Listar todas las membresías de usuario", description = "Obtiene una lista completa de todas las membresías de usuario en el sistema")
    @GetMapping("/user-memberships/all")
    public ResponseEntity<List<UserMembership>> getAllUserMemberships() {
        List<UserMembership> userMemberships = membershipService.getAllUserMemberships();
        return ResponseEntity.ok(userMemberships);
    }

    // Obtener membresía por ID
    @Operation(summary = "Obtener membresía por ID", description = "Obtiene los detalles de una membresía específica por su identificador")
    @GetMapping("/memberships/{id}")
    public ResponseEntity<Membership> getMembershipById(@PathVariable Integer id) {
        return membershipService.getMembershipById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear nueva membresía
    @Operation(summary = "Crear membresía", description = "Crea una nueva membresía en el sistema")
    @PostMapping("/memberships")
    public ResponseEntity<Membership> createMembership(@RequestBody Membership membership) {
        Membership saved = membershipService.saveMembership(membership);
        return ResponseEntity.ok(saved);
    }

    // Actualizar membresía
    @Operation(summary = "Actualizar membresía", description = "Actualiza los datos de una membresía existente")
    @PutMapping("/memberships/{id}")
    public ResponseEntity<Membership> updateMembership(@PathVariable Integer id, @RequestBody Membership membership) {
        return membershipService.getMembershipById(id)
                .map(existing -> {
                    membership.setMembershipId(existing.getMembershipId());
                    return ResponseEntity.ok(membershipService.saveMembership(membership));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar membresía
    @Operation(summary = "Eliminar membresía", description = "Elimina una membresía del sistema por su ID")
    @DeleteMapping("/memberships/{id}")
    public ResponseEntity<Void> deleteMembership(@PathVariable Integer id) {
        membershipService.deleteMembership(id);
        return ResponseEntity.noContent().build();
    }
}
