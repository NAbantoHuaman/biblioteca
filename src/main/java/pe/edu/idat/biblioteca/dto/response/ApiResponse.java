package pe.edu.idat.biblioteca.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * Envoltorio global para estandarizar las respuestas de la API.
 * Proporciona metadatos sobre la operación y el payload real en 'data'.
 */
public record ApiResponse<T>(
        int status,
        String message,
        T data,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp
) {
    /**
     * Respuesta exitosa (200 OK)
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data, LocalDateTime.now());
    }

    /**
     * Respuesta exitosa con tipo de estado CREATED (201)
     */
    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(201, message, data, LocalDateTime.now());
    }
}
