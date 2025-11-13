package com.Membership.GYMETRA.config;

import com.Membership.GYMETRA.entity.Membership;
import com.Membership.GYMETRA.repository.MembershipRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MembershipRepository membershipRepository;

    public DataInitializer(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (membershipRepository.findAll().isEmpty()) {
            // Crear membresías por defecto
            Membership mensual = Membership.builder()
                    .planName("Membresía mensual básica")
                    .durationDays(30)
                    .price(new BigDecimal("60000.00"))
                    .status("available")
                    .description("Mensual")
                    .build();

            Membership trimestral = Membership.builder()
                    .planName("Membresía trimestral premium")
                    .durationDays(90)
                    .price(new BigDecimal("160000.00"))
                    .status("available")
                    .description("Trimestral")
                    .build();

            Membership anual = Membership.builder()
                    .planName("Membresía anual completa")
                    .durationDays(365)
                    .price(new BigDecimal("550000.00"))
                    .status("available")
                    .description("Anual")
                    .build();

            membershipRepository.saveAll(List.of(mensual, trimestral, anual));
        }
    }
}