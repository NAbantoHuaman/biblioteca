package pe.edu.idat.biblioteca.dto.auth;

import jakarta.validation.constraints.NotBlank;
/**
 * DTO para solicitar la renovación de un access token usando el refresh token.
 */
public record RefreshTokenRequest(

    @NotBlank(message = "El refresh token es obligatorio")
    String refreshToken
) {}
