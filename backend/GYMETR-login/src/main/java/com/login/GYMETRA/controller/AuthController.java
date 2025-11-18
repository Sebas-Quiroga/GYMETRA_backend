package com.login.GYMETRA.controller;

import com.login.GYMETRA.dto.LoginRequest;
import com.login.GYMETRA.dto.RegisterRequest;
import com.login.GYMETRA.dto.EditUserRequest;
import com.login.GYMETRA.dto.JwtResponse;
import com.login.GYMETRA.entity.User;
import com.login.GYMETRA.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "API para gestión de autenticación y usuarios")
public class AuthController {

    private final UserService userService;

    // 🧾 REGISTRO DE USUARIO
    @Operation(summary = "Registrar nuevo usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos")
    })
    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@RequestBody RegisterRequest request) {
        JwtResponse jwtResponse = userService.register(request);

        HttpStatus status = jwtResponse.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(jwtResponse);
    }

    // 🔑 LOGIN DE USUARIO
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        JwtResponse response = userService.login(request.getEmail(), request.getPassword());

        HttpStatus status;
        if (response.isSuccess()) {
            status = HttpStatus.OK;
        } else {
            status = response.getMessage().equalsIgnoreCase("Usuario no encontrado")
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.UNAUTHORIZED;
        }

        return ResponseEntity.status(status).body(response);
    }

    // ✏️ EDITAR USUARIO
    @Operation(
            summary = "Editar usuario",
            description = "Actualiza los datos de un usuario existente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/users/{userId}")
    public ResponseEntity<JwtResponse> editUser(
            @Parameter(description = "ID del usuario a editar", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Datos del usuario a actualizar", required = true,
                    schema = @Schema(implementation = EditUserRequest.class))
            @RequestBody EditUserRequest request) {
        JwtResponse response = userService.editUser(userId, request);

        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    // 📋 OBTENER TODOS LOS USUARIOS
    @Operation(summary = "Obtener todos los usuarios", description = "Devuelve una lista de todos los usuarios registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    })
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // ❌ ELIMINAR USUARIO
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "ID del usuario a eliminar", required = true)
            @PathVariable Long userId) {
        boolean deleted = userService.deleteUser(userId);
        if (deleted) {
            return ResponseEntity.ok("Usuario eliminado exitosamente");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 🚫 SUSPENDER/ACTIVAR CUENTA DE USUARIO
    @Operation(summary = "Suspender o activar cuenta de usuario", description = "Cambia el estado de la cuenta de un usuario (active/suspended)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado de la cuenta actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<String> updateUserStatus(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Nuevo estado de la cuenta (active o suspended)", required = true)
            @RequestParam String status) {
        boolean updated = userService.updateUserStatus(userId, status);
        if (updated) {
            return ResponseEntity.ok("Estado de la cuenta actualizado exitosamente");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
