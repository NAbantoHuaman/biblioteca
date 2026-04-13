package pe.edu.idat.biblioteca.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.idat.biblioteca.dto.auth.LoginRequest;
import pe.edu.idat.biblioteca.dto.auth.RefreshTokenRequest;
import pe.edu.idat.biblioteca.dto.auth.RegisterRequest;
import pe.edu.idat.biblioteca.dto.auth.AuthResponse;
import pe.edu.idat.biblioteca.entity.RefreshToken;
import pe.edu.idat.biblioteca.entity.Rol;
import pe.edu.idat.biblioteca.entity.Usuario;
import pe.edu.idat.biblioteca.enums.RolNombre;
import pe.edu.idat.biblioteca.exception.BadRequestException;
import pe.edu.idat.biblioteca.exception.ResourceNotFoundException;
import pe.edu.idat.biblioteca.exception.TokenRefreshException;
import pe.edu.idat.biblioteca.repository.RolRepository;
import pe.edu.idat.biblioteca.repository.UsuarioRepository;
import pe.edu.idat.biblioteca.security.JwtService;
import pe.edu.idat.biblioteca.security.UserDetailsServiceImpl;
import pe.edu.idat.biblioteca.service.AuthService;
import pe.edu.idat.biblioteca.service.RefreshTokenService;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de autenticación.
 * Maneja registro, login, refresh token y logout.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

        private final UsuarioRepository usuarioRepository;
        private final RolRepository rolRepository;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticationManager;
        private final JwtService jwtService;
        private final RefreshTokenService refreshTokenService;
        private final UserDetailsServiceImpl userDetailsService;

        /**
         * Registra un nuevo usuario con rol USUARIO.
         * Verifica que el email no esté registrado previamente.
         */
        @Override
        @Transactional
        public AuthResponse register(RegisterRequest request) {
                log.info("Iniciando proceso de registro para el email: {}", request.email());
                // Verificar si el email ya existe
                if (usuarioRepository.existsByEmail(request.email())) {
                        log.warn("Registro denegado. El email '{}' ya se encuentra registrado en el sistema.",
                                        request.email());
                        throw new BadRequestException("El email '" + request.email() + "' ya está registrado.");
                }

                // Obtener rol USUARIO
                Rol rolUsuario = rolRepository.findByNombre(RolNombre.USUARIO)
                                .orElseThrow(() -> new ResourceNotFoundException("Rol", "nombre", "USUARIO"));

                // Crear el nuevo usuario
                Set<Rol> roles = new HashSet<>();
                roles.add(rolUsuario);

                Usuario usuario = Usuario.builder()
                                .nombre(request.nombre())
                                .apellido(request.apellido())
                                .email(request.email())
                                .password(passwordEncoder.encode(request.password()))
                                .roles(roles)
                                .build();

                usuario = usuarioRepository.save(usuario);
                log.info("Usuario registrado exitosamente: {}", usuario.getEmail());

                // Autenticar al usuario recién registrado
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

                // Generar tokens
                String accessToken = jwtService.generateAccessToken(authentication);
                RefreshToken refreshToken = refreshTokenService.crearRefreshToken(usuario.getId());

                return buildAuthResponse(usuario, accessToken, refreshToken.getToken());
        }

        /**
         * Autentica un usuario y genera tokens JWT.
         */
        @Override
        @Transactional
        public AuthResponse login(LoginRequest request) {
                // Autenticar con Spring Security
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

                log.info("Procesando login para el email: {}", request.email());
                // Obtener usuario
                Usuario usuario = usuarioRepository.findByEmail(request.email())
                                .orElseThrow(() -> {
                                        log.error("Login fallido posterior a autenticación. Usuario con email '{}' no encontrado.",
                                                        request.email());
                                        return new ResourceNotFoundException("Usuario", "email", request.email());
                                });

                // Generar tokens
                String accessToken = jwtService.generateAccessToken(authentication);
                RefreshToken refreshToken = refreshTokenService.crearRefreshToken(usuario.getId());

                log.info("Login exitoso para: {}", request.email());

                return buildAuthResponse(usuario, accessToken, refreshToken.getToken());
        }

        /**
         * Renueva el access token usando un refresh token válido.
         * Implementa rotación de refresh tokens.
         */
        @Override
        @Transactional
        public AuthResponse refreshToken(RefreshTokenRequest request) {
                return refreshTokenService.findByToken(request.refreshToken())
                                .map(refreshTokenService::verificarExpiracion)
                                .map(RefreshToken::getUsuario)
                                .map(usuario -> {
                                        // Generar nuevo access token
                                        UserDetails userDetails = userDetailsService
                                                        .loadUserByUsername(usuario.getEmail());
                                        String accessToken = jwtService.generateAccessToken(userDetails);

                                        // Rotar refresh token (crear nuevo, eliminar viejo)
                                        RefreshToken nuevoRefreshToken = refreshTokenService
                                                        .crearRefreshToken(usuario.getId());

                                        log.info("Token renovado exitosamente para: {}", usuario.getEmail());
                                        return buildAuthResponse(usuario, accessToken, nuevoRefreshToken.getToken());
                                })
                                .orElseThrow(() -> new TokenRefreshException(
                                                request.refreshToken(),
                                                "Refresh token no encontrado en la base de datos."));
        }

        /**
         * Cierra la sesión eliminando los refresh tokens del usuario.
         */
        @Override
        @Transactional
        public void logout(String email) {
                log.info("Iniciando proceso de cierre de sesión para el email: {}", email);
                Usuario usuario = usuarioRepository.findByEmail(email)
                                .orElseThrow(() -> {
                                        log.error("Logout fallido. Usuario '{}' no encontrado.", email);
                                        return new ResourceNotFoundException("Usuario", "email", email);
                                });

                refreshTokenService.eliminarPorUsuarioId(usuario.getId());
                log.info("Logout exitoso para: {}", email);
        }

        // ======================== MÉTODO AUXILIAR ========================

        /**
         * Construye la respuesta de autenticación estandarizada.
         */
        private AuthResponse buildAuthResponse(Usuario usuario, String accessToken, String refreshToken) {
                return AuthResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .email(usuario.getEmail())
                                .nombreCompleto(usuario.getNombre() + " " + usuario.getApellido())
                                .roles(usuario.getRoles().stream()
                                                .map(rol -> rol.getNombre().name())
                                                .collect(Collectors.toList()))
                                .build();
        }
}
