package pe.edu.idat.biblioteca.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pe.edu.idat.biblioteca.dto.response.ApiResponse;
import pe.edu.idat.biblioteca.dto.response.ErrorResponse;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Punto de entrada personalizado para errores de autenticación.
 * Retorna una respuesta JSON 401 en lugar del redirect por defecto de Spring
 * Security.
 */
@Component
@Slf4j
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

        @Override
        public void commence(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException {

                log.error("Error de autenticación: {} - URI: {}", authException.getMessage(), request.getRequestURI());

                // Construir respuesta de error personalizada
                ErrorResponse errorDetails = ErrorResponse.builder()
                                .error("No Autorizado")
                                .path(request.getRequestURI())
                                .build();

                ApiResponse<ErrorResponse> apiResponse = new ApiResponse<>(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Acceso denegado. No está autorizado para realizar esta acción.",
                                errorDetails,
                                LocalDateTime.now());

                // Escribir la respuesta como JSON
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setCharacterEncoding("UTF-8");

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.findAndRegisterModules(); // Soporte para LocalDateTime
                objectMapper.writeValue(response.getOutputStream(), apiResponse);
        }
}
