package com.Membership.GYMETRA.service;

import com.Membership.GYMETRA.entity.Payment;
import com.Membership.GYMETRA.entity.UserMembership;
import com.Membership.GYMETRA.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentById(Integer id) {
        return paymentRepository.findById(id);
    }

    public List<Payment> getPaymentsByUserMembershipId(Integer userMembershipId) {
        return paymentRepository.findByUserMembershipId(userMembershipId);
    }

    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }
    
    // Método simplificado usando JPA con entidad limpia
    public Payment savePaymentSimplified(
            UserMembership userMembership,
            BigDecimal amount,
            String paymentMethod,
            String transactionReference,
            String paymentStatus) {
        
        // Validaciones previas
        if (userMembership == null || userMembership.getId() == null) {
            throw new IllegalArgumentException("UserMembership no puede ser null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount debe ser mayor que 0");
        }
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("PaymentMethod no puede ser null o vacío");
        }
        if (paymentStatus == null || paymentStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("PaymentStatus no puede ser null o vacío");
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        System.out.println("🔍 CREANDO PAYMENT CON ENTIDAD SIMPLIFICADA:");
        System.out.println("   📋 DATOS VALIDADOS:");
        System.out.println("      - UserMembership ID: " + userMembership.getId());
        System.out.println("      - Amount: " + amount);
        System.out.println("      - Payment Method: " + paymentMethod);
        System.out.println("      - Transaction Reference: " + transactionReference);
        System.out.println("      - Payment Status: " + paymentStatus);
        System.out.println("      - Payment Date: " + now);
        
        // Crear entidad Payment simplificada
        Payment payment = Payment.builder()
            .userMembership(userMembership)
            .amount(amount)
            .paymentMethod(Payment.PaymentMethod.valueOf(paymentMethod.toUpperCase()))
            .transactionReference(transactionReference)
            .paymentStatus(Payment.PaymentStatus.valueOf(paymentStatus.toUpperCase()))
            .paymentDate(now)
            .createdAt(now)
            .updatedAt(now)
            .build();
        
        System.out.println("🚀 GUARDANDO PAYMENT CON JPA...");
        
        try {
            Payment savedPayment = paymentRepository.save(payment);
            
            System.out.println("✅ PAYMENT CREADO EXITOSAMENTE:");
            System.out.println("   📝 Payment ID: " + savedPayment.getId());
            System.out.println("   💰 Amount: " + savedPayment.getAmount());
            System.out.println("   📅 Payment Date: " + savedPayment.getPaymentDate());
            System.out.println("   🔗 Transaction Reference: " + savedPayment.getTransactionReference());
            System.out.println("   ✅ Status: " + savedPayment.getPaymentStatus());
            
            return savedPayment;
            
        } catch (Exception e) {
            System.out.println("❌ ERROR AL GUARDAR PAYMENT:");
            System.out.println("   💥 Error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public Payment updatePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public void deletePayment(Integer id) {
        paymentRepository.deleteById(id);
    }
}
