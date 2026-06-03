# Base de datos — GreengrocerApp

Scripts SQL para crear y poblar la base de datos `greengrocer_db` en MySQL 8.x.

## Orden de ejecución

1. `01_schema.sql` — crea la base de datos, tablas, claves foráneas, índices y restricciones.
2. `02_seed.sql`  — inserta categorías, proveedores, clientes y productos de ejemplo.

> **Importante:** el script de esquema hace `DROP DATABASE IF EXISTS greengrocer_db`.
> Si ya existe data productiva, respaldarla antes de ejecutar.

## Cómo ejecutar

### Desde la línea de comandos

```bash
mysql -u root -p < database/01_schema.sql
mysql -u root -p < database/02_seed.sql
```

### Desde MySQL Workbench

1. Abrir `01_schema.sql` y ejecutar todo (`Ctrl+Shift+Enter`).
2. Abrir `02_seed.sql` y ejecutar todo.

## Modelo de datos

```
categoria (1) ─< producto >─ (1) proveedor
                    │
                    └─< detalle_venta >── venta >── usuario
                                              └── cliente
```

- **categoria**: tipos de producto (Frutas, Verduras, …).
- **proveedor**: proveedores de mercadería.
- **producto**: artículos a la venta (precio, stock, unidad, categoría, proveedor).
- **cliente**: clientes registrados; las ventas de mostrador pueden quedar con `id_cliente = NULL`.
- **usuario**: cuentas de acceso al sistema con rol `ADMIN` o `VENDEDOR`.
- **venta**: cabecera con totales, método de pago, estado y comprobante.
- **detalle_venta**: ítems de cada venta. El borrado de una venta cascadea al detalle.

## Usuario administrador

No está en este SQL. La aplicación, al primer arranque, detecta si no
existe ningún ADMIN y crea uno por defecto:

| usuario | contraseña |
|---------|------------|
| admin   | admin123   |

**Cambiarla apenas se inicie sesión.**
