package com.greengrocer.view.panels;

import com.greengrocer.exception.BusinessException;
import com.greengrocer.model.Proveedor;
import com.greengrocer.service.ProveedorService;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

/** CRUD de proveedores. */
public class ProveedoresPanel extends JPanel {

    private final ProveedorService service = new ProveedorService();

    private final JTextField txtNombre    = new JTextField();
    private final JTextField txtRuc       = new JTextField();
    private final JTextField txtTelefono  = new JTextField();
    private final JTextField txtEmail     = new JTextField();
    private final JTextField txtDireccion = new JTextField();
    private final JCheckBox  chkActivo    = new JCheckBox("Activo", true);
    private int selectedId = 0;

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "RUC", "Teléfono", "Correo", "Dirección", "Activo"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final AppTable tabla = new AppTable(modelo);

    public ProveedoresPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ColorPalette.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(new SectionHeader("Proveedores", "Empresas que abastecen la verdulería"),
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
        grid.add(new FormField("Nombre",    txtNombre));
        grid.add(new FormField("RUC",       txtRuc));
        grid.add(new FormField("Teléfono",  txtTelefono));
        grid.add(new FormField("Correo",    txtEmail));
        grid.add(new FormField("Dirección", txtDireccion));
        JPanel chkWrap = new JPanel(new BorderLayout()); chkWrap.setOpaque(false);
        chkWrap.add(chkActivo, BorderLayout.SOUTH);
        grid.add(chkWrap);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        PrimaryButton btnGuardar    = new PrimaryButton("Guardar");
        SecondaryButton btnNuevo    = new SecondaryButton("Nuevo");
        SecondaryButton btnRecargar = new SecondaryButton("Recargar");
        DangerButton btnEliminar    = new DangerButton("Eliminar");
        actions.add(btnGuardar);
        actions.add(javax.swing.Box.createHorizontalStrut(8));
        actions.add(btnNuevo);
        actions.add(javax.swing.Box.createHorizontalStrut(8));
        actions.add(btnRecargar);
        actions.add(javax.swing.Box.createHorizontalGlue());
        actions.add(btnEliminar);

        card.add(grid, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardar());
        btnNuevo.addActionListener(e -> limpiar());
        btnRecargar.addActionListener(e -> cargar());
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
            selectedId  = (int) modelo.getValueAt(r, 0);
            txtNombre.setText(s(modelo.getValueAt(r, 1)));
            txtRuc.setText(s(modelo.getValueAt(r, 2)));
            txtTelefono.setText(s(modelo.getValueAt(r, 3)));
            txtEmail.setText(s(modelo.getValueAt(r, 4)));
            txtDireccion.setText(s(modelo.getValueAt(r, 5)));
            chkActivo.setSelected("Sí".equals(modelo.getValueAt(r, 6)));
        });
        return card;
    }

    private void cargar() {
        modelo.setRowCount(0);
        List<Proveedor> lista = service.listar();
        for (Proveedor p : lista) {
            modelo.addRow(new Object[]{
                    p.getId(), p.getNombre(), s(p.getRuc()), s(p.getTelefono()),
                    s(p.getEmail()), s(p.getDireccion()),
                    p.isActivo() ? "Sí" : "No"
            });
        }
        limpiar();
    }

    private void guardar() {
        try {
            Proveedor p = new Proveedor();
            p.setId(selectedId);
            p.setNombre(txtNombre.getText());
            p.setRuc(txtRuc.getText());
            p.setTelefono(txtTelefono.getText());
            p.setEmail(txtEmail.getText());
            p.setDireccion(txtDireccion.getText());
            p.setActivo(chkActivo.isSelected());
            if (selectedId == 0) {
                service.crear(p);
                Toast.success(this, "Proveedor creado.");
            } else {
                service.actualizar(p);
                Toast.success(this, "Proveedor actualizado.");
            }
            cargar();
        } catch (BusinessException be) {
            Toast.warn(this, be.getMessage());
        } catch (Exception ex) {
            Toast.error(this, "No se pudo guardar: " + ex.getMessage());
        }
    }

    private void eliminar() {
        if (selectedId == 0) { Toast.warn(this, "Seleccione un proveedor."); return; }
        if (!Toast.confirm(this, "¿Eliminar el proveedor seleccionado?")) return;
        try {
            service.eliminar(selectedId);
            Toast.success(this, "Proveedor eliminado.");
            cargar();
        } catch (BusinessException be) {
            Toast.warn(this, be.getMessage());
        } catch (Exception ex) {
            Toast.error(this, ex.getMessage());
        }
    }

    private void limpiar() {
        selectedId = 0;
        txtNombre.setText(""); txtRuc.setText(""); txtTelefono.setText("");
        txtEmail.setText(""); txtDireccion.setText("");
        chkActivo.setSelected(true);
        tabla.clearSelection();
    }

    private static String s(Object o) { return o == null ? "" : String.valueOf(o); }
}
