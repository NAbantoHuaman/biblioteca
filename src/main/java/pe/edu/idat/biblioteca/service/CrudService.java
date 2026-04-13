package pe.edu.idat.biblioteca.service;

import java.util.List;

/**
 * Interfaz genérica para operaciones CRUD.
 * @param <RE> Request DTO type
 * @param <RS> Response DTO type
 * @param <ID> Identity type
 */
public interface CrudService<RE, RS, ID> {
    List<RS> listarTodos();
    RS obtenerPorId(ID id);
    RS crear(RE request);
    RS actualizar(ID id, RE request);
    void eliminar(ID id);
}
