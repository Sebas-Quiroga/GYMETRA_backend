package com.login.GYMETRA.service;

import com.login.GYMETRA.entity.PasswordResetToken;
import com.login.GYMETRA.entity.User;
import com.login.GYMETRA.repository.PasswordResetTokenRepository;
import com.login.GYMETRA.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // Genera token y envía correo
    public void generateResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();
        tokenRepository.save(resetToken);

        String body = "Hola " + user.getFirstName() + ",\n\n"
                + "Este es tu código de recuperación para restablecer tu contraseña:\n\n"
                + token + "\n\n"
                + "Ingresa este código en tu aplicación. Expira en 1 hora.";

        emailService.sendEmail(user.getEmail(), "Código de recuperación de contraseña", body);
        System.out.println("Token de recuperación generado y enviado: " + token);
    }

    // Valida token (y borra si está expirado)
    public boolean validateToken(String token) {
        Optional<PasswordResetToken> resetTokenOpt = tokenRepository.findByToken(token);
        if (resetTokenOpt.isEmpty()) return false;

        PasswordResetToken resetToken = resetTokenOpt.get();
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            // Si está expirado, elimínalo para no dejar basura
            tokenRepository.delete(resetToken);
            return false;
        }
        return true;
    }

    // Cambia la contraseña y elimina el token
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            // También puedes borrarlo aquí si ya está expirado
            tokenRepository.delete(resetToken);
            throw new RuntimeException("Token expirado");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Eliminar token usado
        tokenRepository.delete(resetToken);
    }
}
