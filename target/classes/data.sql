-- ============================
-- DATOS INICIALES
-- Se ejecuta automáticamente al iniciar la aplicación
-- ============================

-- Insertar roles si no existen
INSERT INTO roles (nombre) VALUES ('ADMIN') ON CONFLICT (nombre) DO NOTHING;
INSERT INTO roles (nombre) VALUES ('USUARIO') ON CONFLICT (nombre) DO NOTHING;

-- Insertar o actualizar usuario administrador por defecto
-- Email: admin@biblioteca.edu.pe | Password: Admin123!
-- El password está encriptado con BCrypt
INSERT INTO usuarios (nombre, apellido, email, password, estado, fecha_registro)
VALUES ('Administrador', 'Sistema', 'admin@biblioteca.edu.pe',
        '$2a$10$XmuUbq.DcrDvZsKfizA2RunhFQJBKPk6rKb6yR4s3s4dEv9QcmuSC',
        true, NOW())
ON CONFLICT (email) DO UPDATE SET 
    password = EXCLUDED.password,
    estado = EXCLUDED.estado;

-- Asignar rol ADMIN al usuario administrador
INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id FROM usuarios u, roles r
WHERE u.email = 'admin@biblioteca.edu.pe' AND r.nombre = 'ADMIN'
ON CONFLICT DO NOTHING;

-- Insertar libros de ejemplo
INSERT INTO libros (titulo, autor, isbn, editorial, anio_publicacion, genero, stock, disponible, fecha_registro)
VALUES
    ('Cien años de soledad', 'Gabriel García Márquez', '978-0307474728', 'Editorial Sudamericana', 1967, 'Realismo Mágico', 5, true, NOW()),
    ('Don Quijote de la Mancha', 'Miguel de Cervantes', '978-8420412146', 'Real Academia Española', 1605, 'Novela', 3, true, NOW()),
    ('El principito', 'Antoine de Saint-Exupéry', '978-0156012195', 'Reynal & Hitchcock', 1943, 'Literatura Infantil', 7, true, NOW()),
    ('La ciudad y los perros', 'Mario Vargas Llosa', '978-8420471839', 'Seix Barral', 1963, 'Novela', 4, true, NOW()),
    ('Rayuela', 'Julio Cortázar', '978-8437604572', 'Editorial Sudamericana', 1963, 'Novela Experimental', 2, true, NOW())
ON CONFLICT (isbn) DO NOTHING;
