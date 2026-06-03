package com.greengrocer.service;

import com.greengrocer.dao.UsuarioDao;
import com.greengrocer.exception.BusinessException;
import com.greengrocer.model.Usuario;
import com.greengrocer.model.enums.RolUsuario;
import com.greengrocer.util.PasswordHasher;
import com.greengrocer.util.ValidationUtils;

import java.util.Optional;

/** Autenticación, registro de usuarios y cambio de contraseña. */
public class AuthService {

    private final UsuarioDao usuarioDao = new UsuarioDao();

    /** Devuelve el usuario autenticado o lanza {@link BusinessException}. */
    public Usuario login(String nombreUsuario, String contrasena) {
        String user = ValidationUtils.requireText(nombreUsuario, "Usuario");
        String pass = ValidationUtils.requireText(contrasena,    "Contraseña");

        Optional<Usuario> opt = usuarioDao.buscarPorNombreUsuario(user);
        if (opt.isEmpty()) throw new BusinessException("Usuario o contraseña incorrectos.");

        Usuario u = opt.get();
        if (!u.isActivo()) throw new BusinessException("La cuenta está deshabilitada.");
        if (!PasswordHasher.verify(pass, u.getPasswordSalt(), u.getPasswordHash())) {
            throw new BusinessException("Usuario o contraseña incorrectos.");
        }
        usuarioDao.registrarUltimoAcceso(u.getId());
        return u;
    }

    /** Registra un nuevo usuario con contraseña hasheada. */
    public Usuario registrar(String nombre, String nombreUsuario,
                             String contrasena, RolUsuario rol) {
        nombre        = ValidationUtils.requireText(nombre,        "Nombre completo");
        nombreUsuario = ValidationUtils.requireText(nombreUsuario, "Usuario");
        contrasena    = ValidationUtils.requireText(contrasena,    "Contraseña");
        if (contrasena.length() < 6) {
            throw new BusinessException("La contraseña debe tener al menos 6 caracteres.");
        }
        if (usuarioDao.buscarPorNombreUsuario(nombreUsuario).isPresent()) {
            throw new BusinessException("El nombre de usuario ya está registrado.");
        }

        String salt = PasswordHasher.newSalt();
        String hash = PasswordHasher.hash(contrasena, salt);

        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setNombreUsuario(nombreUsuario);
        u.setPasswordSalt(salt);
        u.setPasswordHash(hash);
        u.setRol(rol != null ? rol : RolUsuario.VENDEDOR);
        u.setActivo(true);
        return usuarioDao.crear(u);
    }

    /** Cambia la contraseña validando la actual. */
    public void cambiarPassword(int idUsuario, String actual, String nueva) {
        actual = ValidationUtils.requireText(actual, "Contraseña actual");
        nueva  = ValidationUtils.requireText(nueva,  "Nueva contraseña");
        if (nueva.length() < 6) {
            throw new BusinessException("La nueva contraseña debe tener al menos 6 caracteres.");
        }
        Usuario u = usuarioDao.buscarPorId(idUsuario)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado."));
        if (!PasswordHasher.verify(actual, u.getPasswordSalt(), u.getPasswordHash())) {
            throw new BusinessException("La contraseña actual no coincide.");
        }
        String salt = PasswordHasher.newSalt();
        String hash = PasswordHasher.hash(nueva, salt);
        usuarioDao.actualizarPassword(idUsuario, hash, salt);
    }

    /**
     * Si la tabla de usuarios está vacía, crea el ADMIN por defecto
     * (usuario: {@code admin}, contraseña: {@code admin123}).
     */
    public void ensureDefaultAdmin() {
        if (usuarioDao.contar() == 0) {
            registrar("Administrador", "admin", "admin123", RolUsuario.ADMIN);
        }
    }
}
