package com.Membership.GYMETRA.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPaymentConfirmation(String to, String userName, String membershipName, String amount) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Confirmación de Pago - GYMETRA");
        message.setText(
                "Hola " + userName + ",\n\n" +
                        "Tu pago para la membresía **" + membershipName + "** ha sido confirmado exitosamente.\n" +
                        "Monto: " + amount + "\n\n" +
                        "Gracias por confiar en GYMETRA 💪.\n\n" +
                        "Equipo GYMETRA"
        );
        mailSender.send(message);
    }
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
