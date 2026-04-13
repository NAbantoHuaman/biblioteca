package pe.edu.idat.biblioteca.enums;

/**
 * Enum que define los roles disponibles en el sistema.
 * ADMIN: acceso total (CRUD libros, gestión préstamos, usuarios).
 * USUARIO: acceso limitado (consulta catálogo, historial propio).
 */
public enum RolNombre {
    ADMIN,
    USUARIO
}
