package pe.edu.idat.biblioteca.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO de respuesta con información de un libro.
 * No expone datos internos de la entidad (préstamos asociados).
 */
@Builder
public record LibroResponse(
    Long id,
    String titulo,
    String autor,
    String isbn,
    String editorial,
    Integer anioPublicacion,
    String genero,
    Integer stock,
    Boolean disponible,
    LocalDateTime fechaRegistro
) {}
