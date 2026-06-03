package com.greengrocer.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/** Artículo a la venta en la verdulería. */
public class Producto {

    private int id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precioCompra = BigDecimal.ZERO;
    private BigDecimal precioVenta  = BigDecimal.ZERO;
    private int stock;
    private int stockMinimo = 5;
    private String unidadMedida = "kg";
    private boolean activo = true;
    private LocalDateTime fechaRegistro;

    // Relaciones
    private int idCategoria;
    private String nombreCategoria;     // poblado por joins, opcional
    private Integer idProveedor;        // puede ser null
    private String nombreProveedor;     // poblado por joins, opcional

    public Producto() { }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; }

    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }

    public Integer getIdProveedor() { return idProveedor; }
    public void setIdProveedor(Integer idProveedor) { this.idProveedor = idProveedor; }

    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }

    public boolean isStockCritico() { return stock <= stockMinimo; }
    public boolean isStockAgotado() { return stock <= 0; }

    @Override public String toString() {
        return codigo + " · " + nombre;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof Producto other)) return false;
        return id == other.id;
    }

    @Override public int hashCode() { return Objects.hash(id); }
}
