package pe.edu.idat.biblioteca.dto.response;

import lombok.Builder;

import java.util.Map;

/**
 * Detalles específicos del error para acoplar dentro del 'data' de un ApiResponse.
 */
@Builder
public record ErrorResponse(
    String error,
    String path,
    /** Errores de validación por campo (solo para errores 422) */
    Map<String, String> erroresValidacion
) {}
