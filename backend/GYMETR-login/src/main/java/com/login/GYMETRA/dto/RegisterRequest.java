package com.login.GYMETRA.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    @Email(message = "Correo inválido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    private String phone;

    @Digits(integer = 10, fraction = 0, message = "La identificación debe tener máximo 10 dígitos")
    @NotNull(message = "La identificación es obligatoria")
    private Long identification;

    // Foto del usuario, opcional
    private String photoUrl;
}
