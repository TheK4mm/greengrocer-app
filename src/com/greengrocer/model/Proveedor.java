package com.greengrocer.model;

/** Proveedor que abastece a la verdulería. */
public class Proveedor extends Persona {

    private String ruc;
    private String telefono;
    private String email;
    private String direccion;

    public Proveedor() { }

    public Proveedor(int id, String nombre, String ruc) {
        super(id, nombre);
        this.ruc = ruc;
    }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    @Override public String mostrarInformacion() {
        return nombre + (ruc != null && !ruc.isBlank() ? " · RUC " + ruc : "");
    }
}
