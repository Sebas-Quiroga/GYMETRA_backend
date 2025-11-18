package com.Membership.GYMETRA.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"userMembership"})
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_membership_id", nullable = false)
    @JsonIgnore
    private UserMembership userMembership;

    // Campo adicional para exponer el userId en JSON sin cargar toda la relación
    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("userId")
    public Integer getUserId() {
        return userMembership != null ? userMembership.getUserId() : null;
    }

    // Campo adicional para exponer el membershipId en JSON sin cargar toda la relación
    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("membershipId")
    public Integer getMembershipId() {
        return userMembership != null ? userMembership.getMembership().getMembershipId() : null;
    }

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal amount;

    // Campo duplicado legacy para compatibilidad con base de datos existente
    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    // Campo duplicado legacy para compatibilidad con base de datos existente
    @Column(name = "metodo_pago", nullable = false, length = 20)
    private String metodoPago;

    @Column(name = "transaction_reference", length = 255)
    private String transactionReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    // Campo duplicado legacy para compatibilidad con base de datos existente
    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum PaymentMethod { 
        CASH, CARD, GATEWAY 
    }

    public enum PaymentStatus { 
        PENDING, CONFIRMED, FAILED 
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (paymentDate == null) {
            paymentDate = now;
        }
        
        // Sincronizar campos duplicados para compatibilidad legacy
        if (amount != null) {
            monto = amount;
        }
        if (paymentDate != null) {
            fechaPago = paymentDate;
        }
        if (paymentMethod != null) {
            metodoPago = paymentMethod.name();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
