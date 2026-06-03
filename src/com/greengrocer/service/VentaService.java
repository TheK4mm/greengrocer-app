package com.greengrocer.service;

import com.greengrocer.config.AppConfig;
import com.greengrocer.dao.VentaDao;
import com.greengrocer.exception.BusinessException;
import com.greengrocer.model.DetalleVenta;
import com.greengrocer.model.Usuario;
import com.greengrocer.model.Venta;
import com.greengrocer.model.enums.EstadoVenta;
import com.greengrocer.model.enums.MetodoPago;
import com.greengrocer.util.CurrencyUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class VentaService {

    private final VentaDao dao = new VentaDao();

    public String siguienteComprobante() { return dao.siguienteNumeroComprobante(); }

    /** Crea una venta a partir de una lista de detalles (carrito). */
    public Venta registrar(Integer idCliente, MetodoPago metodoPago,
                           String observaciones, List<DetalleVenta> detalles) {
        Usuario u = Session.get();
        if (u == null) throw new BusinessException("Debe iniciar sesión para registrar ventas.");
        if (detalles == null || detalles.isEmpty()) {
            throw new BusinessException("La venta debe contener al menos un producto.");
        }
        BigDecimal subtotal = BigDecimal.ZERO;
        for (DetalleVenta d : detalles) {
            if (d.getCantidad() == null || d.getCantidad().signum() <= 0) {
                throw new BusinessException("Cantidad inválida en el producto "
                        + (d.getNombreProducto() != null ? d.getNombreProducto() : "#" + d.getIdProducto()));
            }
            d.recalcularSubtotal();
            subtotal = subtotal.add(d.getSubtotal());
        }
        BigDecimal tasa = AppConfig.get().taxRate();
        BigDecimal impuesto = CurrencyUtils.round(subtotal.multiply(tasa));
        BigDecimal total    = CurrencyUtils.round(subtotal.add(impuesto));

        Venta v = new Venta();
        v.setNumeroComprobante(siguienteComprobante());
        v.setIdCliente(idCliente);
        v.setIdUsuario(u.getId());
        v.setFechaVenta(LocalDateTime.now());
        v.setSubtotal(CurrencyUtils.round(subtotal));
        v.setImpuesto(impuesto);
        v.setTotal(total);
        v.setMetodoPago(metodoPago != null ? metodoPago : MetodoPago.EFECTIVO);
        v.setEstado(EstadoVenta.COMPLETADA);
        v.setObservaciones(observaciones);
        v.getDetalles().addAll(detalles);

        return dao.crear(v);
    }

    public void anular(int idVenta) {
        if (!Session.isAdmin()) {
            throw new BusinessException("Sólo un administrador puede anular ventas.");
        }
        dao.anular(idVenta);
    }

    public Optional<Venta> buscar(int id)            { return dao.buscarPorId(id); }
    public List<Venta> listarRecientes(int limite)   { return dao.listarRecientes(limite); }
    public List<Venta> listarPorRango(LocalDate d, LocalDate h) {
        if (d == null || h == null) throw new BusinessException("Indique el rango de fechas.");
        if (d.isAfter(h)) throw new BusinessException("La fecha desde es posterior a la fecha hasta.");
        return dao.listarPorRangoFecha(d, h);
    }
    public List<DetalleVenta> listarDetalles(int idVenta) { return dao.listarDetalles(idVenta); }
}
