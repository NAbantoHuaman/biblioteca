package pe.edu.idat.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.idat.biblioteca.entity.Rol;
import pe.edu.idat.biblioteca.enums.RolNombre;

import java.util.Optional;

/**
 * Repositorio para operaciones sobre Rol.
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    /** Buscar rol por nombre */
    Optional<Rol> findByNombre(RolNombre nombre);
}
