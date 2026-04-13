package pe.edu.idat.biblioteca.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pe.edu.idat.biblioteca.dto.response.PrestamoResponse;
import pe.edu.idat.biblioteca.entity.Libro;
import pe.edu.idat.biblioteca.entity.Prestamo;
import pe.edu.idat.biblioteca.entity.Usuario;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-13T18:06:46-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (IBM Corporation)"
)
@Component
public class PrestamoMapperImpl implements PrestamoMapper {

    @Override
    public PrestamoResponse toResponse(Prestamo prestamo) {
        if ( prestamo == null ) {
            return null;
        }

        PrestamoResponse.PrestamoResponseBuilder prestamoResponse = PrestamoResponse.builder();

        prestamoResponse.libroId( prestamoLibroId( prestamo ) );
        prestamoResponse.libroTitulo( prestamoLibroTitulo( prestamo ) );
        prestamoResponse.libroAutor( prestamoLibroAutor( prestamo ) );
        prestamoResponse.libroIsbn( prestamoLibroIsbn( prestamo ) );
        prestamoResponse.usuarioId( prestamoUsuarioId( prestamo ) );
        prestamoResponse.usuarioEmail( prestamoUsuarioEmail( prestamo ) );
        prestamoResponse.id( prestamo.getId() );
        prestamoResponse.fechaPrestamo( prestamo.getFechaPrestamo() );
        prestamoResponse.fechaDevolucionEsperada( prestamo.getFechaDevolucionEsperada() );
        prestamoResponse.fechaDevolucionReal( prestamo.getFechaDevolucionReal() );
        if ( prestamo.getEstado() != null ) {
            prestamoResponse.estado( prestamo.getEstado().name() );
        }

        prestamoResponse.usuarioNombre( prestamo.getUsuario().getNombre() + " " + prestamo.getUsuario().getApellido() );

        return prestamoResponse.build();
    }

    private Long prestamoLibroId(Prestamo prestamo) {
        if ( prestamo == null ) {
            return null;
        }
        Libro libro = prestamo.getLibro();
        if ( libro == null ) {
            return null;
        }
        Long id = libro.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String prestamoLibroTitulo(Prestamo prestamo) {
        if ( prestamo == null ) {
            return null;
        }
        Libro libro = prestamo.getLibro();
        if ( libro == null ) {
            return null;
        }
        String titulo = libro.getTitulo();
        if ( titulo == null ) {
            return null;
        }
        return titulo;
    }

    private String prestamoLibroAutor(Prestamo prestamo) {
        if ( prestamo == null ) {
            return null;
        }
        Libro libro = prestamo.getLibro();
        if ( libro == null ) {
            return null;
        }
        String autor = libro.getAutor();
        if ( autor == null ) {
            return null;
        }
        return autor;
    }

    private String prestamoLibroIsbn(Prestamo prestamo) {
        if ( prestamo == null ) {
            return null;
        }
        Libro libro = prestamo.getLibro();
        if ( libro == null ) {
            return null;
        }
        String isbn = libro.getIsbn();
        if ( isbn == null ) {
            return null;
        }
        return isbn;
    }

    private Long prestamoUsuarioId(Prestamo prestamo) {
        if ( prestamo == null ) {
            return null;
        }
        Usuario usuario = prestamo.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        Long id = usuario.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String prestamoUsuarioEmail(Prestamo prestamo) {
        if ( prestamo == null ) {
            return null;
        }
        Usuario usuario = prestamo.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        String email = usuario.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }
}
