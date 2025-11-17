package com.login.GYMETRA.service;

import com.login.GYMETRA.dto.JwtResponse;
import com.login.GYMETRA.dto.RegisterRequest;
import com.login.GYMETRA.dto.EditUserRequest;
import com.login.GYMETRA.entity.Role;
import com.login.GYMETRA.entity.User;
import com.login.GYMETRA.entity.UserRole;
import com.login.GYMETRA.repository.RoleRepository;
import com.login.GYMETRA.repository.UserRepository;
import com.login.GYMETRA.repository.UserRoleRepository;
import com.login.GYMETRA.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    private static final String DEFAULT_USER_ROLE = "Client";

    // ==============================================================
    // REGISTRO DE USUARIO
    // ==============================================================
    @Transactional
    public JwtResponse register(RegisterRequest request) {
        try {
            validateRegistrationRequest(request);

            Role clientRole = roleRepository.findByRoleName(DEFAULT_USER_ROLE)
                    .orElseThrow(() -> new IllegalStateException("Rol " + DEFAULT_USER_ROLE + " no encontrado"));

            User user = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .phone(request.getPhone())
                    .status("active")
                    .identification(Long.valueOf(request.getIdentification()))
                    .photoUrl(request.getPhotoUrl())
                    .createdAt(OffsetDateTime.now())
                    .build();

            UserRole userRole = UserRole.builder()
                    .user(user)
                    .role(clientRole)
                    .build();

            user.setUserRoles(new HashSet<>(Set.of(userRole)));
            clientRole.getUserRoles().add(userRole);

            User savedUser = userRepository.save(user);
            String token = jwtService.generateToken(buildJwtClaims(savedUser), savedUser);

            // Enviar correo de bienvenida
            sendWelcomeEmail(savedUser);

            return new JwtResponse(true, token, "Bearer", "Registro exitoso");

        } catch (UserAlreadyExistsException | IllegalArgumentException ex) {
            return new JwtResponse(false, null, null, ex.getMessage());
        }
    }

    // ==============================================================
    // LOGIN DE USUARIO
    // ==============================================================
    public JwtResponse login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return new JwtResponse(false, null, null, "Usuario no encontrado");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return new JwtResponse(false, null, null, "Contraseña incorrecta");
        }

        String token = jwtService.generateToken(buildJwtClaims(user), user);
        return new JwtResponse(true, token, "Bearer", "Login exitoso");
    }

    // ==============================================================
    // EDICIÓN DE USUARIO
    // ==============================================================
    @Transactional
    public JwtResponse editUser(Long userId, EditUserRequest request) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            // Verificar si el email ya está en uso por otro usuario
            if (request.getEmail() != null && !request.getEmail().equals(user.getEmail()) &&
                    userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException("El email ya está registrado");
            }

            // Actualizar campos no nulos
            if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
            if (request.getLastName() != null) user.setLastName(request.getLastName());
            if (request.getEmail() != null) user.setEmail(request.getEmail());
            if (request.getPhone() != null) user.setPhone(request.getPhone());
            if (request.getPhotoUrl() != null) user.setPhotoUrl(request.getPhotoUrl());
            if (request.getPassword() != null) {
                user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            }

            // Actualizar rol si se proporciona
            if (request.getRoleId() != null) {
                Role newRole = roleRepository.findById(request.getRoleId())
                        .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));

                // Buscar el UserRole existente
                List<UserRole> userRoles = userRoleRepository.findByUser_UserId(userId);
                if (!userRoles.isEmpty()) {
                    UserRole existingUserRole = userRoles.get(0); // Asumiendo un solo rol por usuario
                    existingUserRole.setRole(newRole);
                    userRoleRepository.save(existingUserRole);
                } else {
                    // Si no existe, crear uno nuevo
                    UserRole newUserRole = UserRole.builder()
                            .user(user)
                            .role(newRole)
                            .build();
                    userRoleRepository.save(newUserRole);
                }
            }

            User updatedUser = userRepository.save(user);
            String token = jwtService.generateToken(buildJwtClaims(updatedUser), updatedUser);

            return new JwtResponse(true, token, "Bearer", "Usuario actualizado exitosamente");

        } catch (IllegalArgumentException | UserAlreadyExistsException ex) {
            return new JwtResponse(false, null, null, ex.getMessage());
        }
    }

    // ==============================================================
    // OBTENER TODOS LOS USUARIOS
    // ==============================================================
    public List<User> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "userId"));
    }

    // ==============================================================
    // OBTENER USUARIO POR ID
    // ==============================================================
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    // ==============================================================
    // ELIMINAR USUARIO
    // ==============================================================
    @Transactional
    public boolean deleteUser(Long userId) {
        try {
            if (userRepository.existsById(userId)) {
                userRepository.deleteById(userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // ==============================================================
    // SUSPENDER/ACTIVAR CUENTA DE USUARIO
    // ==============================================================
    @Transactional
    public boolean updateUserStatus(Long userId, String status) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            if (!"active".equals(status) && !"suspended".equals(status)) {
                throw new IllegalArgumentException("Estado inválido. Debe ser 'active' o 'suspended'");
            }

            user.setStatus(status);
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ==============================================================
    // VALIDACIONES Y UTILIDADES
    // ==============================================================
    private void validateRegistrationRequest(RegisterRequest request) {
        validateIdentification(request.getIdentification());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("El email ya está registrado");
        }

        if (userRepository.existsByIdentification(request.getIdentification())) {
            throw new UserAlreadyExistsException("La identificación ya está registrada");
        }
    }

    private void validateIdentification(Long identification) {
        if (identification == null || identification <= 0) {
            throw new IllegalArgumentException("El número de identificación es obligatorio");
        }

        int length = identification.toString().length();
        if (length < 6 || length > 12) {
            throw new IllegalArgumentException("El número de identificación debe tener entre 6 y 12 dígitos");
        }
    }

    private Map<String, Object> buildJwtClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("email", user.getEmail());
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        claims.put("status", user.getStatus());
        claims.put("identification", user.getIdentification());
        claims.put("photoUrl", user.getPhotoUrl());
        claims.put("roleIds", user.getUserRoles().stream()
                .map(ur -> ur.getRole().getRoleId())
                .collect(Collectors.toList()));
        return claims;
    }

    // ==============================================================
    // ENVÍO DE CORREO DE BIENVENIDA
    // ==============================================================
    private void sendWelcomeEmail(User user) {
        String subject = "🌟 ¡Bienvenido a GYMETRA, " + user.getFirstName() + "! 🌟";

        String body = "¡Hola " + user.getFirstName() + "!\n\n"
                + "🎉 **¡Felicitaciones!** Tu cuenta en GYMETRA ha sido creada exitosamente. 🎉\n\n"
                + "Estamos emocionados de tenerte como parte de nuestra comunidad fitness. "
                + "Ahora tienes acceso completo a todas las herramientas y recursos que te ayudarán "
                + "a alcanzar tus objetivos de salud y bienestar.\n\n"
                + "🚀 **¿Qué puedes hacer ahora?**\n"
                + "• Accede a rutinas personalizadas de entrenamiento\n"
                + "• Registra tu progreso diario\n"
                + "• Conecta con entrenadores profesionales\n"
                + "• Descubre consejos nutricionales\n\n"
                + "💪 **¡Tu viaje hacia una mejor versión de ti mismo comienza aquí!**\n\n"
                + "Si tienes alguna duda o necesitas ayuda, nuestro equipo está aquí para apoyarte. "
                + "No dudes en contactarnos a través de la plataforma.\n\n"
                + "¡Éxito en tu transformación!\n\n"
                + "Con entusiasmo,\n"
                + "🏋️‍♂️ **El Equipo de GYMETRA**\n"
                + "Tu compañero en el camino hacia el bienestar";

        emailService.sendEmail(user.getEmail(), subject, body);
    }

    // ==============================================================
    // EXCEPCIÓN PERSONALIZADA
    // ==============================================================
    public static class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }
}
