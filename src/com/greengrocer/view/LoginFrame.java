package com.greengrocer.view;

import com.greengrocer.config.AppConfig;
import com.greengrocer.exception.BusinessException;
import com.greengrocer.model.Usuario;
import com.greengrocer.model.enums.RolUsuario;
import com.greengrocer.service.AuthService;
import com.greengrocer.service.Session;
import com.greengrocer.view.components.FormField;
import com.greengrocer.view.components.PrimaryButton;
import com.greengrocer.view.components.SecondaryButton;
import com.greengrocer.view.components.Toast;
import com.greengrocer.view.icons.VectorIcon;
import com.greengrocer.view.theme.ColorPalette;
import com.greengrocer.view.theme.FontPalette;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

/**
 * Pantalla de autenticación a pantalla completa.
 *
 * <p>Layout a dos paneles: a la izquierda la marca sobre fondo verde y a la
 * derecha el formulario, centrado y de ancho fijo para mantener la simetría en
 * cualquier resolución. Un {@link CardLayout} alterna entre las vistas de
 * inicio de sesión y de registro, ambas a pantalla completa.</p>
 */
public class LoginFrame extends JFrame {

    private static final int FORM_WIDTH = 380;
    private static final Color BRAND_TAG = new Color(0xC8E6C9);

    // Credenciales (login)
    private final JTextField     txtUsuario  = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();

    // Registro
    private final JTextField     txtRegNombre   = new JTextField();
    private final JTextField     txtRegUsuario  = new JTextField();
    private final JPasswordField txtRegPassword = new JPasswordField();
    private final JPasswordField txtRegConfirm  = new JPasswordField();

    private final AuthService authService = new AuthService();
    private final CardLayout  cards = new CardLayout();
    private final JPanel      root  = new JPanel();

    public LoginFrame() {
        setTitle("Iniciar sesión · " + AppConfig.get().businessName());
        setIconImage(new VectorIcon(VectorIcon.Glyph.LEAF, 32, ColorPalette.PRIMARY).toImage());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        setLayout(new BorderLayout());

        // Botón nativo de FlatLaf para mostrar/ocultar la contraseña.
        for (JPasswordField pf : new JPasswordField[]{txtPassword, txtRegPassword, txtRegConfirm}) {
            pf.putClientProperty("JPasswordField.showRevealButton", true);
        }

        root.setLayout(cards);
        root.add(buildView(buildLoginForm()),    "login");
        root.add(buildView(buildRegisterForm()), "register");
        add(root, BorderLayout.CENTER);

        // Enter dispara la acción de cada formulario
        txtPassword.addActionListener(e -> ingresar());
        txtRegConfirm.addActionListener(e -> registrar());

        // Tamaño base (al restaurar) + arranque maximizado a pantalla completa
        setSize(1000, 640);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    // ---------------------------------------------------------------- estructura

    /** Une el panel de marca (izquierda) con un formulario (derecha). */
    private JPanel buildView(JComponent form) {
        JPanel view = new JPanel(new GridLayout(1, 2));
        view.add(buildBrandPanel());
        view.add(form);
        return view;
    }

    /** Panel de marca con el logo vectorial y el nombre del negocio. */
    private JPanel buildBrandPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ColorPalette.PRIMARY_DARK);

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel(new VectorIcon(VectorIcon.Glyph.LEAF, 96, ColorPalette.TEXT_ON_PRIMARY));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brand = new JLabel(AppConfig.get().businessName());
        brand.setFont(FontPalette.HEADER_XL);
        brand.setForeground(ColorPalette.TEXT_ON_PRIMARY);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tag = new JLabel("Sistema de gestión integrada");
        tag.setFont(FontPalette.BODY);
        tag.setForeground(BRAND_TAG);
        tag.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(logo);
        box.add(Box.createVerticalStrut(22));
        box.add(brand);
        box.add(Box.createVerticalStrut(8));
        box.add(tag);

        panel.add(box);
        return panel;
    }

    /** Contenedor del formulario: ancho fijo, centrado sobre fondo claro. */
    private JPanel formContainer() {
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setMaximumSize(new Dimension(FORM_WIDTH, Integer.MAX_VALUE));
        return form;
    }

    private JPanel wrapForm(JPanel form) {
        JPanel side = new JPanel(new GridBagLayout());
        side.setBackground(ColorPalette.BACKGROUND);
        side.setBorder(new EmptyBorder(24, 24, 24, 24));
        side.add(form);
        return side;
    }

    // ---------------------------------------------------------------- formularios

    private JPanel buildLoginForm() {
        JPanel form = formContainer();

        FormField fUser = field("Usuario",     txtUsuario);
        FormField fPass = field("Contraseña",  txtPassword);

        PrimaryButton btnIngresar = new PrimaryButton("Ingresar");
        primary(btnIngresar);
        btnIngresar.addActionListener(e -> ingresar());

        SecondaryButton btnRegistrar = new SecondaryButton("Crear nuevo usuario");
        secondary(btnRegistrar);
        btnRegistrar.addActionListener(e -> {
            cards.show(root, "register");
            txtRegNombre.requestFocusInWindow();
        });

        form.add(label("Iniciar sesión", FontPalette.HEADER, ColorPalette.TEXT_PRIMARY));
        form.add(Box.createVerticalStrut(6));
        form.add(label("Ingresa tus credenciales para continuar", FontPalette.BODY, ColorPalette.TEXT_SECONDARY));
        form.add(Box.createVerticalStrut(28));
        form.add(fUser);
        form.add(Box.createVerticalStrut(14));
        form.add(fPass);
        form.add(Box.createVerticalStrut(28));
        form.add(btnIngresar);
        form.add(Box.createVerticalStrut(12));
        form.add(btnRegistrar);
        form.add(Box.createVerticalStrut(24));
        form.add(label("admin / admin123 al primer arranque", FontPalette.SMALL, ColorPalette.TEXT_MUTED));

        return wrapForm(form);
    }

    private JPanel buildRegisterForm() {
        JPanel form = formContainer();

        FormField fNombre  = field("Nombre completo",        txtRegNombre);
        FormField fUsuario = field("Usuario",                txtRegUsuario);
        FormField fPass    = field("Contraseña",             txtRegPassword);
        FormField fConf    = field("Confirmar contraseña",   txtRegConfirm);

        PrimaryButton btnRegistrar = new PrimaryButton("Registrar");
        primary(btnRegistrar);
        btnRegistrar.addActionListener(e -> registrar());

        SecondaryButton btnVolver = new SecondaryButton("Volver al inicio");
        secondary(btnVolver);
        btnVolver.addActionListener(e -> cards.show(root, "login"));

        form.add(label("Crear cuenta", FontPalette.HEADER, ColorPalette.TEXT_PRIMARY));
        form.add(Box.createVerticalStrut(6));
        form.add(label("Las nuevas cuentas se crean con rol Vendedor.", FontPalette.BODY, ColorPalette.TEXT_SECONDARY));
        form.add(Box.createVerticalStrut(24));
        form.add(fNombre);
        form.add(Box.createVerticalStrut(12));
        form.add(fUsuario);
        form.add(Box.createVerticalStrut(12));
        form.add(fPass);
        form.add(Box.createVerticalStrut(12));
        form.add(fConf);
        form.add(Box.createVerticalStrut(24));
        form.add(btnRegistrar);
        form.add(Box.createVerticalStrut(12));
        form.add(btnVolver);

        return wrapForm(form);
    }

    // ---------------------------------------------------------------- helpers UI

    private static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private static FormField field(String labelText, JComponent input) {
        FormField f = new FormField(labelText, input);
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Ancho fijo: define el ancho de toda la columna del formulario.
        f.setPreferredSize(new Dimension(FORM_WIDTH, 62));
        f.setMaximumSize(new Dimension(FORM_WIDTH, 62));
        return f;
    }

    private static void primary(PrimaryButton b) {
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
    }

    private static void secondary(SecondaryButton b) {
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
    }

    // ---------------------------------------------------------------- acciones

    private void ingresar() {
        try {
            String user = txtUsuario.getText();
            String pass = new String(txtPassword.getPassword());
            Usuario u = authService.login(user, pass);
            Session.set(u);
            new MainDashboard().setVisible(true);
            dispose();
        } catch (BusinessException be) {
            Toast.warn(this, be.getMessage());
            txtPassword.setText("");
        } catch (Exception ex) {
            Toast.error(this, "Error al iniciar sesión: " + ex.getMessage());
        }
    }

    private void registrar() {
        try {
            String pass    = new String(txtRegPassword.getPassword());
            String confirm = new String(txtRegConfirm.getPassword());
            if (!pass.equals(confirm)) {
                throw new BusinessException("Las contraseñas no coinciden.");
            }
            authService.registrar(txtRegNombre.getText(), txtRegUsuario.getText(),
                                  pass, RolUsuario.VENDEDOR);
            Toast.success(this, "Usuario creado. Ya puede iniciar sesión.");
            limpiarRegistro();
            cards.show(root, "login");
        } catch (BusinessException be) {
            Toast.warn(this, be.getMessage());
        } catch (Exception ex) {
            Toast.error(this, "No se pudo registrar: " + ex.getMessage());
        }
    }

    private void limpiarRegistro() {
        txtRegNombre.setText("");
        txtRegUsuario.setText("");
        txtRegPassword.setText("");
        txtRegConfirm.setText("");
    }
}
