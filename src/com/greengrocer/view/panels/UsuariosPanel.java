package com.greengrocer.view.panels;

import com.greengrocer.exception.BusinessException;
import com.greengrocer.model.Usuario;
import com.greengrocer.model.enums.RolUsuario;
import com.greengrocer.service.Session;
import com.greengrocer.service.UsuarioService;
import com.greengrocer.view.components.AppTable;
import com.greengrocer.view.components.DangerButton;
import com.greengrocer.view.components.FormField;
import com.greengrocer.view.components.PrimaryButton;
import com.greengrocer.view.components.RoundedPanel;
import com.greengrocer.view.components.SecondaryButton;
import com.greengrocer.view.components.SectionHeader;
import com.greengrocer.view.components.Toast;
import com.greengrocer.view.theme.ColorPalette;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Administración de usuarios del sistema (solo ADMIN). */
public class UsuariosPanel extends JPanel {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final UsuarioService service = new UsuarioService();

    private final JTextField txtNombre       = new JTextField();
    private final JTextField txtNombreUsuario = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final JComboBox<RolUsuario> cboRol = new JComboBox<>(RolUsuario.values());
    private final JCheckBox chkActivo = new JCheckBox("Activo", true);
    private int selectedId = 0;

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Usuario", "Rol", "Activo", "Último acceso"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final AppTable tabla = new AppTable(modelo);

    public UsuariosPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ColorPalette.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(new SectionHeader("Usuarios", "Cuentas de acceso al sistema"),
                BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(buildForm());
        center.add(javax.swing.Box.createVerticalStrut(16));
        center.add(buildTable());
        add(center, BorderLayout.CENTER);

        cargar();
    }

    private RoundedPanel buildForm() {
        RoundedPanel card = new RoundedPanel(14);
        card.setLayout(new BorderLayout(0, 12));

        JPanel grid = new JPanel(new GridLayout(2, 3, 12, 12));
        grid.setOpaque(false);
        grid.add(new FormField("Nombre completo", txtNombre));
        grid.add(new FormField("Usuario",         txtNombreUsuario));
        grid.add(new FormField("Contraseña",      txtPassword));
        grid.add(new FormField("Rol",             cboRol));
        JPanel chkWrap = new JPanel(new BorderLayout()); chkWrap.setOpaque(false);
        chkWrap.add(chkActivo, BorderLayout.SOUTH);
        grid.add(chkWrap);
        grid.add(new JPanel() {{ setOpaque(false); }});

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        PrimaryButton btnGuardar      = new PrimaryButton("Guardar");
        SecondaryButton btnNuevo      = new SecondaryButton("Nuevo");
        SecondaryButton btnResetPass  = new SecondaryButton("Restablecer contraseña");
        SecondaryButton btnRecargar   = new SecondaryButton("Recargar");
        DangerButton    btnEliminar   = new DangerButton("Eliminar");
        actions.add(btnGuardar);
        actions.add(javax.swing.Box.createHorizontalStrut(8));
        actions.add(btnNuevo);
        actions.add(javax.swing.Box.createHorizontalStrut(8));
        actions.add(btnResetPass);
        actions.add(javax.swing.Box.createHorizontalStrut(8));
        actions.add(btnRecargar);
        actions.add(javax.swing.Box.createHorizontalGlue());
        actions.add(btnEliminar);

        card.add(grid, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardar());
        btnNuevo.addActionListener(e -> limpiar());
        btnRecargar.addActionListener(e -> cargar());
        btnResetPass.addActionListener(e -> resetearPassword());
        btnEliminar.addActionListener(e -> eliminar());
        return card;
    }

    private RoundedPanel buildTable() {
        RoundedPanel card = new RoundedPanel(14);
        card.setLayout(new BorderLayout());
        card.add(new JScrollPane(tabla), BorderLayout.CENTER);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = tabla.getSelectedRow();
            if (row < 0) return;
            int r = tabla.convertRowIndexToModel(row);
            selectedId = (int) modelo.getValueAt(r, 0);
            txtNombre.setText(s(modelo.getValueAt(r, 1)));
            txtNombreUsuario.setText(s(modelo.getValueAt(r, 2)));
            txtPassword.setText("");
            txtNombreUsuario.setEditable(false);
            cboRol.setSelectedItem(RolUsuario.fromString(String.valueOf(modelo.getValueAt(r, 3)).toUpperCase()));
            chkActivo.setSelected("Sí".equals(modelo.getValueAt(r, 4)));
        });
        return card;
    }

    private void cargar() {
        modelo.setRowCount(0);
        List<Usuario> lista = service.listar();
        for (Usuario u : lista) {
            modelo.addRow(new Object[]{
                    u.getId(), u.getNombre(), u.getNombreUsuario(),
                    u.getRol().getLabel(),
                    u.isActivo() ? "Sí" : "No",
                    u.getUltimoAcceso() != null ? u.getUltimoAcceso().format(FMT) : "—"
            });
        }
        limpiar();
    }

    private void guardar() {
        try {
            String pass = new String(txtPassword.getPassword());
            if (selectedId == 0) {
                service.crear(txtNombre.getText(), txtNombreUsuario.getText(),
                              pass, (RolUsuario) cboRol.getSelectedItem());
                Toast.success(this, "Usuario creado.");
            } else {
                service.actualizarPerfil(selectedId, txtNombre.getText(),
                                         (RolUsuario) cboRol.getSelectedItem(),
                                         chkActivo.isSelected());
                Toast.success(this, "Usuario actualizado.");
            }
            cargar();
        } catch (BusinessException be) {
            Toast.warn(this, be.getMessage());
        } catch (Exception ex) {
            Toast.error(this, "No se pudo guardar: " + ex.getMessage());
        }
    }

    private void resetearPassword() {
        if (selectedId == 0) { Toast.warn(this, "Seleccione un usuario."); return; }
        String nueva = new String(txtPassword.getPassword());
        if (nueva.isBlank()) { Toast.warn(this, "Escriba la nueva contraseña en el formulario."); return; }
        try {
            service.restablecerPassword(selectedId, nueva);
            Toast.success(this, "Contraseña restablecida.");
            txtPassword.setText("");
        } catch (BusinessException be) {
            Toast.warn(this, be.getMessage());
        }
    }

    private void eliminar() {
        if (selectedId == 0) { Toast.warn(this, "Seleccione un usuario."); return; }
        if (Session.get() != null && Session.get().getId() == selectedId) {
            Toast.warn(this, "No puede eliminar su propia cuenta.");
            return;
        }
        if (!Toast.confirm(this, "¿Eliminar el usuario seleccionado?")) return;
        try {
            service.eliminar(selectedId);
            Toast.success(this, "Usuario eliminado.");
            cargar();
        } catch (BusinessException be) {
            Toast.warn(this, be.getMessage());
        } catch (Exception ex) {
            Toast.error(this, ex.getMessage());
        }
    }

    private void limpiar() {
        selectedId = 0;
        txtNombre.setText(""); txtNombreUsuario.setText(""); txtPassword.setText("");
        txtNombreUsuario.setEditable(true);
        cboRol.setSelectedItem(RolUsuario.VENDEDOR);
        chkActivo.setSelected(true);
        tabla.clearSelection();
    }

    private static String s(Object o) { return o == null ? "" : String.valueOf(o); }
}
