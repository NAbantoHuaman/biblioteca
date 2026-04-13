package pe.edu.idat.biblioteca.service;

import pe.edu.idat.biblioteca.dto.auth.LoginRequest;
import pe.edu.idat.biblioteca.dto.auth.RefreshTokenRequest;
import pe.edu.idat.biblioteca.dto.auth.RegisterRequest;
import pe.edu.idat.biblioteca.dto.auth.AuthResponse;

/**
 * Interfaz para el servicio de autenticación.
 */
public interface AuthService {

    /** Registrar un nuevo usuario con rol USUARIO */
    AuthResponse register(RegisterRequest request);

    /** Iniciar sesión y obtener tokens JWT */
    AuthResponse login(LoginRequest request);

    /** Renovar access token usando refresh token */
    AuthResponse refreshToken(RefreshTokenRequest request);

    /** Cerrar sesión (eliminar refresh tokens) */
    void logout(String email);
}
