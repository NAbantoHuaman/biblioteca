package pe.edu.idat.biblioteca.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
/**
 * DTO para registrar un nuevo préstamo.
 */
public record PrestamoRequest(

    @NotNull(message = "El ID del libro es obligatorio")
    Long libroId,

    /** ID del usuario. Si es ADMIN, puede especificar el usuario. Si es USUARIO, se ignora y se usa el autenticado. */
    Long usuarioId,

    /** Cantidad de días del préstamo (por defecto 7 si no se especifica) */
    @Min(value = 1, message = "El préstamo debe ser de al menos 1 día")
    @Max(value = 30, message = "El préstamo no puede exceder 30 días")
    Integer diasPrestamo
) {}
