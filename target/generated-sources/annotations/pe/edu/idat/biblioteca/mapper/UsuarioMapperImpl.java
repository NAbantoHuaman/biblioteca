package pe.edu.idat.biblioteca.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.edu.idat.biblioteca.dto.request.UsuarioRequest;
import pe.edu.idat.biblioteca.dto.response.PrestamoResponse;
import pe.edu.idat.biblioteca.dto.response.UsuarioResponse;
import pe.edu.idat.biblioteca.entity.Prestamo;
import pe.edu.idat.biblioteca.entity.Usuario;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-13T18:06:47-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (IBM Corporation)"
)
@Component
public class UsuarioMapperImpl implements UsuarioMapper {

    @Autowired
    private PrestamoMapper prestamoMapper;

    @Override
    public UsuarioResponse toResponse(Usuario usuario) {
        if ( usuario == null ) {
            return null;
        }

        UsuarioResponse.UsuarioResponseBuilder usuarioResponse = UsuarioResponse.builder();

        usuarioResponse.id( usuario.getId() );
        usuarioResponse.nombre( usuario.getNombre() );
        usuarioResponse.apellido( usuario.getApellido() );
        usuarioResponse.email( usuario.getEmail() );
        usuarioResponse.estado( usuario.getEstado() );
        usuarioResponse.fechaRegistro( usuario.getFechaRegistro() );
        usuarioResponse.prestamos( prestamoListToPrestamoResponseList( usuario.getPrestamos() ) );

        usuarioResponse.roles( mapRoles(usuario.getRoles()) );

        return usuarioResponse.build();
    }

    @Override
    public Usuario toEntity(UsuarioRequest request) {
        if ( request == null ) {
            return null;
        }

        Usuario.UsuarioBuilder usuario = Usuario.builder();

        usuario.nombre( request.nombre() );
        usuario.apellido( request.apellido() );
        usuario.email( request.email() );

        return usuario.build();
    }

    @Override
    public void updateEntity(Usuario usuario, UsuarioRequest request) {
        if ( request == null ) {
            return;
        }

        usuario.setNombre( request.nombre() );
        usuario.setApellido( request.apellido() );
        usuario.setEmail( request.email() );
    }

    protected List<PrestamoResponse> prestamoListToPrestamoResponseList(List<Prestamo> list) {
        if ( list == null ) {
            return null;
        }

        List<PrestamoResponse> list1 = new ArrayList<PrestamoResponse>( list.size() );
        for ( Prestamo prestamo : list ) {
            list1.add( prestamoMapper.toResponse( prestamo ) );
        }

        return list1;
    }
}
