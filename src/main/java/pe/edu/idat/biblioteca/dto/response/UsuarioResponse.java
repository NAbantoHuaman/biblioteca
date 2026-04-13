package pe.edu.idat.biblioteca.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta con información del usuario.
 * NUNCA expone la contraseña.
 */
@Builder
public record UsuarioResponse(
    Long id,
    String nombre,
    String apellido,
    String email,
    Boolean estado,
    LocalDateTime fechaRegistro,
    List<String> roles,
    List<PrestamoResponse> prestamos
) {}
