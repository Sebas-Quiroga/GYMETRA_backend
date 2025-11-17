    package com.login.GYMETRA.controller;

    import com.login.GYMETRA.service.PasswordResetService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    @RestController
    @RequestMapping("api/auth")
    @RequiredArgsConstructor
    public class PasswordResetController {

        private final PasswordResetService passwordResetService;

        @PostMapping("/forgot-password")
        public ResponseEntity<String> forgotPassword(@RequestParam String email) {
            try {
                passwordResetService.generateResetToken(email);
                return ResponseEntity.ok("Se ha enviado un correo con el enlace para restablecer la contraseña.");
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }

        @PostMapping("/reset-password")
        public ResponseEntity<String> resetPassword(@RequestParam String token,
                                                    @RequestParam String newPassword) {
            try {
                passwordResetService.resetPassword(token, newPassword);
                return ResponseEntity.ok("Contraseña restablecida correctamente.");
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }

        @PostMapping("/validate-token") // ❌ Este endpoint no estaba en tu controlador antes
        public ResponseEntity<String> validateToken(@RequestParam String token) {
            boolean isValid = passwordResetService.validateToken(token);
            return isValid
                    ? ResponseEntity.ok("Código válido")
                    : ResponseEntity.badRequest().body("Código inválido o expirado");
        }
    }
