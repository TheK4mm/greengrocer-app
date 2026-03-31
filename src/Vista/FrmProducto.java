package Vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class FrmProducto extends JFrame {
    private JTextField txtIdProducto, txtNombre, txtPrecio, txtStock, txtFecha, txtCategoria;
    private JTable tableProductos;
    private DefaultTableModel modelo;
    private JButton btnAgregar, btnActualizar, btnEliminar, btnMostrar, btnSalir;

    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    public FrmProducto() {
        setTitle("Gestión de Productos");
        setSize(800, 600);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Centrado inicial
        setLocationRelativeTo(null);

        // Pantalla completa al abrir
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JLabel lblId = new JLabel("ID:");
        lblId.setBounds(20, 20, 100, 30);
        add(lblId);

        txtIdProducto = new JTextField();
        txtIdProducto.setBounds(120, 20, 150, 30);
        add(txtIdProducto);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(20, 60, 100, 30);
        add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(120, 60, 150, 30);
        add(txtNombre);

        JLabel lblPrecio = new JLabel("Precio:");
        lblPrecio.setBounds(20, 100, 100, 30);
        add(lblPrecio);

        txtPrecio = new JTextField();
        txtPrecio.setBounds(120, 100, 150, 30);
        add(txtPrecio);

        JLabel lblStock = new JLabel("Stock:");
        lblStock.setBounds(20, 140, 100, 30);
        add(lblStock);

        txtStock = new JTextField();
        txtStock.setBounds(120, 140, 150, 30);
        add(txtStock);

        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setBounds(20, 180, 100, 30);
        add(lblFecha);

        txtFecha = new JTextField();
        txtFecha.setBounds(120, 180, 150, 30);
        add(txtFecha);

        JLabel lblCategoria = new JLabel("Categoría:");
        lblCategoria.setBounds(20, 220, 100, 30);
        add(lblCategoria);

        txtCategoria = new JTextField();
        txtCategoria.setBounds(120, 220, 150, 30);
        add(txtCategoria);

        btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(300, 20, 120, 30);
        add(btnAgregar);

        btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(300, 60, 120, 30);
        add(btnActualizar);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(300, 100, 120, 30);
        add(btnEliminar);

        btnMostrar = new JButton("Mostrar");
        btnMostrar.setBounds(300, 140, 120, 30);
        add(btnMostrar);

        btnSalir = new JButton("Salir");
        btnSalir.setBounds(300, 180, 120, 30);
        add(btnSalir);

        modelo = new DefaultTableModel();
        tableProductos = new JTable(modelo);
        modelo.addColumn("ID");
        modelo.addColumn("Nombre");
        modelo.addColumn("Precio");
        modelo.addColumn("Stock");
        modelo.addColumn("Fecha");
        modelo.addColumn("Categoría");

        JScrollPane scroll = new JScrollPane(tableProductos);
        scroll.setBounds(20, 270, 740, 250);
        add(scroll);

        conectar();

        btnAgregar.addActionListener(e -> agregarProducto());
        btnActualizar.addActionListener(e -> actualizarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnMostrar.addActionListener(e -> mostrarProductos());
        btnSalir.addActionListener(e -> dispose());
    }

    public void conectar() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/verduleria", "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error de conexión: " + e.getMessage());
        }
    }

    public void agregarProducto() {
        try {
            String idProducto = txtIdProducto.getText();
            String nombre = txtNombre.getText();
            double precio = Double.parseDouble(txtPrecio.getText());
            int stock = Integer.parseInt(txtStock.getText());
            String fecha = txtFecha.getText();
            int idCategoria = Integer.parseInt(txtCategoria.getText());

            if (precio < 0) {
                JOptionPane.showMessageDialog(null, "El precio no puede ser negativo.");
                return;
            }
            if (stock < 0) {
                JOptionPane.showMessageDialog(null, "El stock no puede ser negativo.");
                return;
            }

            pst = con.prepareStatement("INSERT INTO producto (idProducto, nombre, precio, stock, fecha_registro, id_categoria) VALUES (?, ?, ?, ?, ?, ?)");
            pst.setString(1, idProducto);
            pst.setString(2, nombre);
            pst.setDouble(3, precio);
            pst.setInt(4, stock);
            pst.setString(5, fecha);
            pst.setInt(6, idCategoria);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(null, "Producto agregado");
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, "Error en el formato de precio, stock o categoría. Verifica los datos numéricos.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al agregar: " + e.getMessage());
        }
    }

    public void actualizarProducto() {
        try {
            double precio = Double.parseDouble(txtPrecio.getText());
            int stock = Integer.parseInt(txtStock.getText());

            if (precio < 0) {
                JOptionPane.showMessageDialog(null, "El precio no puede ser negativo.");
                return;
            }
            if (stock < 0) {
                JOptionPane.showMessageDialog(null, "El stock no puede ser negativo.");
                return;
            }

            pst = con.prepareStatement("UPDATE producto SET nombre=?, precio=?, stock=?, fecha_registro=?, id_categoria=? WHERE idProducto=?");
            pst.setString(1, txtNombre.getText());
            pst.setDouble(2, precio);
            pst.setInt(3, stock);
            pst.setString(4, txtFecha.getText());
            pst.setInt(5, Integer.parseInt(txtCategoria.getText()));
            pst.setString(6, txtIdProducto.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Producto actualizado");
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, "Error en el formato de precio, stock o categoría. Verifica los datos numéricos.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar: " + e.getMessage());
        }
    }

    public void eliminarProducto() {
        try {
            pst = con.prepareStatement("DELETE FROM producto WHERE idProducto=?");
            pst.setString(1, txtIdProducto.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Producto eliminado");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar: " + e.getMessage());
        }
    }

    public void mostrarProductos() {
        try {
            modelo.setRowCount(0);
            pst = con.prepareStatement("SELECT * FROM producto");
            rs = pst.executeQuery();
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getString("idProducto"),
                    rs.getString("nombre"),
                    rs.getString("precio"),
                    rs.getString("stock"),
                    rs.getString("fecha_registro"),
                    rs.getString("id_categoria")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al mostrar: " + e.getMessage());
        }
    }
}
