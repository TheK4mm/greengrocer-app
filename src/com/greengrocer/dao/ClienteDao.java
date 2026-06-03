package com.greengrocer.dao;

import com.greengrocer.exception.DataAccessException;
import com.greengrocer.model.Cliente;
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

public class ClienteDao implements GenericDao<Cliente, Integer> {

    private Connection con() { return DatabaseConnection.get().getConnection(); }

    @Override public Cliente crear(Cliente c) {
        String sql = "INSERT INTO cliente (nombre, dni, telefono, email, direccion, activo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDni());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getDireccion());
            ps.setBoolean(6, c.isActivo());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setId(keys.getInt(1));
            }
            return c;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al crear cliente: " + ex.getMessage(), ex);
        }
    }

    @Override public boolean actualizar(Cliente c) {
        String sql = "UPDATE cliente SET nombre=?, dni=?, telefono=?, email=?, direccion=?, activo=? " +
                     "WHERE id_cliente=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDni());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getDireccion());
            ps.setBoolean(6, c.isActivo());
            ps.setInt(7, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al actualizar cliente: " + ex.getMessage(), ex);
        }
    }

    @Override public boolean eliminar(Integer id) {
        String sql = "DELETE FROM cliente WHERE id_cliente=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al eliminar cliente: " + ex.getMessage(), ex);
        }
    }

    @Override public Optional<Cliente> buscarPorId(Integer id) {
        String sql = "SELECT * FROM cliente WHERE id_cliente=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
            return Optional.empty();
        } catch (SQLException ex) {
            throw new DataAccessException("Error al buscar cliente: " + ex.getMessage(), ex);
        }
    }

    @Override public List<Cliente> listar() {
        String sql = "SELECT * FROM cliente ORDER BY nombre";
        List<Cliente> out = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al listar clientes: " + ex.getMessage(), ex);
        }
    }

    public List<Cliente> listarActivos() {
        String sql = "SELECT * FROM cliente WHERE activo=1 ORDER BY nombre";
        List<Cliente> out = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al listar clientes activos: " + ex.getMessage(), ex);
        }
    }

    public List<Cliente> buscarPorTexto(String q) {
        String sql = "SELECT * FROM cliente WHERE nombre LIKE ? OR dni LIKE ? ORDER BY nombre LIMIT 100";
        String like = "%" + q + "%";
        List<Cliente> out = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
            return out;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al buscar clientes: " + ex.getMessage(), ex);
        }
    }

    private Cliente map(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id_cliente"));
        c.setNombre(rs.getString("nombre"));
        c.setDni(rs.getString("dni"));
        c.setTelefono(rs.getString("telefono"));
        c.setEmail(rs.getString("email"));
        c.setDireccion(rs.getString("direccion"));
        c.setActivo(rs.getBoolean("activo"));
        Timestamp t = rs.getTimestamp("fecha_creacion");
        if (t != null) c.setFechaCreacion(t.toLocalDateTime());
        return c;
    }
}
