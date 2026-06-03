package com.greengrocer.service;

import com.greengrocer.dao.UsuarioDao;
import com.greengrocer.exception.BusinessException;
import com.greengrocer.model.Usuario;
import com.greengrocer.model.enums.RolUsuario;
import com.greengrocer.util.PasswordHasher;
import com.greengrocer.util.ValidationUtils;

import java.util.List;

public class UsuarioService {

    private final UsuarioDao dao = new UsuarioDao();

    public List<Usuario> listar() { return dao.listar(); }

    public Usuario crear(String nombre, String nombreUsuario, String contrasena, RolUsuario rol) {
        return new AuthService().registrar(nombre, nombreUsuario, contrasena, rol);
    }

    public void actualizarPerfil(int id, String nombre, RolUsuario rol, boolean activo) {
        Usuario u = dao.buscarPorId(id)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado."));
        u.setNombre(ValidationUtils.requireText(nombre, "Nombre"));
        u.setRol(rol);
        u.setActivo(activo);
        dao.actualizar(u);
    }

    public void restablecerPassword(int idUsuario, String nuevaPassword) {
        nuevaPassword = ValidationUtils.requireText(nuevaPassword, "Nueva contraseña");
        if (nuevaPassword.length() < 6) {
            throw new BusinessException("La contraseña debe tener al menos 6 caracteres.");
        }
        String salt = PasswordHasher.newSalt();
        String hash = PasswordHasher.hash(nuevaPassword, salt);
        dao.actualizarPassword(idUsuario, hash, salt);
    }

    public void eliminar(int id) {
        if (Session.get() != null && Session.get().getId() == id) {
            throw new BusinessException("No puede eliminar su propia cuenta.");
        }
        try { dao.eliminar(id); }
        catch (Exception ex) {
            if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("foreign")) {
                throw new BusinessException(
                        "No se puede eliminar: el usuario tiene ventas registradas.");
            }
            throw ex;
        }
    }
}
