-- =====================================================================
-- GreengrocerApp — Esquema relacional
-- Motor: MySQL 8.x  /  InnoDB  /  utf8mb4
-- =====================================================================
-- Ejecutar este script PRIMERO. Crea la base de datos y todas las tablas.
-- Para datos de ejemplo, ejecutar 02_seed.sql después.
-- =====================================================================

DROP DATABASE IF EXISTS greengrocer_db;
CREATE DATABASE greengrocer_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE greengrocer_db;

-- ---------------------------------------------------------------------
-- Categorías de producto
-- ---------------------------------------------------------------------
CREATE TABLE categoria (
    id_categoria   INT          NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(60)  NOT NULL,
    descripcion    VARCHAR(200) NULL,
    activo         TINYINT(1)   NOT NULL DEFAULT 1,
    fecha_creacion DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_categoria PRIMARY KEY (id_categoria),
    CONSTRAINT uq_categoria_nombre UNIQUE (nombre)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Proveedores
-- ---------------------------------------------------------------------
CREATE TABLE proveedor (
    id_proveedor   INT          NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(120) NOT NULL,
    ruc            VARCHAR(20)  NULL,
    telefono       VARCHAR(20)  NULL,
    email          VARCHAR(120) NULL,
    direccion      VARCHAR(200) NULL,
    activo         TINYINT(1)   NOT NULL DEFAULT 1,
    fecha_creacion DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_proveedor PRIMARY KEY (id_proveedor),
    CONSTRAINT uq_proveedor_ruc UNIQUE (ruc)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Productos
-- ---------------------------------------------------------------------
CREATE TABLE producto (
    id_producto    INT           NOT NULL AUTO_INCREMENT,
    codigo         VARCHAR(20)   NOT NULL,
    nombre         VARCHAR(120)  NOT NULL,
    descripcion    VARCHAR(300)  NULL,
    precio_compra  DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    precio_venta   DECIMAL(10,2) NOT NULL,
    stock          INT           NOT NULL DEFAULT 0,
    stock_minimo   INT           NOT NULL DEFAULT 5,
    unidad_medida  VARCHAR(20)   NOT NULL DEFAULT 'kg',
    id_categoria   INT           NOT NULL,
    id_proveedor   INT           NULL,
    activo         TINYINT(1)    NOT NULL DEFAULT 1,
    fecha_registro DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_producto PRIMARY KEY (id_producto),
    CONSTRAINT uq_producto_codigo UNIQUE (codigo),
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (id_categoria) REFERENCES categoria (id_categoria)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_producto_proveedor
        FOREIGN KEY (id_proveedor) REFERENCES proveedor (id_proveedor)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT chk_producto_precio CHECK (precio_venta >= 0 AND precio_compra >= 0),
    CONSTRAINT chk_producto_stock  CHECK (stock >= 0 AND stock_minimo >= 0)
) ENGINE=InnoDB;

CREATE INDEX ix_producto_nombre   ON producto (nombre);
CREATE INDEX ix_producto_categoria ON producto (id_categoria);

-- ---------------------------------------------------------------------
-- Clientes
-- ---------------------------------------------------------------------
CREATE TABLE cliente (
    id_cliente     INT          NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(120) NOT NULL,
    dni            VARCHAR(20)  NULL,
    telefono       VARCHAR(20)  NULL,
    email          VARCHAR(120) NULL,
    direccion      VARCHAR(200) NULL,
    activo         TINYINT(1)   NOT NULL DEFAULT 1,
    fecha_creacion DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_cliente PRIMARY KEY (id_cliente),
    CONSTRAINT uq_cliente_dni UNIQUE (dni)
) ENGINE=InnoDB;

CREATE INDEX ix_cliente_nombre ON cliente (nombre);

-- ---------------------------------------------------------------------
-- Usuarios del sistema (con autenticación)
-- ---------------------------------------------------------------------
CREATE TABLE usuario (
    id_usuario     INT          NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(120) NOT NULL,
    nombre_usuario VARCHAR(50)  NOT NULL,
    password_hash  VARCHAR(255) NOT NULL,
    password_salt  VARCHAR(255) NOT NULL,
    rol            ENUM('ADMIN','VENDEDOR') NOT NULL DEFAULT 'VENDEDOR',
    activo         TINYINT(1)   NOT NULL DEFAULT 1,
    fecha_creacion DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultimo_acceso  DATETIME     NULL,
    CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuario_nombre_usuario UNIQUE (nombre_usuario)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Ventas (cabecera)
-- ---------------------------------------------------------------------
CREATE TABLE venta (
    id_venta           INT           NOT NULL AUTO_INCREMENT,
    numero_comprobante VARCHAR(20)   NOT NULL,
    id_cliente         INT           NULL,
    id_usuario         INT           NOT NULL,
    fecha_venta        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    subtotal           DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    impuesto           DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total              DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    metodo_pago        ENUM('EFECTIVO','TARJETA','TRANSFERENCIA','YAPE_PLIN')
                           NOT NULL DEFAULT 'EFECTIVO',
    estado             ENUM('COMPLETADA','ANULADA') NOT NULL DEFAULT 'COMPLETADA',
    observaciones      VARCHAR(300)  NULL,
    CONSTRAINT pk_venta PRIMARY KEY (id_venta),
    CONSTRAINT uq_venta_comprobante UNIQUE (numero_comprobante),
    CONSTRAINT fk_venta_cliente
        FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_venta_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE INDEX ix_venta_fecha ON venta (fecha_venta);

-- ---------------------------------------------------------------------
-- Detalle de ventas
-- ---------------------------------------------------------------------
CREATE TABLE detalle_venta (
    id_detalle      INT           NOT NULL AUTO_INCREMENT,
    id_venta        INT           NOT NULL,
    id_producto     INT           NOT NULL,
    cantidad        DECIMAL(10,3) NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal        DECIMAL(10,2) NOT NULL,
    CONSTRAINT pk_detalle_venta PRIMARY KEY (id_detalle),
    CONSTRAINT fk_detalle_venta
        FOREIGN KEY (id_venta) REFERENCES venta (id_venta)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (id_producto) REFERENCES producto (id_producto)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_detalle_cantidad CHECK (cantidad > 0),
    CONSTRAINT chk_detalle_precio   CHECK (precio_unitario >= 0)
) ENGINE=InnoDB;
