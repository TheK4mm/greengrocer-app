package com.greengrocer.model;

import java.time.LocalDateTime;
import java.util.Objects;

/** Categoría a la que pertenece un producto. */
public class Categoria {

    private int id;
    private String nombre;
    private String descripcion;
    private boolean activo = true;
    private LocalDateTime fechaCreacion;

    public Categoria() { }

    public Categoria(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    @Override public String toString() { return nombre; }

    @Override public boolean equals(Object o) {
        if (!(o instanceof Categoria other)) return false;
        return id == other.id;
    }

    @Override public int hashCode() { return Objects.hash(id); }
}
