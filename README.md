# GreengrocerApp · Sistema de gestión para verdulería

Aplicación de escritorio para administrar una verdulería: catálogo de productos, control de inventario, clientes, proveedores, punto de venta, historial de ventas y reportes. Construida con **Java + Swing + MySQL** bajo una arquitectura por capas (Modelo / DAO / Servicio / Vista) sobre la base del paradigma de Programación Orientada a Objetos.

> Reescritura completa del proyecto original. La versión anterior usaba
> JFrames con SQL embebido y contraseñas en texto plano; ésta separa
> responsabilidades, hashea contraseñas con PBKDF2 y suma módulos de
> ventas, proveedores, clientes y reportes.

---

## Tecnologías

| Capa            | Tecnología                                    |
|-----------------|------------------------------------------------|
| Lenguaje        | Java 21+ (compilado a 24)                     |
| Interfaz        | `javax.swing` con componentes flat propios    |
| Persistencia    | MySQL 8.x vía JDBC                            |
| Driver          | `mysql-connector-j` 9.7.0 (incluido en `lib/`) |
| Build           | Apache Ant / NetBeans (`build.xml` incluido)  |
| Seguridad       | PBKDF2-HMAC-SHA256 + salt por usuario         |

---

## Funcionalidades

- **Autenticación** con roles `ADMIN` y `VENDEDOR`. Contraseñas
  hasheadas (nunca se guardan en texto plano).
- **Dashboard** con métricas en vivo: ingresos del día/mes, ventas del
  día, productos con stock crítico.
- **Productos** con código único, categoría, proveedor, precios de
  compra/venta, stock y stock mínimo. Alertas visuales por stock bajo.
- **Categorías** y **proveedores** administrables.
- **Clientes** con búsqueda incremental por nombre o DNI.
- **Punto de venta (POS)**: carrito, cálculo de impuestos,
  comprobante autogenerado, descuento de stock transaccional.
- **Historial de ventas** filtrable por rango de fechas, con vista de
  detalle y anulación (solo administrador, restaura el stock).
- **Reportes** con productos más vendidos y listado de stock crítico.
- **Gestión de usuarios** (solo administrador): alta, edición de rol,
  restablecimiento de contraseña, activación/desactivación.

---

## Arquitectura

```
src/com/greengrocer/
├── config/         AppConfig — carga app.properties
├── util/           DatabaseConnection (singleton), PasswordHasher,
│                   ValidationUtils, CurrencyUtils
├── exception/      DataAccessException, BusinessException
├── model/          Entidades del dominio
│   ├── Persona (abstracta) ── Usuario / Cliente / Proveedor
│   ├── Producto, Categoria
│   ├── Venta, DetalleVenta
│   └── enums/      RolUsuario, MetodoPago, EstadoVenta
├── dao/            GenericDao<T,ID> + impl por tabla
├── service/        Reglas de negocio, validación y transacciones
└── view/           Interfaz gráfica
    ├── theme/      Paleta de colores y tipografías
    ├── components/ Botones, tarjetas, tablas y formularios reusables
    ├── icons/      Íconos vectoriales (Java2D)
    ├── panels/     Pantallas (dashboard, productos, ventas, …)
    ├── Main, LoginFrame, MainDashboard
```

Flujo típico de una petición:

```
Vista (Panel)  →  Service  →  DAO  →  DatabaseConnection (JDBC)
        ←   Modelo / BusinessException   ←
```

- **DAO**: SQL parametrizado con `PreparedStatement`, sin lógica de
  presentación. Lanza `DataAccessException` en errores irrecuperables.
- **Service**: aplica validaciones, traduce errores de BD en mensajes
  de negocio (`BusinessException`) y orquesta transacciones (por
  ejemplo, al registrar una venta se descuenta stock en la misma
  transacción).
- **Vista**: solo conoce al Service; no interpreta SQL ni excepciones
  del driver.

---

## Requisitos

- **JDK 21+** (testeado con OpenJDK Temurin 25)
- **MySQL Server 8.x** corriendo en `localhost:3306`
- **NetBeans 17+ con Ant** *o* `javac`/`java` desde consola

---

## Instalación

### 1. Crear la base de datos

```bash
mysql -u root -p < database/01_schema.sql
mysql -u root -p < database/02_seed.sql
```

El primer script borra y recrea `greengrocer_db`. El segundo inserta
categorías, productos, proveedores y clientes de ejemplo.

### 2. Configurar credenciales

Editar `src/app.properties`:

```properties
db.host=localhost
db.port=3306
db.name=greengrocer_db
db.user=root
db.password=tu_password_aqui

business.name=Verdulería La Cosecha
business.taxRate=0.19
business.currencySymbol=$
```

### 3. Compilar y ejecutar

#### Opción A — NetBeans

1. Abrir el proyecto en NetBeans (detecta el `nbproject/`).
2. `Run > Clean and Build Project` (genera `dist/Verduleria.jar`).
3. `Run > Run Project`.

El driver MySQL ya está vendido en `lib/mysql-connector-j-9.7.0.jar`
y referenciado por `nbproject/project.properties`.

#### Opción B — Línea de comandos

```bash
# Compilar en windows (desde la raíz del proyecto)
javac -d build/classes -cp "lib/mysql-connector-j-9.7.0.jar" (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object {$_.FullName})

# Copiar el archivo de configuración al classpath compilado
copy src/app.properties build/classes/

# Ejecutar
java -cp "build/classes;lib/mysql-connector-j-9.7.0.jar" com.greengrocer.view.Main
```

> En Windows con CMD/PowerShell, reemplazar el separador `;` se mantiene;
> en Linux/Mac se usa `:`.

### 4. Primer inicio de sesión

Al primer arranque, si la tabla `usuario` está vacía, la aplicación
crea automáticamente un administrador por defecto:

| Usuario | Contraseña |
|---------|-----------|
| `admin` | `admin123` |

**Cambiar la contraseña apenas se inicie sesión** desde el módulo
*Usuarios* (panel disponible solo para administradores).

---

## Modelo de datos

```
categoria (1) ─< producto >─ (1) proveedor
                    │
                    └─< detalle_venta >── venta >── usuario
                                              └── cliente (opcional)
```

- `producto.id_categoria` es obligatorio (FK con `ON DELETE RESTRICT`).
- `producto.id_proveedor` es opcional (FK con `ON DELETE SET NULL`).
- `venta.id_cliente` es opcional (venta de mostrador → `NULL`).
- Borrar una `venta` elimina sus `detalle_venta` (cascada).
- Las ventas se descuentan del stock con `UPDATE … WHERE stock >= ?`
  para evitar sobreventa.

Ver `database/01_schema.sql` para el detalle completo (CHECKs, índices,
tipos, etc.).

---

## Seguridad

- Contraseñas almacenadas con **PBKDF2-HMAC-SHA256**, 100 000
  iteraciones, salt de 16 bytes único por usuario.
- Verificación en tiempo constante para evitar timing attacks.
- Todas las consultas usan `PreparedStatement` (sin string-concatenation
  SQL).
- Las anulaciones de venta requieren rol `ADMIN`.

---

## Estructura del repositorio

```
greengrocer-app/
├── README.md
├── build.xml                 # Ant (generado por NetBeans)
├── manifest.mf
├── nbproject/                # configuración de proyecto NetBeans
├── lib/
│   └── mysql-connector-j-9.7.0.jar
├── database/
│   ├── 01_schema.sql
│   ├── 02_seed.sql
│   └── README.md
└── src/
    ├── app.properties        # configuración de conexión / negocio
    └── com/greengrocer/      # código Java
```

---

## Notas para evolucionar

- Sustituir el singleton de conexión por un pool (HikariCP) si la
  aplicación se vuelve multiusuario concurrente.
- Reemplazar `JOptionPane` por toasts no modales en `Toast` (la API ya
  está abstraída, sería un cambio interno).
- Exportar reportes a PDF/Excel (Apache POI / iText).
- Migrar a una build moderna (Maven o Gradle) si se requiere CI/CD.
