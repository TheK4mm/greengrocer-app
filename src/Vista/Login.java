package Vista;

import javax.swing.*;
import Controlador.UsuarioDao;
import Modelo.Usuario;

public class Login extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnIngresar, btnRegistrar;

    public Login() {
        
        setTitle("Login");
        setSize(350, 250);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setBounds(30, 30, 80, 25);
        add(lblUsuario);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(120, 30, 150, 25);
        add(txtUsuario);

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setBounds(30, 70, 80, 25);
        add(lblContrasena);

        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(120, 70, 150, 25);
        add(txtContrasena);

        btnIngresar = new JButton("Ingresar");
        btnIngresar.setBounds(120, 110, 150, 30);
        add(btnIngresar);

        btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBounds(120, 150, 150, 30);
        add(btnRegistrar);

        //Validar login
        btnIngresar.addActionListener(e -> validarLogin());

        //Abrir ventana de registro
        btnRegistrar.addActionListener(e -> abrirVentanaRegistro());

        setLocationRelativeTo(null);
    }

    public void validarLogin() {
        String usuarioTexto = txtUsuario.getText();
        String contrasenaTexto = new String(txtContrasena.getPassword());

        Usuario usuario = new Usuario(usuarioTexto, contrasenaTexto);
        UsuarioDao usuarioDao = new UsuarioDao();

        if (usuarioDao.validarLogin(usuario)) {
            JOptionPane.showMessageDialog(null, "Bienvenido " + usuario.getNombreUsuario());
            FrmProducto menu = new FrmProducto();
            menu.setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(null, "Credenciales incorrectas");
        }
    }

    //Solo abre la ventana de registro de usuario
    public void abrirVentanaRegistro() {
        FrmRegistrarUsuario registro = new FrmRegistrarUsuario();
        registro.setVisible(true);
    }
}
