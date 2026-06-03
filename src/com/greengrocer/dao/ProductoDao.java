package com.greengrocer.dao;

import com.greengrocer.exception.DataAccessException;
import com.greengrocer.model.Producto;
import com.greengrocer.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductoDao implements GenericDao<Producto, Integer> {

    private static final String SELECT_BASE = """
            SELECT p.*, c.nombre AS nombre_categoria, pr.nombre AS nombre_proveedor
              FROM producto p
              JOIN categoria c  ON c.id_categoria = p.id_categoria
              LEFT JOIN proveedor pr ON pr.id_proveedor = p.id_proveedor
            """;

    private Connection con() { return DatabaseConnection.get().getConnection(); }

    @Override public Producto crear(Producto p) {
        String sql = "INSERT INTO producto " +
                "(codigo, nombre, descripcion, precio_compra, precio_venta, stock, " +
                "stock_minimo, unidad_medida, id_categoria, id_proveedor, activo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getDescripcion());
            ps.setBigDecimal(4, p.getPrecioCompra());
            ps.setBigDecimal(5, p.getPrecioVenta());
            ps.setInt(6, p.getStock());
            ps.setInt(7, p.getStockMinimo());
            ps.setString(8, p.getUnidadMedida());
            ps.setInt(9, p.getIdCategoria());
            if (p.getIdProveedor() == null) ps.setNull(10, Types.INTEGER);
            else ps.setInt(10, p.getIdProveedor());
            ps.setBoolean(11, p.isActivo());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getInt(1));
            }
            return p;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al crear producto: " + ex.getMessage(), ex);
        }
    }

    @Override public boolean actualizar(Producto p) {
        String sql = "UPDATE producto SET codigo=?, nombre=?, descripcion=?, precio_compra=?, " +
                "precio_venta=?, stock=?, stock_minimo=?, unidad_medida=?, id_categoria=?, " +
                "id_proveedor=?, activo=? WHERE id_producto=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getDescripcion());
            ps.setBigDecimal(4, p.getPrecioCompra());
            ps.setBigDecimal(5, p.getPrecioVenta());
            ps.setInt(6, p.getStock());
            ps.setInt(7, p.getStockMinimo());
            ps.setString(8, p.getUnidadMedida());
            ps.setInt(9, p.getIdCategoria());
            if (p.getIdProveedor() == null) ps.setNull(10, Types.INTEGER);
            else ps.setInt(10, p.getIdProveedor());
            ps.setBoolean(11, p.isActivo());
            ps.setInt(12, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al actualizar producto: " + ex.getMessage(), ex);
        }
    }

    @Override public boolean eliminar(Integer id) {
        String sql = "DELETE FROM producto WHERE id_producto=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al eliminar producto: " + ex.getMessage(), ex);
        }
    }

    @Override public Optional<Producto> buscarPorId(Integer id) {
        String sql = SELECT_BASE + " WHERE p.id_producto=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
            return Optional.empty();
        } catch (SQLException ex) {
            throw new DataAccessException("Error al buscar producto: " + ex.getMessage(), ex);
        }
    }

    public Optional<Producto> buscarPorCodigo(String codigo) {
        String sql = SELECT_BASE + " WHERE p.codigo=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
            return Optional.empty();
        } catch (SQLException ex) {
            throw new DataAccessException("Error al buscar producto: " + ex.getMessage(), ex);
        }
    }

    @Override public List<Producto> listar() {
        String sql = SELECT_BASE + " ORDER BY p.nombre";
        return runQuery(sql);
    }

    public List<Producto> listarActivos() {
        String sql = SELECT_BASE + " WHERE p.activo=1 ORDER BY p.nombre";
        return runQuery(sql);
    }

    public List<Producto> buscarPorTexto(String q) {
        String sql = SELECT_BASE +
                " WHERE p.activo=1 AND (p.nombre LIKE ? OR p.codigo LIKE ?) ORDER BY p.nombre LIMIT 200";
        String like = "%" + q + "%";
        List<Producto> out = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
            return out;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al buscar productos: " + ex.getMessage(), ex);
        }
    }

    public List<Producto> listarStockCritico() {
        String sql = SELECT_BASE +
                " WHERE p.activo=1 AND p.stock <= p.stock_minimo ORDER BY p.stock ASC";
        return runQuery(sql);
    }

    public int contarActivos() {
        String sql = "SELECT COUNT(*) FROM producto WHERE activo=1";
        try (PreparedStatement ps = con().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al contar productos: " + ex.getMessage(), ex);
        }
    }

    public int contarStockCritico() {
        String sql = "SELECT COUNT(*) FROM producto WHERE activo=1 AND stock <= stock_minimo";
        try (PreparedStatement ps = con().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al contar stock crítico: " + ex.getMessage(), ex);
        }
    }

    private List<Producto> runQuery(String sql) {
        List<Producto> out = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al listar productos: " + ex.getMessage(), ex);
        }
    }

    private Producto map(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getInt("id_producto"));
        p.setCodigo(rs.getString("codigo"));
        p.setNombre(rs.getString("nombre"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setPrecioCompra(rs.getBigDecimal("precio_compra"));
        p.setPrecioVenta(rs.getBigDecimal("precio_venta"));
        p.setStock(rs.getInt("stock"));
        p.setStockMinimo(rs.getInt("stock_minimo"));
        p.setUnidadMedida(rs.getString("unidad_medida"));
        p.setIdCategoria(rs.getInt("id_categoria"));
        p.setNombreCategoria(rs.getString("nombre_categoria"));
        int idProv = rs.getInt("id_proveedor");
        if (!rs.wasNull()) p.setIdProveedor(idProv);
        p.setNombreProveedor(rs.getString("nombre_proveedor"));
        p.setActivo(rs.getBoolean("activo"));
        Timestamp t = rs.getTimestamp("fecha_registro");
        if (t != null) p.setFechaRegistro(t.toLocalDateTime());
        return p;
    }
}
