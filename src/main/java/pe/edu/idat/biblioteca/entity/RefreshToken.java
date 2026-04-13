package pe.edu.idat.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Entidad que representa un refresh token para renovar access tokens JWT.
 * Permite la rotación de tokens y revocación de sesiones.
 */
@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Token opaco único */
    @Column(nullable = false, unique = true)
    private String token;

    /** Fecha de expiración del refresh token */
    @Column(name = "fecha_expiracion", nullable = false)
    private Instant fechaExpiracion;

    // ======================== RELACIONES ========================

    /**
     * Relación OneToOne con Usuario.
     * Cada usuario tiene un único refresh token activo a la vez.
     */
    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false)
    private Usuario usuario;
}
