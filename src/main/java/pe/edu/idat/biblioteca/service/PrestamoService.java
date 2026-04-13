package pe.edu.idat.biblioteca.service;

import pe.edu.idat.biblioteca.dto.request.PrestamoRequest;
import pe.edu.idat.biblioteca.dto.response.PrestamoResponse;

import java.util.List;

/**
 * Interfaz para el servicio de gestión de préstamos.
 */
public interface PrestamoService {

    /** Registrar un nuevo préstamo (ADMIN) */
    PrestamoResponse registrarPrestamo(PrestamoRequest request);

    /** Registrar la devolución de un libro */
    PrestamoResponse registrarDevolucion(Long prestamoId, String emailUsuario);

    /** Obtener historial de préstamos del usuario autenticado */
    List<PrestamoResponse> obtenerMisPrestamos(String email);

    /** Obtener todos los préstamos (ADMIN) */
    List<PrestamoResponse> listarTodos();

    /** Obtener préstamos vencidos (ADMIN) */
    List<PrestamoResponse> obtenerVencidos();
}
