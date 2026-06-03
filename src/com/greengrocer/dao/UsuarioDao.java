package com.greengrocer.dao;

import com.greengrocer.exception.DataAccessException;
import com.greengrocer.model.Usuario;
import com.greengrocer.model.enums.RolUsuario;
import com.greengrocer.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDao implements GenericDao<Usuario, Integer> {

    private Connection con() { return DatabaseConnection.get().getConnection(); }

    @Override public Usuario crear(Usuario u) {
        String sql = "INSERT INTO usuario " +
                "(nombre, nombre_usuario, password_hash, password_salt, rol, activo) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getNombreUsuario());
            ps.setString(3, u.getPasswordHash());
            ps.setString(4, u.getPasswordSalt());
            ps.setString(5, u.getRol().name());
            ps.setBoolean(6, u.isActivo());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) u.setId(keys.getInt(1));
            }
            return u;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al crear usuario: " + ex.getMessage(), ex);
        }
    }

    @Override public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuario SET nombre=?, nombre_usuario=?, rol=?, activo=? WHERE id_usuario=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getNombreUsuario());
            ps.setString(3, u.getRol().name());
            ps.setBoolean(4, u.isActivo());
            ps.setInt(5, u.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al actualizar usuario: " + ex.getMessage(), ex);
        }
    }

    public boolean actualizarPassword(int idUsuario, String nuevoHash, String nuevoSalt) {
        String sql = "UPDATE usuario SET password_hash=?, password_salt=? WHERE id_usuario=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, nuevoHash);
            ps.setString(2, nuevoSalt);
            ps.setInt(3, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al actualizar contraseña: " + ex.getMessage(), ex);
        }
    }

    public boolean registrarUltimoAcceso(int idUsuario) {
        String sql = "UPDATE usuario SET ultimo_acceso=? WHERE id_usuario=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al registrar acceso: " + ex.getMessage(), ex);
        }
    }

    @Override public boolean eliminar(Integer id) {
        String sql = "DELETE FROM usuario WHERE id_usuario=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al eliminar usuario: " + ex.getMessage(), ex);
        }
    }

    @Override public Optional<Usuario> buscarPorId(Integer id) {
        String sql = "SELECT * FROM usuario WHERE id_usuario=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
            return Optional.empty();
        } catch (SQLException ex) {
            throw new DataAccessException("Error al buscar usuario: " + ex.getMessage(), ex);
        }
    }

    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) {
        String sql = "SELECT * FROM usuario WHERE nombre_usuario=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
            return Optional.empty();
        } catch (SQLException ex) {
            throw new DataAccessException("Error al buscar usuario: " + ex.getMessage(), ex);
        }
    }

    @Override public List<Usuario> listar() {
        String sql = "SELECT * FROM usuario ORDER BY nombre";
        List<Usuario> out = new ArrayList<>();
        try (PreparedStatement ps = con().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al listar usuarios: " + ex.getMessage(), ex);
        }
    }

    public int contar() {
        String sql = "SELECT COUNT(*) FROM usuario";
        try (PreparedStatement ps = con().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException ex) {
            throw new DataAccessException("Error al contar usuarios: " + ex.getMessage(), ex);
        }
    }

    private Usuario map(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id_usuario"));
        u.setNombre(rs.getString("nombre"));
        u.setNombreUsuario(rs.getString("nombre_usuario"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setPasswordSalt(rs.getString("password_salt"));
        u.setRol(RolUsuario.fromString(rs.getString("rol")));
        u.setActivo(rs.getBoolean("activo"));
        Timestamp t = rs.getTimestamp("fecha_creacion");
        if (t != null) u.setFechaCreacion(t.toLocalDateTime());
        Timestamp ua = rs.getTimestamp("ultimo_acceso");
        if (ua != null) u.setUltimoAcceso(ua.toLocalDateTime());
        return u;
    }
}
