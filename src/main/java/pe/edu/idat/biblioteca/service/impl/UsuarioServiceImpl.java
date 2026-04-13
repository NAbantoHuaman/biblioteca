package pe.edu.idat.biblioteca.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.idat.biblioteca.dto.response.UsuarioResponse;
import pe.edu.idat.biblioteca.entity.Usuario;
import pe.edu.idat.biblioteca.exception.ResourceNotFoundException;
import pe.edu.idat.biblioteca.mapper.UsuarioMapper;
import pe.edu.idat.biblioteca.repository.UsuarioRepository;
import pe.edu.idat.biblioteca.service.UsuarioService;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.idat.biblioteca.dto.request.UsuarioRequest;
import pe.edu.idat.biblioteca.service.impl.BaseServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de usuarios.
 * Utiliza la base genérica para operaciones CRUD y mantiene lógica propia para
 * perfiles.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioServiceImpl extends BaseServiceImpl<Usuario, UsuarioRequest, UsuarioResponse, Long>
        implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    protected JpaRepository<Usuario, Long> getRepository() {
        return usuarioRepository;
    }

    @Override
    protected UsuarioResponse mapToResponse(Usuario entity) {
        return usuarioMapper.toResponse(entity);
    }

    @Override
    protected Usuario mapToEntity(UsuarioRequest request) {
        return usuarioMapper.toEntity(request);
    }

    @Override
    protected void updateEntityFromRequest(UsuarioRequest request, Usuario entity) {
        usuarioMapper.updateEntity(entity, request);
    }

    @Override
    protected String getEntityName() {
        return "Usuario";
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPerfil(String email) {
        log.info("Obteniendo perfil para el usuario con email: '{}'", email);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Perfil no encontrado. Usuario con email '{}' no existe.", email);
                    return new ResourceNotFoundException("Usuario", "email", email);
                });
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponse cambiarEstado(Long id, boolean estado) {
        log.info("Iniciando cambio de estado a '{}' para el usuario con ID: {}", estado ? "ACTIVO" : "INACTIVO", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cambio de estado fallido. Usuario con ID {} no encontrado.", id);
                    return new ResourceNotFoundException("Usuario", "id", id);
                });

        usuario.setEstado(estado);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        String estadoLog = estado ? "ACTIVADO" : "BANEADO/DADO DE BAJA";
        log.info("Usuario '{}' (Email: {}) ha cambiado al estado: {}", usuario.getNombre(), usuario.getEmail(),
                estadoLog);

        return usuarioMapper.toResponse(usuarioActualizado);
    }
}
