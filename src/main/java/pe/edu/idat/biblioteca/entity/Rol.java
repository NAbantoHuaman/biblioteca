package pe.edu.idat.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;
import pe.edu.idat.biblioteca.enums.RolNombre;

/**
 * Entidad que representa un rol del sistema.
 * Los roles determinan los permisos de acceso a los endpoints.
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del rol (ADMIN o USUARIO).
     * Se almacena como String en la BD.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 20)
    private RolNombre nombre;
}
