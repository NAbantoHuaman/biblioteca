package pe.edu.idat.biblioteca.service;

import pe.edu.idat.biblioteca.entity.RefreshToken;

import java.util.Optional;

/**
 * Interfaz para el servicio de gestión de Refresh Tokens.
 */
public interface RefreshTokenService {

    /** Crear un nuevo refresh token para un usuario */
    RefreshToken crearRefreshToken(Long usuarioId);

    /** Buscar refresh token por su valor */
    Optional<RefreshToken> findByToken(String token);

    /** Verificar si un refresh token ha expirado */
    RefreshToken verificarExpiracion(RefreshToken token);

    /** Eliminar los refresh tokens de un usuario (para logout) */
    void eliminarPorUsuarioId(Long usuarioId);
}
