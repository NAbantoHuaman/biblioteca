package pe.edu.idat.biblioteca.dto.auth;

import lombok.Builder;

import java.util.List;

/**
 * DTO de respuesta tras autenticación exitosa.
 * Contiene el access token, refresh token y datos básicos del usuario.
 */
@Builder
public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    String email,
    String nombreCompleto,
    List<String> roles
) {
    public AuthResponse {
        if (tokenType == null) {
            tokenType = "Bearer";
        }
    }
}
