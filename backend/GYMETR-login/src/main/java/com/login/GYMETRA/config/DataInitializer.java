package com.login.GYMETRA.config;

import com.login.GYMETRA.entity.Role;
import com.login.GYMETRA.entity.User;
import com.login.GYMETRA.entity.UserRole;
import com.login.GYMETRA.repository.RoleRepository;
import com.login.GYMETRA.repository.UserRepository;
import com.login.GYMETRA.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            // Crear roles primero
            Role adminRole = Role.builder()
                    .roleName("Admin")
                    .description("Administrador del sistema")
                    .build();
            adminRole = roleRepository.save(adminRole);

            Role clientRole = Role.builder()
                    .roleName("Client")
                    .description("Cliente del gimnasio")
                    .build();
            clientRole = roleRepository.save(clientRole);

            // Crear usuarios y asignar roles
            // Usuario 1: admin1@gymetra.com con rol Client
            User user1 = User.builder()
                    .firstName("Admin")
                    .lastName("Uno")
                    .email("admin1@gymetra.com")
                    .passwordHash(passwordEncoder.encode("password123"))
                    .phone("1234567890")
                    .status("active")
                    .identification(123456789L)
                    .photoUrl(null)
                    .createdAt(OffsetDateTime.now())
                    .build();
            user1 = userRepository.save(user1);

            UserRole userRole1 = UserRole.builder()
                    .user(user1)
                    .role(clientRole)
                    .build();
            userRoleRepository.save(userRole1);

            // Usuario 2: admin2@gymetra.com con rol Admin
            User user2 = User.builder()
                    .firstName("Admin")
                    .lastName("Dos")
                    .email("admin2@gymetra.com")
                    .passwordHash(passwordEncoder.encode("password123"))
                    .phone("0987654321")
                    .status("active")
                    .identification(987654321L)
                    .photoUrl(null)
                    .createdAt(OffsetDateTime.now())
                    .build();
            user2 = userRepository.save(user2);

            UserRole userRole2 = UserRole.builder()
                    .user(user2)
                    .role(adminRole)
                    .build();
            userRoleRepository.save(userRole2);
        }
    }
}