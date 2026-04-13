package pe.edu.idat.biblioteca.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.edu.idat.biblioteca.dto.response.ApiResponse;
import pe.edu.idat.biblioteca.dto.response.ErrorResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador centralizado de excepciones para toda la API.
 * Todas las excepciones se transforman en respuestas JSON con formato
 * consistente.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ======================== EXCEPCIONES PERSONALIZADAS ========================

    /** Recurso no encontrado (404) */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    /** Solicitud inválida (400) */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBadRequest(
            BadRequestException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    /** No autorizado (401) */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleUnauthorized(
            UnauthorizedException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    /** Error de refresh token (403) */
    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleTokenRefresh(
            TokenRefreshException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    // ======================== EXCEPCIONES DE SPRING ========================

    /** Credenciales inválidas (401) */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED,
                "Credenciales inválidas. Verifique su email y contraseña.", request);
    }

    /** Acceso denegado (403) */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN,
                "No tiene permisos para acceder a este recurso.", request);
    }

    /** Errores de validación de campos (422 Unprocessable Entity) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(campo, mensaje);
        });

        ErrorResponse errorDetails = ErrorResponse.builder()
                .error("Error de Validación")
                .path(request.getRequestURI())
                .erroresValidacion(errores)
                .build();

        ApiResponse<ErrorResponse> response = new ApiResponse<>(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Los datos enviados contienen errores de validación",
                errorDetails,
                LocalDateTime.now());

        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /** Violación de integridad de datos — duplicados, FK, etc. (409 Conflict) */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleDataIntegrity(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        String mensaje = "Error de integridad de datos. Posible registro duplicado.";
        if (ex.getMessage() != null && ex.getMessage().contains("Duplicate entry")) {
            mensaje = "Ya existe un registro con los datos proporcionados.";
        }
        return buildErrorResponse(HttpStatus.CONFLICT, mensaje, request);
    }

    /** Excepción genérica no controlada (500) */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleGlobalException(
            Exception ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor. Contacte al administrador.", request);
    }

    // ======================== MÉTODO AUXILIAR ========================

    /**
     * Construye la respuesta de error con formato estandarizado.
     */
    private ResponseEntity<ApiResponse<ErrorResponse>> buildErrorResponse(
            HttpStatus status, String mensaje, HttpServletRequest request) {
        ErrorResponse errorDetails = ErrorResponse.builder()
                .error(status.getReasonPhrase())
                .path(request.getRequestURI())
                .build();

        ApiResponse<ErrorResponse> response = new ApiResponse<>(
                status.value(),
                mensaje,
                errorDetails,
                LocalDateTime.now());

        return new ResponseEntity<>(response, status);
    }
}
