package com.Membership.GYMETRA.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"userMemberships"}) // Excluye la colección para evitar ciclos infinitos
@Entity
@Table(name = "membership")
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer membershipId; // id_membresía

    @Column(nullable = false)
    private String planName; // nombre_plan: Mensual/Trimestral/Anual

    @Column(nullable = false)
    private Integer durationDays; // duración en días

    @Column(nullable = false)
    private BigDecimal price; // precio

    @Column(nullable = false)
    private String status; // available/unavailable

    @Column(columnDefinition = "TEXT")
    private String description; // descripción

    // Relación con UserMembership (uno a muchos)
    @OneToMany(mappedBy = "membership")
    @JsonManagedReference
    private List<UserMembership> userMemberships;
}
