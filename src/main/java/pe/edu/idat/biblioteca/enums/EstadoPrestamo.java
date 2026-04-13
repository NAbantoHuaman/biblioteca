package pe.edu.idat.biblioteca.enums;

/**
 * Enum que define los posibles estados de un préstamo.
 * ACTIVO: el libro aún no ha sido devuelto.
 * DEVUELTO: el libro fue devuelto exitosamente.
 * VENCIDO: la fecha de devolución esperada ha pasado sin devolución.
 */
public enum EstadoPrestamo {
    ACTIVO,
    DEVUELTO,
    VENCIDO
}
