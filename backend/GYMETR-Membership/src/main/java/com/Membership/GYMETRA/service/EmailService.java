package com.Membership.GYMETRA.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String fromEmail;

    public EmailService(JavaMailSender mailSender, @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    public void sendPaymentConfirmation(String to, String userName, String membershipName, String amount) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
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
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            System.out.println("📧 ENVIANDO EMAIL:");
            System.out.println("   De: " + fromEmail);
            System.out.println("   Para: " + to);
            System.out.println("   Asunto: " + subject);

            mailSender.send(message);

            System.out.println("✅ EMAIL ENVIADO EXITOSAMENTE A: " + to);
        } catch (Exception e) {
            System.err.println("❌ ERROR AL ENVIAR EMAIL:");
            System.err.println("   Para: " + to);
            System.err.println("   Error: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-lanzar para que el controlador lo maneje
        }
    }

    public void sendWelcomeEmail(String to, String userName, String membershipName) {
        String subject = "🌟 ¡Bienvenido a GYMETRA, " + userName + "! 🌟";

        String body = "¡Hola " + userName + "!\n\n"
                + "🎉 **¡Felicitaciones!** Tu pago para la membresía **" + membershipName + "** ha sido aprobado exitosamente. 🎉\n\n"
                + "Estamos emocionados de tenerte como parte de nuestra comunidad fitness. "
                + "Ahora tienes acceso completo a todas las herramientas y recursos premium que te ayudarán "
                + "a alcanzar tus objetivos de salud y bienestar.\n\n"
                + "🚀 **¿Qué puedes hacer ahora con tu membresía " + membershipName + "?**\n"
                + "• Accede a rutinas personalizadas de entrenamiento\n"
                + "• Registra tu progreso diario con herramientas avanzadas\n"
                + "• Conecta con entrenadores profesionales certificados\n"
                + "• Descubre planes nutricionales personalizados\n"
                + "• Accede a contenido exclusivo y clases virtuales\n\n"
                + "💪 **¡Tu viaje hacia una mejor versión de ti mismo comienza aquí!**\n\n"
                + "Si tienes alguna duda o necesitas ayuda para maximizar tu membresía, nuestro equipo está aquí para apoyarte. "
                + "No dudes en contactarnos a través de la plataforma.\n\n"
                + "¡Éxito en tu transformación!\n\n"
                + "Con entusiasmo,\n"
                + "🏋️‍♂️ **El Equipo de GYMETRA**\n"
                + "Tu compañero en el camino hacia el bienestar";

        sendEmail(to, subject, body);
    }
}
