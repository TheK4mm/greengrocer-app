package com.greengrocer.model;

import java.math.BigDecimal;

/** Línea de una venta (un producto, su cantidad y subtotal). */
public class DetalleVenta {

    private int id;
    private int idVenta;
    private int idProducto;
    private String codigoProducto;    // poblado por joins
    private String nombreProducto;    // poblado por joins
    private String unidadMedida;      // poblado por joins
    private BigDecimal cantidad        = BigDecimal.ZERO;
    private BigDecimal precioUnitario  = BigDecimal.ZERO;
    private BigDecimal subtotal        = BigDecimal.ZERO;

    public DetalleVenta() { }

    public DetalleVenta(int idProducto, BigDecimal cantidad, BigDecimal precioUnitario) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        recalcularSubtotal();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getCodigoProducto() { return codigoProducto; }
    public void setCodigoProducto(String codigoProducto) { this.codigoProducto = codigoProducto; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }

    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
        recalcularSubtotal();
    }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        recalcularSubtotal();
    }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public void recalcularSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            this.subtotal = cantidad.multiply(precioUnitario);
        }
    }
}
