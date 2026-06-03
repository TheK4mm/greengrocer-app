package com.greengrocer.model;

import java.time.LocalDateTime;

/**
 * Clase base abstracta para entidades con identidad humana
 * (Usuario, Cliente, Proveedor).
 *
 * <p>Encapsula los atributos comunes y obliga a las subclases a definir
 * cómo se presentan en interfaz mediante {@link #mostrarInformacion()},
 * habilitando polimorfismo.</p>
 */
public abstract class Persona {

    protected int id;
    protected String nombre;
    protected boolean activo = true;
    protected LocalDateTime fechaCreacion;

    protected Persona() { }

    protected Persona(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    /** Representación legible para diálogos y mensajes. */
    public abstract String mostrarInformacion();

    @Override public String toString() { return mostrarInformacion(); }
}
