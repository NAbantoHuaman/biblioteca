package pe.edu.idat.biblioteca.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pe.edu.idat.biblioteca.dto.request.LibroRequest;
import pe.edu.idat.biblioteca.dto.response.LibroResponse;
import pe.edu.idat.biblioteca.entity.Libro;

/**
 * Mapper MapStruct para conversión entre Libro Entity y DTOs.
 */
@Mapper(componentModel = "spring")
public interface LibroMapper {

    /** Convierte una entidad Libro a LibroResponse DTO */
    LibroResponse toResponse(Libro libro);

    /** Convierte un LibroRequest DTO a entidad Libro (para creación) */
    @Mapping(target = "disponible", expression = "java(request.stock() != null && request.stock() > 0)")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "prestamos", ignore = true)
    Libro toEntity(LibroRequest request);

    /** Actualiza una entidad Libro existente con datos del LibroRequest DTO */
    @Mapping(target = "disponible", expression = "java(request.stock() != null && request.stock() > 0)")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "prestamos", ignore = true)
    void updateEntity(@MappingTarget Libro libro, LibroRequest request);
}
