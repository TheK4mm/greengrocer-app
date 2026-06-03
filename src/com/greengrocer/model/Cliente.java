package com.greengrocer.model;

/** Cliente final atendido en la verdulería. */
public class Cliente extends Persona {

    private String dni;
    private String telefono;
    private String email;
    private String direccion;

    public Cliente() { }

    public Cliente(int id, String nombre, String dni) {
        super(id, nombre);
        this.dni = dni;
    }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    @Override public String mostrarInformacion() {
        return nombre + (dni != null && !dni.isBlank() ? " · DNI " + dni : "");
    }
}
