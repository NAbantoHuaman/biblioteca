package pe.edu.idat.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.idat.biblioteca.entity.Usuario;

import java.util.Optional;

/**
 * Repositorio para operaciones CRUD sobre Usuario.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /** Buscar usuario por email (login) */
    Optional<Usuario> findByEmail(String email);

    /** Verificar si un email ya está registrado */
    boolean existsByEmail(String email);
}
