package com.Membership.GYMETRA.controller;

import com.Membership.GYMETRA.client.UserMembershipClient;
import com.Membership.GYMETRA.client.UserMembershipResponse;
import com.Membership.GYMETRA.entity.Membership;
import com.Membership.GYMETRA.entity.UserMembership;
import com.Membership.GYMETRA.entity.Payment;
import com.Membership.GYMETRA.service.MembershipService;
import com.Membership.GYMETRA.service.UserMembershipService;
import com.Membership.GYMETRA.service.PaymentService;
import com.Membership.GYMETRA.service.StripePaymentService;
import com.Membership.GYMETRA.service.EmailService;
import com.stripe.model.PaymentIntent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Pagos", description = "Controlador para gestionar pagos y transacciones")
public class PaymentController {

    private final StripePaymentService stripePaymentService;
    private final UserMembershipClient userMembershipClient;
    private final EmailService emailService;
    private final MembershipService membershipService;
    private final UserMembershipService userMembershipService;
    private final PaymentService paymentService;

    public PaymentController(
            StripePaymentService stripePaymentService,
            UserMembershipClient userMembershipClient,
            EmailService emailService,
            MembershipService membershipService,
            UserMembershipService userMembershipService,
            PaymentService paymentService
    ) {
        this.stripePaymentService = stripePaymentService;
        this.userMembershipClient = userMembershipClient;
        this.emailService = emailService;
        this.membershipService = membershipService;
        this.userMembershipService = userMembershipService;
        this.paymentService = paymentService;
    }

    /**
     * Crear un PaymentIntent en Stripe
     */
    @Operation(summary = "Crear intención de pago", description = "Crea una intención de pago en Stripe para procesar el pago de una membresía")
    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody Map<String, Object> request) {
        try {
            Integer membershipId = (Integer) request.get("membershipId");
            Integer userId = (Integer) request.get("userId");

            // 1. Obtener la membresía para obtener el precio
            Optional<Membership> membershipOpt = membershipService.getMembershipById(membershipId);
            if (membershipOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Membresía no encontrada");
            }
            
            Membership membership = membershipOpt.get();
            BigDecimal price = membership.getPrice();
            BigDecimal amountInCents = price.multiply(BigDecimal.valueOf(100));

            // 2. Crear PaymentIntent en Stripe
            String description = "Membresía " + membership.getPlanName() + " - Usuario ID: " + userId;
            PaymentIntent intent = stripePaymentService.createPaymentIntent(amountInCents, "usd", description);

            // 3. Enviar correo de intento de pago (opcional - solo si podemos obtener info del usuario)
            try {
                UserMembershipResponse userInfo = userMembershipClient.getUserMembershipById(userId);
                if (userInfo != null) {
                    String to = userInfo.getUser().getEmail();
                    String subject = "Intento de pago creado - " + membership.getPlanName();
                    String body = "Hola " + userInfo.getUser().getFirstName() + ",\n\n" +
                            "Se ha generado un intento de pago para la membresía: " + membership.getPlanName() + ".\n" +
                            "Monto: $" + price + " USD.\n\n" +
                            "Una vez se confirme el pago, activaremos tu membresía.";

                    emailService.sendEmail(to, subject, body);
                }
            } catch (Exception emailError) {
                // Log error pero continúa con el proceso
                System.err.println("Error enviando email: " + emailError.getMessage());
            }

            return ResponseEntity.ok(Map.of(
                "clientSecret", intent.getClientSecret(),
                "membershipName", membership.getPlanName(),
                "amount", price
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error al crear el PaymentIntent: " + e.getMessage());
        }
    }

    /**
     * Confirmar el pago y crear la membresía del usuario
     */
    @Operation(summary = "Confirmar pago", description = "Confirma el pago realizado y activa la membresía del usuario")
    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, Object> request) {
        try {
            String paymentIntentId = (String) request.get("paymentIntentId");
            Integer membershipId = (Integer) request.get("membershipId");
            Integer userId = (Integer) request.get("userId");

            // 1. Verificar el PaymentIntent en Stripe
            PaymentIntent paymentIntent = stripePaymentService.retrievePaymentIntent(paymentIntentId);

            if ("succeeded".equals(paymentIntent.getStatus())) {
                // 2. Obtener la membresía para obtener la duración
                Optional<Membership> membershipOpt = membershipService.getMembershipById(membershipId);
                if (membershipOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body("Membresía no encontrada");
                }
                
                Membership membership = membershipOpt.get();
                
                // 3. Crear nueva UserMembership en la base de datos local
                UserMembership userMembership = UserMembership.builder()
                    .userId(userId)
                    .membership(membership)
                    .status(UserMembership.Status.ACTIVE)
                    .startDate(LocalDateTime.now().toLocalDate())
                    .endDate(LocalDateTime.now().plusDays(membership.getDurationDays()).toLocalDate())
                    .createdAt(LocalDateTime.now())
                    .build();
                
                UserMembership savedMembership = userMembershipService.createOrUpdateMembership(userMembership);
                
                // 4. Crear registro de pago usando JPA simplificado
                System.out.println("🔄 INICIANDO CREACIÓN DE PAYMENT...");
                
                // Validar datos antes de procesar
                System.out.println("🔍 VALIDANDO DATOS PARA PAYMENT:");
                System.out.println("   - UserMembership ID: " + savedMembership.getId());
                System.out.println("   - Membership Price: " + membership.getPrice());
                System.out.println("   - Payment Intent ID: " + paymentIntentId);
                System.out.println("   - UserMembership Object: " + (savedMembership != null ? "✅ Valid" : "❌ NULL"));
                
                if (savedMembership == null || savedMembership.getId() == null) {
                    throw new RuntimeException("UserMembership no fue creado correctamente");
                }
                if (membership.getPrice() == null || membership.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new RuntimeException("Price de membresía inválido: " + membership.getPrice());
                }
                if (paymentIntentId == null || paymentIntentId.trim().isEmpty()) {
                    throw new RuntimeException("PaymentIntent ID no puede ser null o vacío");
                }
                
                try {
                    Payment savedPayment = paymentService.savePaymentSimplified(
                        savedMembership,
                        membership.getPrice(),
                        "GATEWAY",
                        paymentIntentId,
                        "CONFIRMED"
                    );
                    
                    System.out.println("✅ PAYMENT PROCESO COMPLETADO:");
                    System.out.println("   📝 Payment ID: " + savedPayment.getId());
                    System.out.println("   🔗 UserMembership ID: " + savedMembership.getId());
                    System.out.println("   💰 Amount: " + membership.getPrice());
                } catch (Exception paymentException) {
                    System.out.println("❌ ERROR CRÍTICO AL GUARDAR PAYMENT:");
                    System.out.println("   📋 Datos recibidos:");
                    System.out.println("      - UserMembership: " + savedMembership);
                    System.out.println("      - Amount: " + membership.getPrice());
                    System.out.println("      - PaymentIntentId: " + paymentIntentId);
                    System.out.println("   💥 Error: " + paymentException.getMessage());
                    paymentException.printStackTrace();
                    throw paymentException; // Re-lanzar para que el try-catch principal lo maneje
                }

                // 5. Respuesta con información de la membresía creada
                Map<String, Object> response = Map.of(
                    "message", "Pago confirmado y membresía activada",
                    "userMembershipId", savedMembership.getId(),
                    "userId", userId,
                    "membershipId", membershipId,
                    "status", "ACTIVE",
                    "startDate", savedMembership.getStartDate(),
                    "endDate", savedMembership.getEndDate()
                );

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("Pago no confirmado aún. Estado: " + paymentIntent.getStatus());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error al confirmar el pago: " + e.getMessage());
        }
    }

    /**
     * Obtener todos los pagos
     */
    @Operation(summary = "Obtener todos los pagos", description = "Devuelve una lista de todos los pagos registrados en el sistema")
    @GetMapping("/all")
    public ResponseEntity<List<Payment>> getAllPayments() {
        try {
            List<Payment> payments = paymentService.getAllPayments();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }
}
