package pe.edu.idat.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.idat.biblioteca.entity.Libro;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones CRUD y consultas personalizadas sobre Libro.
 */
@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    /** Buscar libro por ISBN (único) */
    Optional<Libro> findByIsbn(String isbn);

    /** Verificar si existe un libro con un ISBN dado */
    boolean existsByIsbn(String isbn);

    /** Buscar libros cuyo título contenga un texto (ignorando mayúsculas) */
    List<Libro> findByTituloContainingIgnoreCase(String titulo);

    /** Buscar libros cuyo autor contenga un texto (ignorando mayúsculas) */
    List<Libro> findByAutorContainingIgnoreCase(String autor);

    /** Buscar libros por género */
    List<Libro> findByGeneroIgnoreCase(String genero);

    /** Buscar solo libros disponibles (stock > 0) */
    List<Libro> findByDisponibleTrue();

    /**
     * Consulta personalizada JPQL: búsqueda combinada por título y/o autor.
     * Permite buscar libros que coincidan con título O autor simultáneamente.
     */
    @Query("SELECT l FROM Libro l WHERE " +
            "LOWER(l.titulo) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(l.autor) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Libro> buscarPorTituloOAutor(@Param("termino") String termino);

    /**
     * Consulta personalizada: obtener libros con stock bajo (menor a un umbral).
     */
    @Query("SELECT l FROM Libro l WHERE l.stock > 0 AND l.stock <= :umbral ORDER BY l.stock ASC")
    List<Libro> findLibrosConStockBajo(@Param("umbral") int umbral);
}
