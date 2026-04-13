package pe.edu.idat.biblioteca.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.idat.biblioteca.dto.response.PrestamoResponse;
import pe.edu.idat.biblioteca.entity.Prestamo;

/**
 * Mapper MapStruct para conversión de Prestamo Entity a DTO.
 * Incluye datos anidados del libro y usuario del préstamo.
 */
@Mapper(componentModel = "spring")
public interface PrestamoMapper {

    /** Convierte una entidad Prestamo a PrestamoResponse DTO con datos anidados */
    @Mapping(source = "libro.id", target = "libroId")
    @Mapping(source = "libro.titulo", target = "libroTitulo")
    @Mapping(source = "libro.autor", target = "libroAutor")
    @Mapping(source = "libro.isbn", target = "libroIsbn")
    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(target = "usuarioNombre", expression = "java(prestamo.getUsuario().getNombre() + \" \" + prestamo.getUsuario().getApellido())")
    @Mapping(source = "usuario.email", target = "usuarioEmail")
    PrestamoResponse toResponse(Prestamo prestamo);
}
