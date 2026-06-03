package com.greengrocer.view.panels;

import com.greengrocer.exception.BusinessException;
import com.greengrocer.model.Cliente;
import com.greengrocer.service.ClienteService;
import com.greengrocer.view.components.AppTable;
import com.greengrocer.view.components.DangerButton;
import com.greengrocer.view.components.FormField;
import com.greengrocer.view.components.PrimaryButton;
import com.greengrocer.view.components.RoundedPanel;
import com.greengrocer.view.components.SearchField;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

/** CRUD de clientes con búsqueda. */
public class ClientesPanel extends JPanel {

    private final ClienteService service = new ClienteService();

    private final JTextField txtNombre   = new JTextField();
    private final JTextField txtDni      = new JTextField();
    private final JTextField txtTelefono = new JTextField();
    private final JTextField txtEmail    = new JTextField();
    private final JTextField txtDireccion= new JTextField();
    private final JCheckBox  chkActivo   = new JCheckBox("Activo", true);
    private final SearchField search     = new SearchField("Buscar por nombre o DNI...");
    private int selectedId = 0;

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "DNI", "Teléfono", "Correo", "Dirección", "Activo"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final AppTable tabla = new AppTable(modelo);

    public ClientesPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ColorPalette.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(new SectionHeader("Clientes", "Cartera de clientes registrados"),
                BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(buildForm());
        center.add(javax.swing.Box.createVerticalStrut(16));
        center.add(buildTable());
        add(center, BorderLayout.CENTER);

        search.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { cargar(); }
            @Override public void removeUpdate(DocumentEvent e)  { cargar(); }
            @Override public void changedUpdate(DocumentEvent e) { cargar(); }
        });
        cargar();
    }

    private RoundedPanel buildForm() {
        RoundedPanel card = new RoundedPanel(14);
        card.setLayout(new BorderLayout(0, 12));

        JPanel grid = new JPanel(new GridLayout(2, 3, 12, 12));
        grid.setOpaque(false);
        grid.add(new FormField("Nombre",    txtNombre));
        grid.add(new FormField("DNI",       txtDni));
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
        btnRecargar.addActionListener(e -> { search.setText(""); cargar(); });
        btnEliminar.addActionListener(e -> eliminar());
        return card;
    }

    private RoundedPanel buildTable() {
        RoundedPanel card = new RoundedPanel(14);
        card.setLayout(new BorderLayout(0, 10));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        search.setPreferredSize(new java.awt.Dimension(320, 36));
        topBar.add(search, BorderLayout.EAST);
        card.add(topBar, BorderLayout.NORTH);
        card.add(new JScrollPane(tabla), BorderLayout.CENTER);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = tabla.getSelectedRow();
            if (row < 0) return;
            int r = tabla.convertRowIndexToModel(row);
            selectedId   = (int) modelo.getValueAt(r, 0);
            txtNombre.setText(s(modelo.getValueAt(r, 1)));
            txtDni.setText(s(modelo.getValueAt(r, 2)));
            txtTelefono.setText(s(modelo.getValueAt(r, 3)));
            txtEmail.setText(s(modelo.getValueAt(r, 4)));
            txtDireccion.setText(s(modelo.getValueAt(r, 5)));
            chkActivo.setSelected("Sí".equals(modelo.getValueAt(r, 6)));
        });
        return card;
    }

    private void cargar() {
        modelo.setRowCount(0);
        List<Cliente> lista = service.buscar(search.getQuery());
        for (Cliente c : lista) {
            modelo.addRow(new Object[]{
                    c.getId(), c.getNombre(), s(c.getDni()), s(c.getTelefono()),
                    s(c.getEmail()), s(c.getDireccion()), c.isActivo() ? "Sí" : "No"
            });
        }
    }

    private void guardar() {
        try {
            Cliente c = new Cliente();
            c.setId(selectedId);
            c.setNombre(txtNombre.getText());
            c.setDni(txtDni.getText());
            c.setTelefono(txtTelefono.getText());
            c.setEmail(txtEmail.getText());
            c.setDireccion(txtDireccion.getText());
            c.setActivo(chkActivo.isSelected());
            if (selectedId == 0) {
                service.crear(c);
                Toast.success(this, "Cliente creado.");
            } else {
                service.actualizar(c);
                Toast.success(this, "Cliente actualizado.");
            }
            cargar();
            limpiar();
        } catch (BusinessException be) {
            Toast.warn(this, be.getMessage());
        } catch (Exception ex) {
            Toast.error(this, "No se pudo guardar: " + ex.getMessage());
        }
    }

    private void eliminar() {
        if (selectedId == 0) { Toast.warn(this, "Seleccione un cliente."); return; }
        if (!Toast.confirm(this, "¿Eliminar el cliente seleccionado?")) return;
        try {
            service.eliminar(selectedId);
            Toast.success(this, "Cliente eliminado.");
            cargar();
            limpiar();
        } catch (BusinessException be) {
            Toast.warn(this, be.getMessage());
        } catch (Exception ex) {
            Toast.error(this, ex.getMessage());
        }
    }

    private void limpiar() {
        selectedId = 0;
        txtNombre.setText(""); txtDni.setText(""); txtTelefono.setText("");
        txtEmail.setText("");  txtDireccion.setText("");
        chkActivo.setSelected(true);
        tabla.clearSelection();
    }

    private static String s(Object o) { return o == null ? "" : String.valueOf(o); }
}
