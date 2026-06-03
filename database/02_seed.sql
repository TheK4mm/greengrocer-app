-- =====================================================================
-- GreengrocerApp — Datos de prueba (semilla)
-- =====================================================================
-- Ejecutar DESPUÉS de 01_schema.sql.
-- El usuario administrador por defecto se crea automáticamente al
-- primer arranque de la aplicación (ver AuthService#ensureDefaultAdmin):
--     usuario: admin   contraseña: admin123
-- Por seguridad, cambiar la contraseña tras el primer ingreso.
-- =====================================================================

USE greengrocer_db;

-- Categorías
INSERT INTO categoria (nombre, descripcion) VALUES
    ('Frutas',      'Frutas frescas de temporada'),
    ('Verduras',    'Verduras y hortalizas frescas'),
    ('Tubérculos',  'Papas, camotes y derivados'),
    ('Legumbres',   'Granos secos y legumbres'),
    ('Hierbas',     'Hierbas aromáticas y especias frescas');

-- Proveedores
INSERT INTO proveedor (nombre, ruc, telefono, email, direccion) VALUES
    ('Mercado Mayorista La Parada',     '20512345678', '01-555-1234',
        'contacto@laparada.pe',  'Av. La Parada 123, La Victoria'),
    ('Distribuidora Frutícola del Sur', '20598765432', '01-555-5678',
        'ventas@frutisur.pe',    'Av. Tomás Marsano 4500, Surco'),
    ('AgroAndes SAC',                   '20611223344', '01-555-9012',
        'pedidos@agroandes.pe',  'Carretera Central km 18, Ate');

-- Cliente genérico de mostrador
INSERT INTO cliente (nombre, dni, telefono, email, direccion) VALUES
    ('Cliente Mostrador',  '00000000', '-',           '-',                       '-'),
    ('María Quispe',       '46781234', '987654321',   'maria.quispe@correo.pe',  'Jr. Las Flores 245'),
    ('Carlos Ramírez',     '07845612', '912345678',   'cramirez@correo.pe',      'Av. Brasil 1820');

-- Productos
INSERT INTO producto
    (codigo, nombre, descripcion, precio_compra, precio_venta, stock, stock_minimo, unidad_medida, id_categoria, id_proveedor)
VALUES
    ('PR-001', 'Manzana Roja',     'Manzana importada por kilo',           2.50, 4.00,  50, 10, 'kg',     1, 2),
    ('PR-002', 'Plátano de Seda',  'Plátano nacional dulce',               1.20, 2.50,  80, 15, 'kg',     1, 2),
    ('PR-003', 'Naranja de Jugo',  'Naranja Valencia para jugo',           1.80, 3.00,  60, 10, 'kg',     1, 2),
    ('PR-004', 'Tomate Italiano',  'Tomate maduro para ensalada',          1.80, 3.50,  40, 10, 'kg',     2, 1),
    ('PR-005', 'Lechuga Americana','Lechuga fresca, unidad grande',        1.00, 2.00,  30,  8, 'unidad', 2, 1),
    ('PR-006', 'Zanahoria',        'Zanahoria pelada lista para usar',     1.40, 2.50,  35, 10, 'kg',     2, 1),
    ('PR-007', 'Papa Amarilla',    'Papa amarilla, ideal para puré',       1.50, 3.00, 100, 20, 'kg',     3, 3),
    ('PR-008', 'Camote Morado',    'Camote morado, fuente de fibra',       1.30, 2.60,  60, 15, 'kg',     3, 3),
    ('PR-009', 'Lentejas',         'Lentejas peruanas a granel',           5.00, 7.50,  25,  5, 'kg',     4, 3),
    ('PR-010', 'Frijol Canario',   'Frijol canario seleccionado',          4.50, 7.00,  20,  5, 'kg',     4, 3),
    ('PR-011', 'Cilantro',         'Atado de cilantro fresco',             0.30, 0.80,  40, 10, 'atado',  5, 1),
    ('PR-012', 'Hierba Buena',     'Atado de hierba buena',                0.30, 0.80,  30, 10, 'atado',  5, 1);
