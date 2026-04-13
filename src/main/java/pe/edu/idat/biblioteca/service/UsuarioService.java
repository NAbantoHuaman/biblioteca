package pe.edu.idat.biblioteca.service;

import pe.edu.idat.biblioteca.dto.request.UsuarioRequest;
import pe.edu.idat.biblioteca.dto.response.UsuarioResponse;

import java.util.List;

/**
 * Interfaz para el servicio de gestión de usuarios.
 */
public interface UsuarioService extends CrudService<UsuarioRequest, UsuarioResponse, Long> {

    /** Obtener perfil del usuario autenticado */
    UsuarioResponse obtenerPerfil(String email);

    /** Activar o desactivar un usuario (ADMIN) */
    UsuarioResponse cambiarEstado(Long id, boolean estado);
}
