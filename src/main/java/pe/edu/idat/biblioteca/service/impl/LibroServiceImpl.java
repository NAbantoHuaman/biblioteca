package pe.edu.idat.biblioteca.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.idat.biblioteca.dto.request.LibroRequest;
import pe.edu.idat.biblioteca.dto.response.LibroResponse;
import pe.edu.idat.biblioteca.entity.Libro;
import pe.edu.idat.biblioteca.exception.BadRequestException;
import pe.edu.idat.biblioteca.exception.ResourceNotFoundException;
import pe.edu.idat.biblioteca.mapper.LibroMapper;
import pe.edu.idat.biblioteca.repository.LibroRepository;
import pe.edu.idat.biblioteca.repository.PrestamoRepository;
import pe.edu.idat.biblioteca.service.LibroService;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.idat.biblioteca.service.impl.BaseServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de libros.
 * Hereda la lógica CRUD base y la especializa con validaciones de negocio.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LibroServiceImpl extends BaseServiceImpl<Libro, LibroRequest, LibroResponse, Long>
        implements LibroService {

    private final LibroRepository libroRepository;
    private final PrestamoRepository prestamoRepository;
    private final LibroMapper libroMapper;

    @Override
    protected JpaRepository<Libro, Long> getRepository() {
        return libroRepository;
    }

    @Override
    protected LibroResponse mapToResponse(Libro entity) {
        return libroMapper.toResponse(entity);
    }

    @Override
    protected Libro mapToEntity(LibroRequest request) {
        return libroMapper.toEntity(request);
    }

    @Override
    protected void updateEntityFromRequest(LibroRequest request, Libro entity) {
        libroMapper.updateEntity(entity, request);
    }

    @Override
    protected String getEntityName() {
        return "Libro";
    }

    @Override
    @Transactional
    public LibroResponse crear(LibroRequest request) {
        log.info("Iniciando creación de libro con ISBN: {}", request.isbn());
        if (libroRepository.existsByIsbn(request.isbn())) {
            log.error("Error en validación. Ya existe un libro con el ISBN: {}", request.isbn());
            throw new BadRequestException("Ya existe un libro con el ISBN: " + request.isbn());
        }
        return super.crear(request);
    }

    @Override
    @Transactional
    public LibroResponse actualizar(Long id, LibroRequest request) {
        log.info("Iniciando actualización de libro con ID: {}", id);
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getEntityName(), "id", id));

        if (!libro.getIsbn().equals(request.isbn()) && libroRepository.existsByIsbn(request.isbn())) {
            log.error("Error en validación al actualizar. Ya existe otro libro con el ISBN: {}", request.isbn());
            throw new BadRequestException("Ya existe otro libro con el ISBN: " + request.isbn());
        }

        return super.actualizar(id, request);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Iniciando el proceso de eliminación para el libro con ID: {}", id);
        if (prestamoRepository.existePrestamoActivoPorLibro(id)) {
            log.warn("Eliminación denegada. El libro (ID: {}) tiene préstamos activos.", id);
            throw new BadRequestException("No se puede eliminar el libro porque tiene préstamos activos.");
        }
        super.eliminar(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroResponse> buscar(String termino) {
        log.info("Buscando libros mediante el término (título/autor): '{}'", termino);
        return libroRepository.buscarPorTituloOAutor(termino).stream()
                .map(libroMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroResponse> listarDisponibles() {
        log.info("Obteniendo la lista de libros con stock disponible...");
        return libroRepository.findByDisponibleTrue().stream()
                .map(libroMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroResponse> buscarPorGenero(String genero) {
        log.info("Filtrando el catálogo para el género: '{}'", genero);
        return libroRepository.findByGeneroIgnoreCase(genero).stream()
                .map(libroMapper::toResponse)
                .collect(Collectors.toList());
    }
}
