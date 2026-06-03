package com.greengrocer.view.panels;

import com.greengrocer.exception.BusinessException;
import com.greengrocer.model.Categoria;
import com.greengrocer.service.CategoriaService;
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

/** CRUD de categorías. */
public class CategoriasPanel extends JPanel {

    private final CategoriaService service = new CategoriaService();

    private final JTextField txtNombre = new JTextField();
    private final JTextField txtDescripcion = new JTextField();
    private final JCheckBox  chkActivo = new JCheckBox("Activa", true);
    private int selectedId = 0;

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Descripción", "Activa", "Creada"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final AppTable tabla = new AppTable(modelo);

    public CategoriasPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ColorPalette.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(new SectionHeader("Categorías", "Organiza los productos en familias de venta"),
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

        JPanel grid = new JPanel(new GridLayout(1, 3, 12, 12));
        grid.setOpaque(false);
        grid.add(new FormField("Nombre", txtNombre));
        grid.add(new FormField("Descripción", txtDescripcion));
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
            int modelRow = tabla.convertRowIndexToModel(row);
            selectedId   = (int) modelo.getValueAt(modelRow, 0);
            txtNombre.setText(String.valueOf(modelo.getValueAt(modelRow, 1)));
            txtDescripcion.setText(String.valueOf(modelo.getValueAt(modelRow, 2)));
            chkActivo.setSelected("Sí".equals(modelo.getValueAt(modelRow, 3)));
        });
        return card;
    }

    private void cargar() {
        modelo.setRowCount(0);
        List<Categoria> lista = service.listar();
        for (Categoria c : lista) {
            modelo.addRow(new Object[]{
                    c.getId(),
                    c.getNombre(),
                    c.getDescripcion() != null ? c.getDescripcion() : "",
                    c.isActivo() ? "Sí" : "No",
                    c.getFechaCreacion() != null ? c.getFechaCreacion().toLocalDate() : ""
            });
        }
        limpiar();
    }

    private void guardar() {
        try {
            if (selectedId == 0) {
                service.crear(txtNombre.getText(), txtDescripcion.getText());
                Toast.success(this, "Categoría creada.");
            } else {
                Categoria c = new Categoria();
                c.setId(selectedId);
                c.setNombre(txtNombre.getText());
                c.setDescripcion(txtDescripcion.getText());
                c.setActivo(chkActivo.isSelected());
                service.actualizar(c);
                Toast.success(this, "Categoría actualizada.");
            }
            cargar();
        } catch (BusinessException be) {
            Toast.warn(this, be.getMessage());
        } catch (Exception ex) {
            Toast.error(this, "No se pudo guardar: " + ex.getMessage());
        }
    }

    private void eliminar() {
        if (selectedId == 0) {
            Toast.warn(this, "Seleccione una categoría primero.");
            return;
        }
        if (!Toast.confirm(this, "¿Eliminar la categoría seleccionada?")) return;
        try {
            service.eliminar(selectedId);
            Toast.success(this, "Categoría eliminada.");
            cargar();
        } catch (BusinessException be) {
            Toast.warn(this, be.getMessage());
        } catch (Exception ex) {
            Toast.error(this, ex.getMessage());
        }
    }

    private void limpiar() {
        selectedId = 0;
        txtNombre.setText("");
        txtDescripcion.setText("");
        chkActivo.setSelected(true);
        tabla.clearSelection();
    }
}
