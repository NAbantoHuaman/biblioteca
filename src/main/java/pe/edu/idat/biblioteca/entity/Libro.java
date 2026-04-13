package pe.edu.idat.biblioteca.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad que representa un libro en el catálogo de la biblioteca.
 * Incluye validaciones complejas, campo de stock y relación OneToMany con Prestamo.
 */
@Entity
@Table(name = "libros", uniqueConstraints = {
        @UniqueConstraint(columnNames = "isbn")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Título del libro */
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 1, max = 200, message = "El título debe tener entre 1 y 200 caracteres")
    @Column(nullable = false, length = 200)
    private String titulo;

    /** Autor del libro */
    @NotBlank(message = "El autor es obligatorio")
    @Size(min = 2, max = 150, message = "El autor debe tener entre 2 y 150 caracteres")
    @Column(nullable = false, length = 150)
    private String autor;

    /** ISBN único del libro (International Standard Book Number) */
    @NotBlank(message = "El ISBN es obligatorio")
    @Size(min = 10, max = 17, message = "El ISBN debe tener entre 10 y 17 caracteres")
    @Column(nullable = false, unique = true, length = 17)
    private String isbn;

    /** Editorial que publicó el libro */
    @NotBlank(message = "La editorial es obligatoria")
    @Size(max = 100, message = "La editorial no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String editorial;

    /** Año de publicación */
    @NotNull(message = "El año de publicación es obligatorio")
    @Min(value = 1000, message = "El año debe ser válido")
    @Max(value = 2100, message = "El año no puede ser mayor a 2100")
    @Column(name = "anio_publicacion", nullable = false)
    private Integer anioPublicacion;

    /** Género literario del libro */
    @NotBlank(message = "El género es obligatorio")
    @Size(max = 50, message = "El género no puede exceder 50 caracteres")
    @Column(nullable = false, length = 50)
    private String genero;

    /** Cantidad de ejemplares disponibles */
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 1;

    /** Indica si el libro está disponible para préstamo (stock > 0) */
    @Column(nullable = false)
    @Builder.Default
    private Boolean disponible = true;

    /** Fecha y hora en que se registró el libro */
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    /** Fecha y hora de la última actualización */
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if (this.stock == null) this.stock = 1;
        if (this.disponible == null) this.disponible = (this.stock > 0);
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
        if (this.stock != null) {
            this.disponible = (this.stock > 0);
        }
    }

    // ======================== RELACIONES ========================

    /**
     * Relación OneToMany con Prestamo.
     * Un libro puede estar asociado a múltiples préstamos (en distintas fechas).
     */
    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Prestamo> prestamos;
}
