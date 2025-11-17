package com.Membership.GYMETRA.service;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripePaymentService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    /**
     * Método principal con los 3 parámetros
     */
    public PaymentIntent createPaymentIntent(BigDecimal amount, String currency, String description) throws Exception {
        Stripe.apiKey = stripeSecretKey;

        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount.longValue()); // Stripe recibe en cents como Long
        params.put("currency", currency);
        params.put("description", description);

        return PaymentIntent.create(params);
    }

    /**
     * Sobrecarga para cuando no quieras pasar descripción
     */
    public PaymentIntent createPaymentIntent(BigDecimal amount, String currency) throws Exception {
        return createPaymentIntent(amount, currency, "Pago estándar");
    }

    /**
     * Sobrecarga para cuando tengas amount en Long
     */
    public PaymentIntent createPaymentIntent(Long amount, String currency) throws Exception {
        return createPaymentIntent(BigDecimal.valueOf(amount), currency, "Pago estándar");
    }
    /**
     *  Recupera un PaymentIntent existente por su ID
    */
    public PaymentIntent retrievePaymentIntent(String id) throws Exception {
        Stripe.apiKey = stripeSecretKey;
        return PaymentIntent.retrieve(id);
    }
}
