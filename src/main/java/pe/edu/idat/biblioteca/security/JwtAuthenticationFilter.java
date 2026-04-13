package pe.edu.idat.biblioteca.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de seguridad personalizado que intercepta cada petición HTTP.
 * Extrae el JWT del header Authorization, lo valida y establece el SecurityContext.
 * Extiende OncePerRequestFilter para garantizar ejecución única por request.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // 1. Extraer el token JWT del header Authorization
            String jwt = extractJwtFromRequest(request);

            if (jwt != null) {
                // 2. Extraer el email (username) del token
                String username = jwtService.extractUsername(jwt);

                // 3. Si hay username y no hay autenticación previa en el contexto
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // 4. Cargar los detalles del usuario desde la BD
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 5. Validar el token contra los datos del usuario
                    if (jwtService.isTokenValid(jwt, userDetails)) {

                        // 6. Crear objeto de autenticación
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        // 7. Establecer la autenticación en el SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.debug("Usuario autenticado: {}", username);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error al procesar autenticación JWT: {}", e.getMessage());
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del header Authorization.
     * Formato esperado: "Bearer <token>"
     *
     * @param request petición HTTP
     * @return token JWT o null si no está presente
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
