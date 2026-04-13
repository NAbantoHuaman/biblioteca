package pe.edu.idat.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import pe.edu.idat.biblioteca.entity.RefreshToken;
import pe.edu.idat.biblioteca.entity.Usuario;

import java.util.Optional;

/**
 * Repositorio para operaciones sobre RefreshToken.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /** Buscar refresh token por su valor */
    Optional<RefreshToken> findByToken(String token);

    /** Buscar refresh token de un usuario específico */
    Optional<RefreshToken> findByUsuario(Usuario usuario);
    
    /** Eliminar todos los refresh tokens de un usuario (logout) */
    @Modifying
    int deleteByUsuario(Usuario usuario);
}
