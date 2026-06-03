package com.greengrocer.dao;

import com.greengrocer.exception.BusinessException;
import com.greengrocer.exception.DataAccessException;
import com.greengrocer.model.DetalleVenta;
import com.greengrocer.model.Venta;
import com.greengrocer.model.enums.EstadoVenta;
import com.greengrocer.model.enums.MetodoPago;
import com.greengrocer.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Persistencia de ventas con manejo transaccional.
 *
 * <p>{@link #crear(Venta)} inserta la cabecera, los detalles y descuenta
 * el stock en una sola transacción. Si algo falla, se revierte todo.</p>
 */
public class VentaDao {

    private static final String SELECT_BASE = """
            SELECT v.*,
                   c.nombre AS nombre_cliente,
                   u.nombre AS nombre_usuario
              FROM venta v
              LEFT JOIN cliente c ON c.id_cliente = v.id_cliente
              JOIN usuario u ON u.id_usuario = v.id_usuario
            """;

    private Connection con() { return DatabaseConnection.get().getConnection(); }

    public Venta crear(Venta v) {
        Connection con = con();
        try {
            con.setAutoCommit(false);

            // 1. Cabecera
            String sqlVenta = "INSERT INTO venta " +
                    "(numero_comprobante, id_cliente, id_usuario, fecha_venta, subtotal, " +
                    "impuesto, total, metodo_pago, estado, observaciones) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, v.getNumeroComprobante());
                if (v.getIdCliente() == null) ps.setNull(2, Types.INTEGER);
                else ps.setInt(2, v.getIdCliente());
                ps.setInt(3, v.getIdUsuario());
                ps.setTimestamp(4, Timestamp.valueOf(
                        v.getFechaVenta() != null ? v.getFechaVenta() : LocalDateTime.now()));
                ps.setBigDecimal(5, v.getSubtotal());
                ps.setBigDecimal(6, v.getImpuesto());
                ps.setBigDecimal(7, v.getTotal());
                ps.setString(8, v.getMetodoPago().name());
                ps.setString(9, v.getEstado().name());
                ps.setString(10, v.getObservaciones());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) v.setId(keys.getInt(1));
                }
            }

            // 2. Detalles + descuento de stock
            String sqlDet = "INSERT INTO detalle_venta " +
                    "(id_venta, id_producto, cantidad, precio_unitario, subtotal) " +
                    "VALUES (?, ?, ?, ?, ?)";
            String sqlStock = "UPDATE producto SET stock = stock - ? " +
                    "WHERE id_producto = ? AND stock >= ?";

            try (PreparedStatement psDet  = con.prepareStatement(sqlDet);
                 PreparedStatement psStk  = con.prepareStatement(sqlStock)) {

                for (DetalleVenta d : v.getDetalles()) {
                    d.setIdVenta(v.getId());
                    psDet.setInt(1, v.getId());
                    psDet.setInt(2, d.getIdProducto());
                    psDet.setBigDecimal(3, d.getCantidad());
                    psDet.setBigDecimal(4, d.getPrecioUnitario());
                    psDet.setBigDecimal(5, d.getSubtotal());
                    psDet.addBatch();

                    int cantInt = d.getCantidad().setScale(0, java.math.RoundingMode.CEILING).intValueExact();
                    psStk.setInt(1, cantInt);
                    psStk.setInt(2, d.getIdProducto());
                    psStk.setInt(3, cantInt);
                    int rows = psStk.executeUpdate();
                    if (rows == 0) {
                        throw new BusinessException("Stock insuficiente para el producto "
                                + (d.getNombreProducto() != null ? d.getNombreProducto() : "#" + d.getIdProducto()));
                    }
                }
                psDet.executeBatch();
            }

            con.commit();
            return v;

        } catch (BusinessException be) {
            safeRollback(con);
            throw be;
        } catch (SQLException ex) {
            safeRollback(con);
            throw new DataAccessException("Error al registrar venta: " + ex.getMessage(), ex);
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException ignored) { }
        }
    }

    public boolean anular(int idVenta) {
        Connection con = con();
        try {
            con.setAutoCommit(false);

            // Verificar estado actual
            EstadoVenta estado;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT estado FROM venta WHERE id_venta=?")) {
                ps.setInt(1, idVenta);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new BusinessException("La venta no existe.");
                    }
                    estado = EstadoVenta.fromString(rs.getString(1));
                }
            }
            if (estado == EstadoVenta.ANULADA) {
                throw new BusinessException("La venta ya está anulada.");
            }

            // Devolver stock
            String reponer = "UPDATE producto p " +
                    "JOIN detalle_venta d ON d.id_producto = p.id_producto " +
                    "SET p.stock = p.stock + CEIL(d.cantidad) " +
                    "WHERE d.id_venta = ?";
            try (PreparedStatement ps = con.prepareStatement(reponer)) {
                ps.setInt(1, idVenta);
                ps.executeUpdate();
            }
            // Marcar anulada
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE venta SET estado='ANULADA' WHERE id_venta=?")) {
                ps.setInt(1, idVenta);
                ps.executeUpdate();
            }
            con.commit();
            return true;
        } catch (BusinessException be) {
            safeRollback(con);
            throw be;
        } catch (SQLException ex) {
            safeRollback(con);
            throw new DataAccessException("Error al anular venta: " + ex.getMessage(), ex);
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException ignored) { }
        }
    }

    public Optional<Venta> buscarPorId(int idVenta) {
        String sql = SELECT_BASE + " WHERE v.id_venta=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Venta v = map(rs);
                    v.getDetalles().addAll(listarDetalles(idVenta));
                    return Optional.of(v);
                }
            }
            return Optional.empty();
        } catch (SQLException ex) {
            throw new DataAccessException("Error al buscar venta: " + ex.getMessage(), ex);
        }
    }

    public List<DetalleVenta> listarDetalles(int idVenta) {
        String sql = """
                SELECT d.*, p.codigo, p.nombre AS nombre_producto, p.unidad_medida
                  FROM detalle_venta d
                  JOIN producto p ON p.id_producto = d.id_producto
                 WHERE d.id_venta = ?
                """;
        List<DetalleVenta> out = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetalleVenta d = new DetalleVenta();
                    d.setId(rs.getInt("id_detalle"));
                    d.setIdVenta(rs.getInt("id_venta"));
                    d.setIdProducto(rs.getInt("id_producto"));
                    d.setCodigoProducto(rs.getString("codigo"));
                    d.setNombreProducto(rs.getString("nombre_producto"));
                    d.setUnidadMedida(rs.getString("unidad_medida"));
                    d.setCantidad(rs.getBigDecimal("cantidad"));
                    d.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
                    d.setSubtotal(rs.getBigDecimal("subtotal"));
                    out.add(d);
                }
            }
            return out;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al listar detalle: " + ex.getMessage(), ex);
        }
    }

    public List<Venta> listarRecientes(int limite) {
        String sql = SELECT_BASE + " ORDER BY v.fecha_venta DESC LIMIT ?";
        List<Venta> out = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
            return out;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al listar ventas: " + ex.getMessage(), ex);
        }
    }

    public List<Venta> listarPorRangoFecha(LocalDate desde, LocalDate hasta) {
        String sql = SELECT_BASE + " WHERE DATE(v.fecha_venta) BETWEEN ? AND ? " +
                "ORDER BY v.fecha_venta DESC";
        List<Venta> out = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(desde));
            ps.setDate(2, java.sql.Date.valueOf(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
            return out;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al listar ventas por fecha: " + ex.getMessage(), ex);
        }
    }

    public int contarVentasDelDia() {
        String sql = "SELECT COUNT(*) FROM venta " +
                "WHERE DATE(fecha_venta)=CURDATE() AND estado='COMPLETADA'";
        try (PreparedStatement ps = con().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al contar ventas del día: " + ex.getMessage(), ex);
        }
    }

    public BigDecimal totalIngresosDelDia() {
        String sql = "SELECT COALESCE(SUM(total),0) FROM venta " +
                "WHERE DATE(fecha_venta)=CURDATE() AND estado='COMPLETADA'";
        try (PreparedStatement ps = con().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al sumar ingresos: " + ex.getMessage(), ex);
        }
    }

    public BigDecimal totalIngresosDelMes() {
        String sql = "SELECT COALESCE(SUM(total),0) FROM venta " +
                "WHERE YEAR(fecha_venta)=YEAR(CURDATE()) " +
                "  AND MONTH(fecha_venta)=MONTH(CURDATE()) " +
                "  AND estado='COMPLETADA'";
        try (PreparedStatement ps = con().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al sumar ingresos del mes: " + ex.getMessage(), ex);
        }
    }

    public List<Object[]> topProductosVendidos(int limite) {
        String sql = """
                SELECT p.codigo, p.nombre, SUM(d.cantidad) AS unidades, SUM(d.subtotal) AS ingresos
                  FROM detalle_venta d
                  JOIN producto p ON p.id_producto = d.id_producto
                  JOIN venta v    ON v.id_venta   = d.id_venta
                 WHERE v.estado = 'COMPLETADA'
                 GROUP BY p.id_producto
                 ORDER BY unidades DESC
                 LIMIT ?
                """;
        List<Object[]> out = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Object[]{
                            rs.getString("codigo"),
                            rs.getString("nombre"),
                            rs.getBigDecimal("unidades"),
                            rs.getBigDecimal("ingresos")
                    });
                }
            }
            return out;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al listar top productos: " + ex.getMessage(), ex);
        }
    }

    public String siguienteNumeroComprobante() {
        String sql = "SELECT COALESCE(MAX(id_venta),0)+1 FROM venta";
        try (PreparedStatement ps = con().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int n = rs.next() ? rs.getInt(1) : 1;
            return String.format("B001-%06d", n);
        } catch (SQLException ex) {
            throw new DataAccessException("Error al generar comprobante: " + ex.getMessage(), ex);
        }
    }

    private Venta map(ResultSet rs) throws SQLException {
        Venta v = new Venta();
        v.setId(rs.getInt("id_venta"));
        v.setNumeroComprobante(rs.getString("numero_comprobante"));
        int idCli = rs.getInt("id_cliente");
        if (!rs.wasNull()) v.setIdCliente(idCli);
        v.setNombreCliente(rs.getString("nombre_cliente"));
        v.setIdUsuario(rs.getInt("id_usuario"));
        v.setNombreUsuario(rs.getString("nombre_usuario"));
        Timestamp f = rs.getTimestamp("fecha_venta");
        if (f != null) v.setFechaVenta(f.toLocalDateTime());
        v.setSubtotal(rs.getBigDecimal("subtotal"));
        v.setImpuesto(rs.getBigDecimal("impuesto"));
        v.setTotal(rs.getBigDecimal("total"));
        v.setMetodoPago(MetodoPago.fromString(rs.getString("metodo_pago")));
        v.setEstado(EstadoVenta.fromString(rs.getString("estado")));
        v.setObservaciones(rs.getString("observaciones"));
        return v;
    }

    private void safeRollback(Connection con) {
        try { con.rollback(); } catch (SQLException ignored) { }
    }
}
