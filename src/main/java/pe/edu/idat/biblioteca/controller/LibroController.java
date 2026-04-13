package pe.edu.idat.biblioteca.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.idat.biblioteca.dto.request.LibroRequest;
import pe.edu.idat.biblioteca.dto.response.ApiResponse;
import pe.edu.idat.biblioteca.dto.response.LibroResponse;
import pe.edu.idat.biblioteca.service.LibroService;

import java.util.List;

/**
 * Controlador de libros.
 * GET: público | POST, PUT, DELETE: solo ADMIN.
 */
@RestController
@RequestMapping("/api/libros")
@RequiredArgsConstructor
@Tag(name = "Libros", description = "Gestión del catálogo de libros (CRUD)")
public class LibroController {

    private final LibroService libroService;

    // ======================== RUTAS PÚBLICAS (GET) ========================

    /**
     * Lista todos los libros del catálogo.
     */
    @GetMapping
    @Operation(summary = "Listar todos los libros", description = "Retorna el catálogo completo de libros. Acceso público.")
    public ResponseEntity<ApiResponse<List<LibroResponse>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.success("Libros obtenidos exitosamente", libroService.listarTodos()));
    }

    /**
     * Obtiene un libro por su ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener libro por ID", description = "Retorna la información detallada de un libro. Acceso público.")
    public ResponseEntity<ApiResponse<LibroResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Libro encontrado", libroService.obtenerPorId(id)));
    }

    /**
     * Busca libros por término (título o autor).
     */
    @GetMapping("/buscar")
    @Operation(summary = "Buscar libros", description = "Busca libros por título o autor que coincidan con el término. Acceso público.")
    public ResponseEntity<ApiResponse<List<LibroResponse>>> buscar(@RequestParam String termino) {
        return ResponseEntity.ok(ApiResponse.success("Resultados de la búsqueda", libroService.buscar(termino)));
    }

    /**
     * Lista solo los libros disponibles (con stock > 0).
     */
    @GetMapping("/disponibles")
    @Operation(summary = "Listar libros disponibles", description = "Retorna solo los libros con stock disponible para préstamo. Acceso público.")
    public ResponseEntity<ApiResponse<List<LibroResponse>>> listarDisponibles() {
        return ResponseEntity.ok(ApiResponse.success("Libros disponibles", libroService.listarDisponibles()));
    }

    /**
     * Busca libros por género literario.
     */
    @GetMapping("/genero/{genero}")
    @Operation(summary = "Buscar por género", description = "Retorna libros filtrados por género literario. Acceso público.")
    public ResponseEntity<ApiResponse<List<LibroResponse>>> buscarPorGenero(@PathVariable String genero) {
        return ResponseEntity.ok(ApiResponse.success("Resultados por género", libroService.buscarPorGenero(genero)));
    }

    // ======================== RUTAS PROTEGIDAS (ADMIN) ========================

    /**
     * Registra un nuevo libro en el catálogo.
     * Solo accesible por usuarios con rol ADMIN.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear libro [ADMIN]", description = "Registra un nuevo libro en el catálogo. Requiere rol ADMIN.", security = @SecurityRequirement(name = "Bearer JWT"))
    public ResponseEntity<ApiResponse<LibroResponse>> crear(@Valid @RequestBody LibroRequest request) {
        LibroResponse response = libroService.crear(request);
        return new ResponseEntity<>(ApiResponse.created("Libro registrado exitosamente", response), HttpStatus.CREATED);
    }

    /**
     * Actualiza un libro existente.
     * Solo accesible por usuarios con rol ADMIN.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar libro [ADMIN]", description = "Actualiza la información de un libro existente. Requiere rol ADMIN.", security = @SecurityRequirement(name = "Bearer JWT"))
    public ResponseEntity<ApiResponse<LibroResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody LibroRequest request) {
        return ResponseEntity
                .ok(ApiResponse.success("Libro actualizado exitosamente", libroService.actualizar(id, request)));
    }

    /**
     * Elimina un libro del catálogo.
     * Solo accesible por usuarios con rol ADMIN.
     * No se puede eliminar si tiene préstamos activos.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar libro [ADMIN]", description = "Elimina un libro del catálogo (si no tiene préstamos activos). Requiere rol ADMIN.", security = @SecurityRequirement(name = "Bearer JWT"))
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        libroService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success("Libro eliminado exitosamente.", null));
    }
}
