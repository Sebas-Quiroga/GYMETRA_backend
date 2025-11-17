    package com.login.GYMETRA.controller;

    import com.login.GYMETRA.dto.RoleRequest;
    import com.login.GYMETRA.dto.RoleResponse;
    import com.login.GYMETRA.service.RoleService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.Optional;

    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.Parameter;
    import io.swagger.v3.oas.annotations.media.Content;
    import io.swagger.v3.oas.annotations.media.Schema;
    import io.swagger.v3.oas.annotations.responses.ApiResponse;
    import io.swagger.v3.oas.annotations.responses.ApiResponses;
    import io.swagger.v3.oas.annotations.tags.Tag;

    @RestController
    @RequestMapping("/api/roles")
    @RequiredArgsConstructor
    @Tag(name = "Roles", description = "API para gestión de roles")
    public class RoleController {

        private final RoleService roleService;

        // ➕ CREAR ROL
        @Operation(summary = "Crear nuevo rol", description = "Crea un nuevo rol en el sistema")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "201", description = "Rol creado exitosamente",
                        content = @Content(schema = @Schema(implementation = RoleResponse.class))),
                @ApiResponse(responseCode = "400", description = "Datos del rol inválidos")
        })
        @PostMapping
        public ResponseEntity<RoleResponse> createRole(@RequestBody RoleRequest request) {
            try {
                RoleResponse response = roleService.createRole(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } catch (IllegalArgumentException ex) {
                return ResponseEntity.badRequest().build();
            }
        }

        // 📋 OBTENER TODOS LOS ROLES
        @Operation(summary = "Obtener todos los roles", description = "Devuelve una lista de todos los roles registrados")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Lista de roles obtenida exitosamente")
        })
        @GetMapping
        public ResponseEntity<List<RoleResponse>> getAllRoles() {
            List<RoleResponse> roles = roleService.getAllRoles();
            return ResponseEntity.ok(roles);
        }

        // 🔍 OBTENER ROL POR ID
        @Operation(summary = "Obtener rol por ID", description = "Devuelve un rol específico por su ID")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Rol encontrado",
                        content = @Content(schema = @Schema(implementation = RoleResponse.class))),
                @ApiResponse(responseCode = "404", description = "Rol no encontrado")
        })
        @GetMapping("/{roleId}")
        public ResponseEntity<RoleResponse> getRoleById(
                @Parameter(description = "ID del rol a obtener", required = true)
                @PathVariable Long roleId) {
            Optional<RoleResponse> role = roleService.getRoleById(roleId);
            return role.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        }

        // ✏️ ACTUALIZAR ROL
        @Operation(summary = "Actualizar rol", description = "Actualiza los datos de un rol existente")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Rol actualizado exitosamente",
                        content = @Content(schema = @Schema(implementation = RoleResponse.class))),
                @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos"),
                @ApiResponse(responseCode = "404", description = "Rol no encontrado")
        })
        @PutMapping("/{roleId}")
        public ResponseEntity<RoleResponse> updateRole(
                @Parameter(description = "ID del rol a actualizar", required = true)
                @PathVariable Long roleId,
                @Parameter(description = "Datos del rol a actualizar", required = true,
                        schema = @Schema(implementation = RoleRequest.class))
                @RequestBody RoleRequest request) {
            try {
                RoleResponse response = roleService.updateRole(roleId, request);
                return ResponseEntity.ok(response);
            } catch (IllegalArgumentException ex) {
                return ResponseEntity.badRequest().build();
            }
        }

        // ❌ ELIMINAR ROL
        @Operation(summary = "Eliminar rol", description = "Elimina un rol por su ID")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Rol eliminado exitosamente"),
                @ApiResponse(responseCode = "404", description = "Rol no encontrado")
        })
        @DeleteMapping("/{roleId}")
        public ResponseEntity<String> deleteRole(
                @Parameter(description = "ID del rol a eliminar", required = true)
                @PathVariable Long roleId) {
            boolean deleted = roleService.deleteRole(roleId);
            if (deleted) {
                return ResponseEntity.ok("Rol eliminado exitosamente");
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }