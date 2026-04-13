package pe.edu.idat.biblioteca.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.idat.biblioteca.dto.request.PrestamoRequest;
import pe.edu.idat.biblioteca.dto.response.PrestamoResponse;
import pe.edu.idat.biblioteca.entity.Libro;
import pe.edu.idat.biblioteca.entity.Prestamo;
import pe.edu.idat.biblioteca.entity.Usuario;
import pe.edu.idat.biblioteca.enums.EstadoPrestamo;
import pe.edu.idat.biblioteca.exception.BadRequestException;
import pe.edu.idat.biblioteca.exception.ResourceNotFoundException;
import pe.edu.idat.biblioteca.mapper.PrestamoMapper;
import pe.edu.idat.biblioteca.repository.LibroRepository;
import pe.edu.idat.biblioteca.repository.PrestamoRepository;
import pe.edu.idat.biblioteca.repository.UsuarioRepository;
import pe.edu.idat.biblioteca.service.PrestamoService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de préstamos.
 * Incluye lógica de negocio robusta: control de stock, límite de préstamos,
 * registro de devoluciones y detección de vencimientos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PrestamoServiceImpl implements PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;
    private final UsuarioRepository usuarioRepository;
    private final PrestamoMapper prestamoMapper;

    /** Límite máximo de préstamos activos por usuario */
    private static final int MAX_PRESTAMOS_ACTIVOS = 3;

    /**
     * Registra un nuevo préstamo.
     * Validaciones:
     * - El libro debe existir y tener stock disponible
     * - El usuario debe existir y estar activo
     * - El usuario no puede exceder el límite de préstamos activos
     */
    @Override
    @Transactional
    public PrestamoResponse registrarPrestamo(PrestamoRequest request) {
        // 1. Validar libro
        log.info("Iniciando registro de préstamo para el libro ID: {} por el usuario ID: {}", request.libroId(),
                request.usuarioId());
        Libro libro = libroRepository.findById(request.libroId())
                .orElseThrow(() -> {
                    log.error("Registro fallido. Libro con ID {} no encontrado.", request.libroId());
                    return new ResourceNotFoundException("Libro", "id", request.libroId());
                });

        if (libro.getStock() <= 0 || !libro.getDisponible()) {
            log.warn("Registro denegado. El libro '{}' (ID: {}) no tiene ejemplares disponibles. Stock actual: {}",
                    libro.getTitulo(), libro.getId(), libro.getStock());
            throw new BadRequestException(
                    "El libro '" + libro.getTitulo() + "' no tiene ejemplares disponibles.");
        }

        // 2. Validar usuario
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> {
                    log.error("Registro fallido. Usuario con ID {} no encontrado.", request.usuarioId());
                    return new ResourceNotFoundException("Usuario", "id", request.usuarioId());
                });

        if (!usuario.getEstado()) {
            log.warn("Registro denegado. El usuario '{}' (ID: {}) se encuentra desactivado.", usuario.getEmail(),
                    usuario.getId());
            throw new BadRequestException(
                    "El usuario '" + usuario.getNombre() + "' está desactivado y no puede realizar préstamos.");
        }

        // 3. Verificar límite de préstamos activos
        long prestamosActivos = prestamoRepository.contarPrestamosActivosPorUsuario(usuario.getId());
        if (prestamosActivos >= MAX_PRESTAMOS_ACTIVOS) {
            log.warn(
                    "Registro denegado. El usuario '{}' excedió el límite de préstamos pendientes ({} préstamos actuales).",
                    usuario.getEmail(), prestamosActivos);
            throw new BadRequestException(
                    "El usuario ya tiene " + MAX_PRESTAMOS_ACTIVOS +
                            " préstamos activos. Debe devolver algún libro antes de solicitar otro.");
        }

        // 4. Crear el préstamo
        int diasPrestamo = request.diasPrestamo() != null ? request.diasPrestamo() : 7;

        Prestamo prestamo = Prestamo.builder()
                .usuario(usuario)
                .libro(libro)
                .fechaPrestamo(LocalDate.now())
                .fechaDevolucionEsperada(LocalDate.now().plusDays(diasPrestamo))
                .estado(EstadoPrestamo.ACTIVO)
                .build();

        // 5. Decrementar stock del libro
        libro.setStock(libro.getStock() - 1);
        if (libro.getStock() == 0) {
            libro.setDisponible(false);
        }
        libroRepository.save(libro);

        // 6. Guardar el préstamo
        prestamo = prestamoRepository.save(prestamo);
        log.info("Préstamo registrado: libro '{}' → usuario '{}' (devolver antes de {})",
                libro.getTitulo(), usuario.getEmail(), prestamo.getFechaDevolucionEsperada());

        return prestamoMapper.toResponse(prestamo);
    }

    /**
     * Registra la devolución de un libro.
     * Actualiza el estado del préstamo y restaura el stock del libro.
     */
    @Override
    @Transactional
    public PrestamoResponse registrarDevolucion(Long prestamoId, String emailUsuario) {
        log.info("Iniciando proceso de devolución para el préstamo ID: {} efectuado por: {}", prestamoId, emailUsuario);
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> {
                    log.error("Devolución fallida. Préstamo con ID {} no encontrado.", prestamoId);
                    return new ResourceNotFoundException("Préstamo", "id", prestamoId);
                });

        // Verificar que el préstamo está activo
        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            log.warn("Devolución denegada. El préstamo ID {} ya se encontraba en estado DEVUELTO.", prestamoId);
            throw new BadRequestException("Este préstamo ya fue devuelto anteriormente.");
        }

        // Verificar que el usuario es el dueño del préstamo o es ADMIN
        // (La verificación de ADMIN se hace en el controlador)
        if (!prestamo.getUsuario().getEmail().equals(emailUsuario)) {
            // Verificar si es admin se delega al controlador con @PreAuthorize
            Usuario solicitante = usuarioRepository.findByEmail(emailUsuario)
                    .orElseThrow(() -> {
                        log.error("Autorización fallida. Usario solicitante '{}' no encontrado.", emailUsuario);
                        return new ResourceNotFoundException("Usuario", "email", emailUsuario);
                    });

            boolean esAdmin = solicitante.getRoles().stream()
                    .anyMatch(rol -> rol.getNombre().name().equals("ADMIN"));

            if (!esAdmin) {
                log.warn(
                        "Devolución denegada. Usuario '{}' intentó devolver un préstamo que no le pertenece (Préstamo ID {}).",
                        emailUsuario, prestamoId);
                throw new BadRequestException("Solo puede devolver sus propios préstamos.");
            }
        }

        // Registrar la devolución
        prestamo.setFechaDevolucionReal(LocalDate.now());

        // Determinar estado: devuelto o vencido (si se devolvió después de la fecha
        // esperada)
        if (LocalDate.now().isAfter(prestamo.getFechaDevolucionEsperada())) {
            prestamo.setEstado(EstadoPrestamo.VENCIDO);
            log.warn("Préstamo ID {} devuelto con retraso!", prestamoId);
        } else {
            prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        }

        // Restaurar stock del libro
        Libro libro = prestamo.getLibro();
        libro.setStock(libro.getStock() + 1);
        libro.setDisponible(true);
        libroRepository.save(libro);

        prestamo = prestamoRepository.save(prestamo);
        log.info("Devolución registrada: préstamo ID {} (libro '{}', estado: {})",
                prestamoId, libro.getTitulo(), prestamo.getEstado());

        return prestamoMapper.toResponse(prestamo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponse> obtenerMisPrestamos(String email) {
        log.info("Obteniendo historial de préstamos para el usuario: '{}'", email);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Búsqueda de historial fallida. Usuario '{}' no encontrado.", email);
                    return new ResourceNotFoundException("Usuario", "email", email);
                });

        return prestamoRepository.findByUsuarioId(usuario.getId()).stream()
                .map(prestamoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponse> listarTodos() {
        log.info("Extrayendo todos los préstamos acumulados en el sistema...");
        return prestamoRepository.findAll().stream()
                .map(prestamoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponse> obtenerVencidos() {
        log.info("Verificando préstamos vencidos al día de hoy: {}", LocalDate.now());
        return prestamoRepository.findPrestamosVencidos(LocalDate.now()).stream()
                .map(prestamoMapper::toResponse)
                .collect(Collectors.toList());
    }
}
