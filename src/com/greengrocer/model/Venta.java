package com.greengrocer.model;

import com.greengrocer.model.enums.EstadoVenta;
import com.greengrocer.model.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Cabecera de una venta (boleta / comprobante). */
public class Venta {

    private int id;
    private String numeroComprobante;
    private Integer idCliente;          // nullable: venta de mostrador
    private String nombreCliente;       // poblado por joins
    private int idUsuario;
    private String nombreUsuario;       // poblado por joins
    private LocalDateTime fechaVenta;
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal impuesto = BigDecimal.ZERO;
    private BigDecimal total    = BigDecimal.ZERO;
    private MetodoPago metodoPago = MetodoPago.EFECTIVO;
    private EstadoVenta estado    = EstadoVenta.COMPLETADA;
    private String observaciones;

    private final List<DetalleVenta> detalles = new ArrayList<>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumeroComprobante() { return numeroComprobante; }
    public void setNumeroComprobante(String numeroComprobante) { this.numeroComprobante = numeroComprobante; }

    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public LocalDateTime getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDateTime fechaVenta) { this.fechaVenta = fechaVenta; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getImpuesto() { return impuesto; }
    public void setImpuesto(BigDecimal impuesto) { this.impuesto = impuesto; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }

    public EstadoVenta getEstado() { return estado; }
    public void setEstado(EstadoVenta estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public List<DetalleVenta> getDetalles() { return detalles; }
}
