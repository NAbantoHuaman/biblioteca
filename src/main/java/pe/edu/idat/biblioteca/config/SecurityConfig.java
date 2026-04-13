package pe.edu.idat.biblioteca.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pe.edu.idat.biblioteca.security.JwtAuthEntryPoint;
import pe.edu.idat.biblioteca.security.JwtAccessDeniedHandler;
import pe.edu.idat.biblioteca.security.JwtAuthenticationFilter;
import pe.edu.idat.biblioteca.security.UserDetailsServiceImpl;

/**
 * Configuración central de seguridad de la aplicación.
 * Define las reglas de acceso, filtros JWT y políticas de sesión.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Habilita @PreAuthorize en controladores
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF (API REST stateless — no usa cookies de sesión)
                .csrf(AbstractHttpConfigurer::disable)

                // Manejo de excepciones de seguridad
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                // Política de sesión: STATELESS (sin estado — solo JWT)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Reglas de autorización por ruta
                .authorizeHttpRequests(auth -> auth
                        // ========== RUTAS PÚBLICAS ==========
                        // Autenticación: Login y Registro son públicos, Logout requiere token
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh").permitAll()
                        .requestMatchers("/api/auth/logout").authenticated()
                        // Swagger / OpenAPI
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // ========== RUTAS DE LIBROS ==========
                        // GET libros: público (cualquiera puede consultar el catálogo)
                        .requestMatchers(HttpMethod.GET, "/api/libros/**").permitAll()
                        // POST, PUT, DELETE libros: solo ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/libros/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/libros/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/libros/**").hasRole("ADMIN")

                        // ========== RUTAS DE PRÉSTAMOS ==========
                        // Registrar préstamo: solo ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/prestamos/**").hasRole("ADMIN")
                        // Listar todos y vencidos: solo ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/prestamos/todos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/prestamos/vencidos").hasRole("ADMIN")
                        // Mis préstamos y devolver: cualquier usuario autenticado
                        .requestMatchers(HttpMethod.GET, "/api/prestamos/mis-prestamos").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/prestamos/devolver/**").authenticated()

                        // ========== RUTAS DE USUARIOS ==========
                        // Listar y gestionar usuarios: solo ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/*/estado").hasRole("ADMIN")
                        // Perfil propio: cualquier usuario autenticado
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/perfil").authenticated()

                        // ========== CUALQUIER OTRA RUTA ==========
                        .anyRequest().authenticated()
                )

                // Proveedor de autenticación
                .authenticationProvider(authenticationProvider())

                // Agregar filtro JWT antes del filtro de autenticación estándar
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Proveedor de autenticación con UserDetailsService y BCrypt.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Bean para el AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Codificador de contraseñas BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
