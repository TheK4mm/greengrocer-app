package Modelo;

//Clase base para aplicar herencia
public class Persona {
    private String nombre;

    public Persona() {}

    public Persona(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String mostrarInformacion() {
        return "Nombre: " + nombre;
    }
}

class Usuario extends Persona {
    private String contraseña;

    public Usuario() {}

    public Usuario(String nombre, String contraseña) {
        super(nombre);
        this.contraseña = contraseña;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    // Polimorfismo para sobreescribir el método
    @Override
    public String mostrarInformacion() {
        return "Usuario: " + getNombre();
    }
}

