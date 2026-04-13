package pe.edu.idat.biblioteca.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.idat.biblioteca.entity.Usuario;
import pe.edu.idat.biblioteca.repository.UsuarioRepository;

import java.util.stream.Collectors;

/**
 * Implementación personalizada de UserDetailsService.
 * Carga los datos del usuario desde la base de datos para la autenticación.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Carga un usuario por su email (usado como username).
     * Convierte la entidad Usuario a un UserDetails de Spring Security,
     * incluyendo sus roles como GrantedAuthorities con prefijo ROLE_.
     *
     * @param email email del usuario
     * @return UserDetails con credenciales y autoridades
     * @throws UsernameNotFoundException si el email no existe
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado con email: {}", email);
                    return new UsernameNotFoundException(
                            "Usuario no encontrado con email: " + email);
                });

        log.debug("Usuario encontrado: {} con roles: {}", email, usuario.getRoles());

        return new User(
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getEstado(),   // enabled
                true,                  // accountNonExpired
                true,                  // credentialsNonExpired
                true,                  // accountNonLocked
                usuario.getRoles().stream()
                        .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre().name()))
                        .collect(Collectors.toList())
        );
    }
}
