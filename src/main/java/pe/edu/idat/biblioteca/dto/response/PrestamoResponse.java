package pe.edu.idat.biblioteca.dto.response;

import lombok.Builder;

import java.time.LocalDate;

/**
 * DTO de respuesta con información de un préstamo.
 * Incluye datos anidados del libro y usuario asociados.
 */
@Builder
public record PrestamoResponse(
    Long id,
    LocalDate fechaPrestamo,
    LocalDate fechaDevolucionEsperada,
    LocalDate fechaDevolucionReal,
    String estado,

    // Datos del libro
    Long libroId,
    String libroTitulo,
    String libroAutor,
    String libroIsbn,

    // Datos del usuario
    Long usuarioId,
    String usuarioNombre,
    String usuarioEmail
) {}
