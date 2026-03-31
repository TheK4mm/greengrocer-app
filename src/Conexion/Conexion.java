package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    Connection conectar = null;

    public Connection conexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conectar = DriverManager.getConnection("jdbc:mysql://localhost:3306/verduleria", "root", "");
            System.out.println("Conexión exitosa!!");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error en la conexión :(" + e.getMessage());
        }
        return conectar;
    }
}

