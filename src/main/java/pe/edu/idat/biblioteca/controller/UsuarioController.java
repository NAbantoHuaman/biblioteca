package pe.edu.idat.biblioteca.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.idat.biblioteca.dto.response.ApiResponse;
import pe.edu.idat.biblioteca.dto.response.UsuarioResponse;
import pe.edu.idat.biblioteca.service.UsuarioService;

import java.util.List;

/**
 * Controlador de usuarios.
 * Perfil propio: cualquier autenticado | Listado y gestión: solo ADMIN.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios y perfiles")
@SecurityRequirement(name = "Bearer JWT")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Obtiene el perfil del usuario autenticado.
     */
    @GetMapping("/perfil")
    @Operation(summary = "Ver mi perfil", description = "Retorna la información del perfil del usuario autenticado.")
    public ResponseEntity<ApiResponse<UsuarioResponse>> obtenerPerfil(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success("Perfil cargado exitosamente",
                usuarioService.obtenerPerfil(authentication.getName())));
    }

    /**
     * Lista todos los usuarios registrados. Solo ADMIN.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar usuarios [ADMIN]", description = "Retorna la lista de todos los usuarios registrados. Requiere rol ADMIN.")
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.success("Lista de usuarios obtenida", usuarioService.listarTodos()));
    }

    /**
     * Obtiene un usuario por ID. Solo ADMIN.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener usuario por ID [ADMIN]", description = "Retorna la información de un usuario específico. Requiere rol ADMIN.")
    public ResponseEntity<ApiResponse<UsuarioResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Usuario encontrado", usuarioService.obtenerPorId(id)));
    }

    /**
     * Activa o desactiva un usuario. Solo ADMIN.
     *
     * @param id     ID del usuario
     * @param activo true para activar, false para desactivar
     */
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cambiar estado de usuario [ADMIN]", description = "Activa o desactiva un usuario del sistema. Requiere rol ADMIN.")
    public ResponseEntity<ApiResponse<UsuarioResponse>> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean estado) {
        UsuarioResponse response = usuarioService.cambiarEstado(id, estado);
        String statusText = estado ? "activado (Acceso Restaurado)" : "baneado (Dado de Baja)";
        String message = "Usuario " + response.nombre() + " " + response.apellido() + " ha sido " + statusText
                + " exitosamente.";
        return ResponseEntity.ok(ApiResponse.success(message, response));
    }
}
