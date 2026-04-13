package pe.edu.idat.biblioteca.dto.request;

import jakarta.validation.constraints.*;
/**
 * DTO para registrar o actualizar un libro.
 */
public record LibroRequest(

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 1, max = 200, message = "El título debe tener entre 1 y 200 caracteres")
    String titulo,

    @NotBlank(message = "El autor es obligatorio")
    @Size(min = 2, max = 150, message = "El autor debe tener entre 2 y 150 caracteres")
    String autor,

    @NotBlank(message = "El ISBN es obligatorio")
    @Size(min = 10, max = 17, message = "El ISBN debe tener entre 10 y 17 caracteres")
    String isbn,

    @NotBlank(message = "La editorial es obligatoria")
    @Size(max = 100, message = "La editorial no puede exceder 100 caracteres")
    String editorial,

    @NotNull(message = "El año de publicación es obligatorio")
    @Min(value = 1000, message = "El año debe ser válido")
    @Max(value = 2100, message = "El año no puede ser mayor a 2100")
    Integer anioPublicacion,

    @NotBlank(message = "El género es obligatorio")
    @Size(max = 50, message = "El género no puede exceder 50 caracteres")
    String genero,

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    Integer stock
) {}
