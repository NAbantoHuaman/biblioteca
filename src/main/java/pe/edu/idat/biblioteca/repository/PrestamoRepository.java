package pe.edu.idat.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.idat.biblioteca.entity.Prestamo;
import pe.edu.idat.biblioteca.enums.EstadoPrestamo;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para operaciones CRUD y consultas personalizadas sobre Prestamo.
 */
@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    /** Obtener todos los préstamos de un usuario */
    List<Prestamo> findByUsuarioId(Long usuarioId);

    /** Obtener préstamos por estado */
    List<Prestamo> findByEstado(EstadoPrestamo estado);

    /** Obtener préstamos activos de un usuario específico */
    List<Prestamo> findByUsuarioIdAndEstado(Long usuarioId, EstadoPrestamo estado);

    /**
     * Contar préstamos activos de un usuario.
     * Se usa para limitar la cantidad máxima de préstamos simultáneos.
     */
    @Query("SELECT COUNT(p) FROM Prestamo p WHERE p.usuario.id = :usuarioId AND p.estado = 'ACTIVO'")
    long contarPrestamosActivosPorUsuario(@Param("usuarioId") Long usuarioId);

    /**
     * Obtener préstamos vencidos (fecha de devolución esperada pasada y estado ACTIVO).
     * Útil para la gestión administrativa.
     */
    @Query("SELECT p FROM Prestamo p WHERE p.estado = 'ACTIVO' AND p.fechaDevolucionEsperada < :fechaActual")
    List<Prestamo> findPrestamosVencidos(@Param("fechaActual") LocalDate fechaActual);

    /**
     * Verificar si un libro tiene préstamos activos.
     */
    @Query("SELECT COUNT(p) > 0 FROM Prestamo p WHERE p.libro.id = :libroId AND p.estado = 'ACTIVO'")
    boolean existePrestamoActivoPorLibro(@Param("libroId") Long libroId);
}
