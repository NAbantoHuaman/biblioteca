package pe.edu.idat.biblioteca.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pe.edu.idat.biblioteca.dto.request.LibroRequest;
import pe.edu.idat.biblioteca.dto.response.LibroResponse;
import pe.edu.idat.biblioteca.entity.Libro;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-13T18:06:47-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (IBM Corporation)"
)
@Component
public class LibroMapperImpl implements LibroMapper {

    @Override
    public LibroResponse toResponse(Libro libro) {
        if ( libro == null ) {
            return null;
        }

        LibroResponse.LibroResponseBuilder libroResponse = LibroResponse.builder();

        libroResponse.id( libro.getId() );
        libroResponse.titulo( libro.getTitulo() );
        libroResponse.autor( libro.getAutor() );
        libroResponse.isbn( libro.getIsbn() );
        libroResponse.editorial( libro.getEditorial() );
        libroResponse.anioPublicacion( libro.getAnioPublicacion() );
        libroResponse.genero( libro.getGenero() );
        libroResponse.stock( libro.getStock() );
        libroResponse.disponible( libro.getDisponible() );
        libroResponse.fechaRegistro( libro.getFechaRegistro() );

        return libroResponse.build();
    }

    @Override
    public Libro toEntity(LibroRequest request) {
        if ( request == null ) {
            return null;
        }

        Libro.LibroBuilder libro = Libro.builder();

        libro.titulo( request.titulo() );
        libro.autor( request.autor() );
        libro.isbn( request.isbn() );
        libro.editorial( request.editorial() );
        libro.anioPublicacion( request.anioPublicacion() );
        libro.genero( request.genero() );
        libro.stock( request.stock() );

        libro.disponible( request.stock() != null && request.stock() > 0 );

        return libro.build();
    }

    @Override
    public void updateEntity(Libro libro, LibroRequest request) {
        if ( request == null ) {
            return;
        }

        libro.setTitulo( request.titulo() );
        libro.setAutor( request.autor() );
        libro.setIsbn( request.isbn() );
        libro.setEditorial( request.editorial() );
        libro.setAnioPublicacion( request.anioPublicacion() );
        libro.setGenero( request.genero() );
        libro.setStock( request.stock() );

        libro.setDisponible( request.stock() != null && request.stock() > 0 );
    }
}
