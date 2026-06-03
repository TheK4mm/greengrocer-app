package com.greengrocer.dao;

import com.greengrocer.exception.DataAccessException;
import com.greengrocer.model.Categoria;
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

public class CategoriaDao implements GenericDao<Categoria, Integer> {

    private Connection con() { return DatabaseConnection.get().getConnection(); }

    @Override public Categoria crear(Categoria c) {
        String sql = "INSERT INTO categoria (nombre, descripcion, activo) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setBoolean(3, c.isActivo());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setId(keys.getInt(1));
            }
            return c;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al crear categoría: " + ex.getMessage(), ex);
        }
    }

    @Override public boolean actualizar(Categoria c) {
        String sql = "UPDATE categoria SET nombre=?, descripcion=?, activo=? WHERE id_categoria=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setBoolean(3, c.isActivo());
            ps.setInt(4, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al actualizar categoría: " + ex.getMessage(), ex);
        }
    }

    @Override public boolean eliminar(Integer id) {
        String sql = "DELETE FROM categoria WHERE id_categoria=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al eliminar categoría: " + ex.getMessage(), ex);
        }
    }

    @Override public Optional<Categoria> buscarPorId(Integer id) {
        String sql = "SELECT * FROM categoria WHERE id_categoria=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
            return Optional.empty();
        } catch (SQLException ex) {
            throw new DataAccessException("Error al buscar categoría: " + ex.getMessage(), ex);
        }
    }

    @Override public List<Categoria> listar() {
        String sql = "SELECT * FROM categoria ORDER BY nombre";
        List<Categoria> out = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al listar categorías: " + ex.getMessage(), ex);
        }
    }

    public List<Categoria> listarActivas() {
        String sql = "SELECT * FROM categoria WHERE activo=1 ORDER BY nombre";
        List<Categoria> out = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al listar categorías activas: " + ex.getMessage(), ex);
        }
    }

    private Categoria map(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();
        c.setId(rs.getInt("id_categoria"));
        c.setNombre(rs.getString("nombre"));
        c.setDescripcion(rs.getString("descripcion"));
        c.setActivo(rs.getBoolean("activo"));
        Timestamp t = rs.getTimestamp("fecha_creacion");
        if (t != null) c.setFechaCreacion(t.toLocalDateTime());
        return c;
    }
}
