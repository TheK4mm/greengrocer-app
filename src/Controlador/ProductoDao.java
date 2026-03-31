package Controlador;

import Modelo.Producto;
import java.sql.*;
import javax.swing.JOptionPane;

//Permite hacer el CRUD y hacer consultas SQL
public class ProductoDao {
    Connection con;

    public ProductoDao(Connection con) {
        this.con = con;
    }

    //Sobrecarga 1 para insertar producto completo
    public void agregarProducto(Producto producto) {
        try {
            PreparedStatement pst = con.prepareStatement(
                "INSERT INTO producto (idProducto, nombre, precio, stock, fecha_registro, id_categoria) VALUES (?, ?, ?, ?, ?, ?)"
            );
            pst.setString(1, producto.getIdProducto());
            pst.setString(2, producto.getNombre());
            pst.setDouble(3, producto.getPrecio());
            pst.setInt(4, producto.getStock());
            pst.setString(5, producto.getFechaRegistro());
            pst.setInt(6, producto.getIdCategoria());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Producto agregado");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    //Sobrecarga 2 para insertar solo nombre y precio
    public void agregarProducto(String nombre, double precio) {
        try {
            PreparedStatement pst = con.prepareStatement(
                "INSERT INTO producto (nombre, precio) VALUES (?, ?)"
            );
            pst.setString(1, nombre);
            pst.setDouble(2, precio);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Producto básico agregado");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
}

