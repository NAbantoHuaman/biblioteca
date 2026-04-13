package pe.edu.idat.biblioteca.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.idat.biblioteca.dto.auth.LoginRequest;
import pe.edu.idat.biblioteca.dto.auth.RefreshTokenRequest;
import pe.edu.idat.biblioteca.dto.auth.RegisterRequest;
import pe.edu.idat.biblioteca.dto.response.ApiResponse;
import pe.edu.idat.biblioteca.dto.auth.AuthResponse;
import pe.edu.idat.biblioteca.service.AuthService;

/**
 * Controlador de autenticación.
 * Gestiona registro, login, refresh token y logout.
 * Todas las rutas son públicas excepto /logout.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de registro, login, refresh token y logout")
public class AuthController {

    private final AuthService authService;

    /**
     * Registra un nuevo usuario con rol USUARIO.
     *
     * @param request datos de registro (nombre, apellido, email, password)
     * @return tokens JWT y datos del usuario
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario", description = "Crea una cuenta nueva con rol USUARIO y retorna tokens JWT")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(ApiResponse.created("Usuario registrado exitosamente", response),
                HttpStatus.CREATED);
    }

    /**
     * Inicia sesión y genera tokens JWT.
     *
     * @param request credenciales (email, password)
     * @return access token, refresh token y datos del usuario
     */
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y retorna access token + refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Inicio de sesión exitoso", response));
    }

    /**
     * Renueva el access token usando un refresh token válido.
     * Implementa rotación: el refresh token usado se invalida y se emite uno nuevo.
     *
     * @param request refresh token actual
     * @return nuevos tokens JWT
     */
    @PostMapping("/refresh")
    @Operation(summary = "Renovar access token", description = "Renueva el access token usando un refresh token válido (con rotación)")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token renovado exitosamente", response));
    }

    /**
     * Cierra la sesión del usuario autenticado.
     * Elimina los refresh tokens asociados.
     *
     * @param authentication datos del usuario autenticado
     * @return mensaje de confirmación
     */
    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Invalida los refresh tokens del usuario autenticado", security = @SecurityRequirement(name = "Bearer JWT"))
    public ResponseEntity<ApiResponse<String>> logout(Authentication authentication) {
        String email = authentication.getName();
        authService.logout(email);
        return ResponseEntity.ok(ApiResponse.success("Sesión cerrada exitosamente para el usuario: " + email, email));
    }
}
