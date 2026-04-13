package pe.edu.idat.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;
import pe.edu.idat.biblioteca.enums.EstadoPrestamo;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa un préstamo de libro.
 * Relación ManyToOne con Usuario y Libro.
 * Maneja estados: ACTIVO, DEVUELTO, VENCIDO.
 */
@Entity
@Table(name = "prestamos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Fecha en que se realizó el préstamo */
    @Column(name = "fecha_prestamo", nullable = false, updatable = false)
    private LocalDate fechaPrestamo;

    /** Fecha y hora de la última actualización del registro */
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        this.fechaPrestamo = LocalDate.now();
        this.fechaActualizacion = LocalDateTime.now();
        if (this.estado == null) this.estado = EstadoPrestamo.ACTIVO;
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    /** Fecha esperada de devolución */
    @Column(name = "fecha_devolucion_esperada", nullable = false)
    private LocalDate fechaDevolucionEsperada;

    /** Fecha real de devolución (null si no se ha devuelto) */
    @Column(name = "fecha_devolucion_real")
    private LocalDate fechaDevolucionReal;

    /** Estado actual del préstamo */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoPrestamo estado = EstadoPrestamo.ACTIVO;

    // ======================== RELACIONES ========================

    /**
     * Relación ManyToOne con Usuario.
     * Muchos préstamos pueden pertenecer a un mismo usuario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    /**
     * Relación ManyToOne con Libro.
     * Muchos préstamos pueden referirse al mismo libro.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "libro_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Libro libro;
}
