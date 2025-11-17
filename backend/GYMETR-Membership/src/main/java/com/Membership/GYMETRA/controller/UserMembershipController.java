package com.Membership.GYMETRA.controller;

import com.Membership.GYMETRA.entity.UserMembership;
import com.Membership.GYMETRA.service.UserMembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-memberships")
@Tag(name = "Membresías de Usuario", description = "Controlador para gestionar las membresías activas de los usuarios")
public class UserMembershipController {

    private final UserMembershipService userMembershipService;

    public UserMembershipController(UserMembershipService userMembershipService) {
        this.userMembershipService = userMembershipService;
    }

    // =====================================
    // 1️⃣ Crear o actualizar membresía
    // =====================================
    @Operation(summary = "Crear o actualizar membresía de usuario", description = "Crea una nueva membresía de usuario o actualiza una existente")
    @PostMapping
    public ResponseEntity<UserMembership> createOrUpdateMembership(@RequestBody UserMembership userMembership) {
        try {
            UserMembership saved = userMembershipService.createOrUpdateMembership(userMembership);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // =====================================
    // 2️⃣ Activar membresía
    // =====================================
    @Operation(summary = "Activar membresía", description = "Activa una membresía de usuario por su ID")
    @PutMapping("/{id}/activate")
    public ResponseEntity<UserMembership> activateMembership(@PathVariable Integer id) {
        return updateMembershipStatus(id, UserMembership.Status.ACTIVE);
    }

    // =====================================
    // 3️⃣ Suspender membresía
    // =====================================
    @Operation(summary = "Suspender membresía", description = "Suspende una membresía de usuario por su ID")
    @PutMapping("/{id}/suspend")
    public ResponseEntity<UserMembership> suspendMembership(@PathVariable Integer id) {
        return updateMembershipStatus(id, UserMembership.Status.SUSPENDED);
    }

    // =====================================
    // 4️⃣ Cancelar membresía
    // =====================================
    @Operation(summary = "Cancelar membresía", description = "Cancela una membresía de usuario por su ID")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<UserMembership> cancelMembership(@PathVariable Integer id) {
        return updateMembershipStatus(id, UserMembership.Status.CANCELED);
    }

    // =====================================
    // Método interno para actualizar status
    // =====================================
    private ResponseEntity<UserMembership> updateMembershipStatus(Integer id, UserMembership.Status status) {
        return userMembershipService.getUserMembershipById(id)
                .map(membership -> {
                    membership.setStatus(status);
                    UserMembership updated = userMembershipService.createOrUpdateMembership(membership);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // =====================================
    // 5️⃣ Listar todas las membresías de un usuario
    // =====================================
    @Operation(summary = "Listar membresías de usuario", description = "Obtiene todas las membresías asociadas a un usuario específico")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserMembership>> getMembershipsByUser(@PathVariable Integer userId) {
        List<UserMembership> memberships = userMembershipService.getUserMembershipsByUserId(userId);
        return ResponseEntity.ok(memberships);
    }

    // =====================================
    // 6️⃣ Obtener días restantes de la última membresía activa
    // =====================================
    @Operation(summary = "Obtener días restantes", description = "Calcula los días restantes de la membresía activa más reciente de un usuario")
    @GetMapping("/user/{userId}/remaining-days")
    public ResponseEntity<Long> getRemainingDays(@PathVariable Integer userId) {
        return userMembershipService.getLatestActiveMembership(userId)
                .map(membership -> ResponseEntity.ok(userMembershipService.getRemainingDays(membership)))
                .orElse(ResponseEntity.notFound().build());
    }

    // =====================================
    // 7️⃣ Tarea programada para eliminar pendientes > 5 minutos
    // =====================================
    @Scheduled(fixedRate = 60000) // Cada 1 minuto
    public void cleanupPendingMemberships() {
        userMembershipService.deletePendingOlderThanMinutes(5);
    }
}
