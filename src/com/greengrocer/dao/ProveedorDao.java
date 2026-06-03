package com.greengrocer.dao;

import com.greengrocer.exception.DataAccessException;
import com.greengrocer.model.Proveedor;
import com.greengrocer.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProveedorDao implements GenericDao<Proveedor, Integer> {

    private Connection con() { return DatabaseConnection.get().getConnection(); }

    @Override public Proveedor crear(Proveedor p) {
        String sql = "INSERT INTO proveedor (nombre, ruc, telefono, email, direccion, activo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getRuc());
            ps.setString(3, p.getTelefono());
            ps.setString(4, p.getEmail());
            ps.setString(5, p.getDireccion());
            ps.setBoolean(6, p.isActivo());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getInt(1));
            }
            return p;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al crear proveedor: " + ex.getMessage(), ex);
        }
    }

    @Override public boolean actualizar(Proveedor p) {
        String sql = "UPDATE proveedor SET nombre=?, ruc=?, telefono=?, email=?, direccion=?, activo=? " +
                     "WHERE id_proveedor=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getRuc());
            ps.setString(3, p.getTelefono());
            ps.setString(4, p.getEmail());
            ps.setString(5, p.getDireccion());
            ps.setBoolean(6, p.isActivo());
            ps.setInt(7, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al actualizar proveedor: " + ex.getMessage(), ex);
        }
    }

    @Override public boolean eliminar(Integer id) {
        String sql = "DELETE FROM proveedor WHERE id_proveedor=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al eliminar proveedor: " + ex.getMessage(), ex);
        }
    }

    @Override public Optional<Proveedor> buscarPorId(Integer id) {
        String sql = "SELECT * FROM proveedor WHERE id_proveedor=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
            return Optional.empty();
        } catch (SQLException ex) {
            throw new DataAccessException("Error al buscar proveedor: " + ex.getMessage(), ex);
        }
    }

    @Override public List<Proveedor> listar() {
        String sql = "SELECT * FROM proveedor ORDER BY nombre";
        List<Proveedor> out = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al listar proveedores: " + ex.getMessage(), ex);
        }
    }

    public List<Proveedor> listarActivos() {
        String sql = "SELECT * FROM proveedor WHERE activo=1 ORDER BY nombre";
        List<Proveedor> out = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al listar proveedores activos: " + ex.getMessage(), ex);
        }
    }

    private Proveedor map(ResultSet rs) throws SQLException {
        Proveedor p = new Proveedor();
        p.setId(rs.getInt("id_proveedor"));
        p.setNombre(rs.getString("nombre"));
        p.setRuc(rs.getString("ruc"));
        p.setTelefono(rs.getString("telefono"));
        p.setEmail(rs.getString("email"));
        p.setDireccion(rs.getString("direccion"));
        p.setActivo(rs.getBoolean("activo"));
        Timestamp t = rs.getTimestamp("fecha_creacion");
        if (t != null) p.setFechaCreacion(t.toLocalDateTime());
        return p;
    }
}
