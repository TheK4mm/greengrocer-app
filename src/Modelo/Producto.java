package Modelo;

public class Producto {
    private String idProducto;
    private String nombre;
    private double precio;
    private int stock;
    private String fechaRegistro;
    private int idCategoria;

    public Producto() {}

    public Producto(String idProducto, String nombre, double precio, int stock, String fechaRegistro, int idCategoria) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.fechaRegistro = fechaRegistro;
        this.idCategoria = idCategoria;
    }

    public String getIdProducto() { 
        return idProducto; 
    }
    public void setIdProducto(String idProducto) { 
        this.idProducto = idProducto; 
    }

    public String getNombre() { 
        return nombre; 
    }
    public void setNombre(String nombre)
    { this.nombre = nombre; 
    }

    public double getPrecio() {
        return precio; 
    }
    public void setPrecio(double precio) { 
        this.precio = precio; 
    }

    public int getStock() {
        return stock; 
    }
    public void setStock(int stock) { 
        this.stock = stock; 
    }

    public String getFechaRegistro() {
        return fechaRegistro; 
    }
    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdCategoria() { 
        return idCategoria;
    }
    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria; 
    }
}

