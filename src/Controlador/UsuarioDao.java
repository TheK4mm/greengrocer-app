package Controlador;

import Modelo.Usuario;
import java.sql.*;
import javax.swing.JOptionPane;

public class UsuarioDao {
    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    public void conectar() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/verduleria", "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error de conexión: " + e.getMessage());
        }
    }

    public boolean validarLogin(Usuario usuario) {
        boolean resultado = false;
        try {
            conectar();
            pst = con.prepareStatement("SELECT * FROM usuario WHERE nombre_usuario=? AND contrasena=?");
            pst.setString(1, usuario.getNombreUsuario());
            pst.setString(2, usuario.getContrasena());
            rs = pst.executeQuery();

            if (rs.next()) {
                resultado = true;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al validar login: " + e.getMessage());
        }
        return resultado;
    }

    public boolean registrarUsuario(Usuario usuario) {
        boolean resultado = false;
        try {
            conectar();

            //Verificar si el usuario ya existe
            pst = con.prepareStatement("SELECT * FROM usuario WHERE nombre_usuario=?");
            pst.setString(1, usuario.getNombreUsuario());
            rs = pst.executeQuery();

            if (rs.next()) {
                return false;  //Usuario ya existe
            }

            //Si no existe, lo insertamos
            pst = con.prepareStatement("INSERT INTO usuario (nombre_usuario, contrasena) VALUES (?, ?)");
            pst.setString(1, usuario.getNombreUsuario());
            pst.setString(2, usuario.getContrasena());
            pst.executeUpdate();
            resultado = true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al registrar usuario: " + e.getMessage());
        }
        return resultado;
    }
}
