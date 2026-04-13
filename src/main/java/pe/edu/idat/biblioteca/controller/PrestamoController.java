package pe.edu.idat.biblioteca.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.idat.biblioteca.dto.request.PrestamoRequest;
import pe.edu.idat.biblioteca.dto.response.ApiResponse;
import pe.edu.idat.biblioteca.dto.response.PrestamoResponse;
import pe.edu.idat.biblioteca.service.PrestamoService;

import java.util.List;

/**
 * Controlador de préstamos y devoluciones.
 * Registro de préstamo: solo ADMIN.
 * Devolución e historial propio: cualquier autenticado.
 * Listado y vencidos: solo ADMIN.
 */
@RestController
@RequestMapping("/api/prestamos")
@RequiredArgsConstructor
@Tag(name = "Préstamos", description = "Gestión de préstamos y devoluciones de libros")
@SecurityRequirement(name = "Bearer JWT")
public class PrestamoController {

    private final PrestamoService prestamoService;

    /**
     * Registra un nuevo préstamo de libro.
     * Solo accesible por usuarios con rol ADMIN.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Registrar préstamo [ADMIN]", description = "Registra un nuevo préstamo de libro para un usuario. Requiere rol ADMIN.")
    public ResponseEntity<ApiResponse<PrestamoResponse>> registrarPrestamo(
            @Valid @RequestBody PrestamoRequest request) {
        PrestamoResponse response = prestamoService.registrarPrestamo(request);
        return new ResponseEntity<>(ApiResponse.created("Préstamo registrado exitosamente", response),
                HttpStatus.CREATED);
    }

    /**
     * Registra la devolución de un libro.
     * ADMIN puede devolver cualquier préstamo.
     * USUARIO solo puede devolver sus propios préstamos.
     */
    @PutMapping("/devolver/{id}")
    @Operation(summary = "Registrar devolución", description = "Registra la devolución de un libro prestado. USUARIO solo puede devolver sus préstamos.")
    public ResponseEntity<ApiResponse<PrestamoResponse>> registrarDevolucion(
            @PathVariable Long id,
            Authentication authentication) {
        PrestamoResponse response = prestamoService.registrarDevolucion(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Devolución registrada exitosamente", response));
    }

    /**
     * Obtiene el historial de préstamos del usuario autenticado.
     */
    @GetMapping("/mis-prestamos")
    @Operation(summary = "Mi historial de préstamos", description = "Retorna todos los préstamos (activos y devueltos) del usuario autenticado.")
    public ResponseEntity<ApiResponse<List<PrestamoResponse>>> obtenerMisPrestamos(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success("Historial de préstamos recuperado",
                prestamoService.obtenerMisPrestamos(authentication.getName())));
    }

    /**
     * Lista todos los préstamos del sistema. Solo ADMIN.
     */
    @GetMapping("/todos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos los préstamos [ADMIN]", description = "Retorna la lista completa de préstamos del sistema. Requiere rol ADMIN.")
    public ResponseEntity<ApiResponse<List<PrestamoResponse>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.success("Listado general de préstamos", prestamoService.listarTodos()));
    }

    /**
     * Obtiene los préstamos vencidos (no devueltos a tiempo). Solo ADMIN.
     */
    @GetMapping("/vencidos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Préstamos vencidos [ADMIN]", description = "Retorna los préstamos activos que han excedido su fecha de devolución. Requiere rol ADMIN.")
    public ResponseEntity<ApiResponse<List<PrestamoResponse>>> obtenerVencidos() {
        return ResponseEntity
                .ok(ApiResponse.success("Listado de préstamos vencidos", prestamoService.obtenerVencidos()));
    }
}
