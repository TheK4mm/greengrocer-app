package Vista;

import javax.swing.*;
import Controlador.UsuarioDao;
import Modelo.Usuario;

public class FrmRegistrarUsuario extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnGuardar;

    public FrmRegistrarUsuario() {
        setTitle("Registrar Usuario");
        setSize(350, 220);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel lblUsuario = new JLabel("Nuevo usuario:");
        lblUsuario.setBounds(30, 30, 100, 25);
        add(lblUsuario);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(140, 30, 150, 25);
        add(txtUsuario);

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setBounds(30, 70, 100, 25);
        add(lblContrasena);

        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(140, 70, 150, 25);
        add(txtContrasena);

        btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(140, 120, 150, 30);
        add(btnGuardar);

        btnGuardar.addActionListener(e -> registrarUsuario());

        setLocationRelativeTo(null);
    }

    private void registrarUsuario() {
        String usuarioTexto = txtUsuario.getText().trim();
        String contrasenaTexto = new String(txtContrasena.getPassword()).trim();

        if (usuarioTexto.isEmpty() || contrasenaTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa ambos campos...");
            return;
        }

        Usuario usuario = new Usuario(usuarioTexto, contrasenaTexto);
        UsuarioDao usuarioDao = new UsuarioDao();

        if (usuarioDao.registrarUsuario(usuario)) {
            JOptionPane.showMessageDialog(this, "Usuario registrado correctamente!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error: el usuario ya existe o no se pudo guardar!");
        }
    }
}

