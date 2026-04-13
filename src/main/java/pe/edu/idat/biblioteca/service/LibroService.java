package pe.edu.idat.biblioteca.service;

import pe.edu.idat.biblioteca.dto.request.LibroRequest;
import pe.edu.idat.biblioteca.dto.response.LibroResponse;

import java.util.List;

/**
 * Interfaz para el servicio de gestión de libros.
 */
public interface LibroService extends CrudService<LibroRequest, LibroResponse, Long> {

    /** Buscar libros por término (título o autor) */
    List<LibroResponse> buscar(String termino);

    /** Obtener libros disponibles */
    List<LibroResponse> listarDisponibles();

    /** Buscar libros por género */
    List<LibroResponse> buscarPorGenero(String genero);
}
