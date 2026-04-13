package pe.edu.idat.biblioteca.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pe.edu.idat.biblioteca.dto.request.UsuarioRequest;
import pe.edu.idat.biblioteca.dto.response.UsuarioResponse;
import pe.edu.idat.biblioteca.entity.Rol;
import pe.edu.idat.biblioteca.entity.Usuario;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper MapStruct para conversión de Usuario Entity a DTO.
 * NUNCA incluye la contraseña en la respuesta.
 */
@Mapper(componentModel = "spring", uses = { PrestamoMapper.class })
public interface UsuarioMapper {

    /** Convierte una entidad Usuario a UsuarioResponse DTO (sin password) */
    @Mapping(target = "roles", expression = "java(mapRoles(usuario.getRoles()))")
    UsuarioResponse toResponse(Usuario usuario);

    /** Convierte UsuarioRequest a Usuario Entity */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "prestamos", ignore = true)
    Usuario toEntity(UsuarioRequest request);

    /** Actualiza una entidad Usuario desde un UsuarioRequest */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "prestamos", ignore = true)
    void updateEntity(@MappingTarget Usuario usuario, UsuarioRequest request);

    default List<String> mapRoles(java.util.Set<Rol> roles) {
        if (roles == null)
            return null;
        return roles.stream().map(rol -> rol.getNombre().name()).collect(Collectors.toList());
    }
}
