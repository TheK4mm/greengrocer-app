package com.greengrocer.model;

import com.greengrocer.model.enums.RolUsuario;

import java.time.LocalDateTime;

/** Usuario del sistema (operador o administrador). */
public class Usuario extends Persona {

    private String nombreUsuario;
    private String passwordHash;
    private String passwordSalt;
    private RolUsuario rol = RolUsuario.VENDEDOR;
    private LocalDateTime ultimoAcceso;

    public Usuario() { }

    public Usuario(int id, String nombre, String nombreUsuario, RolUsuario rol) {
        super(id, nombre);
        this.nombreUsuario = nombreUsuario;
        this.rol = rol;
    }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPasswordSalt() { return passwordSalt; }
    public void setPasswordSalt(String passwordSalt) { this.passwordSalt = passwordSalt; }

    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }

    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }

    public boolean isAdmin() { return rol == RolUsuario.ADMIN; }

    @Override public String mostrarInformacion() {
        return nombre + " (" + nombreUsuario + " · " + rol.getLabel() + ")";
    }
}
