package pe.edu.idat.biblioteca.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.idat.biblioteca.entity.RefreshToken;
import pe.edu.idat.biblioteca.entity.Usuario;
import pe.edu.idat.biblioteca.exception.ResourceNotFoundException;
import pe.edu.idat.biblioteca.exception.TokenRefreshException;
import pe.edu.idat.biblioteca.repository.RefreshTokenRepository;
import pe.edu.idat.biblioteca.repository.UsuarioRepository;
import pe.edu.idat.biblioteca.service.RefreshTokenService;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementación del servicio de gestión de Refresh Tokens.
 * Maneja creación, verificación de expiración, y eliminación (rotación).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    /**
     * Crea un nuevo refresh token para un usuario.
     * Si el usuario ya tiene un refresh token, lo actualiza (rotación).
     */
    @Override
    @Transactional
    public RefreshToken crearRefreshToken(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));

        // Buscar si ya existe un token para el usuario
        RefreshToken refreshToken = refreshTokenRepository.findByUsuario(usuario)
                .orElse(new RefreshToken());

        // Actualizar o setear valores
        refreshToken.setUsuario(usuario);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setFechaExpiracion(Instant.now().plusMillis(refreshTokenExpiration));

        refreshToken = refreshTokenRepository.save(refreshToken);
        log.info("Refresh token gestionado (creado/actualizado) para usuario: {}", usuario.getEmail());
        return refreshToken;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Verifica si un refresh token ha expirado.
     * Si ha expirado, lo elimina y lanza excepción.
     */
    @Override
    @Transactional
    public RefreshToken verificarExpiracion(RefreshToken token) {
        if (token.getFechaExpiracion().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(),
                    "El refresh token ha expirado. Por favor, inicie sesión nuevamente.");
        }
        return token;
    }

    @Override
    @Transactional
    public void eliminarPorUsuarioId(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));
        refreshTokenRepository.deleteByUsuario(usuario);
        log.info("Refresh tokens eliminados para usuario ID: {}", usuarioId);
    }
}
