package pe.edu.idat.biblioteca.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import pe.edu.idat.biblioteca.dto.response.ApiResponse;
import pe.edu.idat.biblioteca.dto.response.ErrorResponse;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Manejador personalizado para errores de autorización (403 Forbidden).
 * Se activa cuando un usuario autenticado intenta acceder a una ruta para la cual no tiene el rol necesario.
 */
@Component
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        log.warn("Acceso denegado (Forbidden): {} - URI: {}", accessDeniedException.getMessage(), request.getRequestURI());

        // Construir respuesta de error personalizada
        ErrorResponse errorDetails = ErrorResponse.builder()
                .error("Prohibido")
                .path(request.getRequestURI())
                .build();

        ApiResponse<ErrorResponse> apiResponse = new ApiResponse<>(
                HttpStatus.FORBIDDEN.value(),
                "Acceso denegado. No tiene los permisos necesarios.",
                errorDetails,
                LocalDateTime.now()
        );

        // Escribir la respuesta como JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); 
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}
