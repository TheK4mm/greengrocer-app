package com.greengrocer.view.panels;

import com.greengrocer.exception.BusinessException;
import com.greengrocer.model.Categoria;
import com.greengrocer.model.Producto;
import com.greengrocer.model.Proveedor;
import com.greengrocer.service.CategoriaService;
import com.greengrocer.service.ProductoService;
import com.greengrocer.service.ProveedorService;
import com.greengrocer.util.CurrencyUtils;
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
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.List;

/** CRUD de productos con categoría, proveedor y búsqueda. */
public class ProductosPanel extends JPanel {

    private final ProductoService service          = new ProductoService();
    private final CategoriaService categoriaService = new CategoriaService();
    private final ProveedorService proveedorService = new ProveedorService();

    private final JTextField txtCodigo       = new JTextField();
    private final JTextField txtNombre       = new JTextField();
    private final JTextField txtDescripcion  = new JTextField();
    private final JTextField txtPrecioCompra = new JTextField();
    private final JTextField txtPrecioVenta  = new JTextField();
    private final JTextField txtStock        = new JTextField();
    private final JTextField txtStockMin     = new JTextField();
    private final JComboBox<String> cboUnidad = new JComboBox<>(
            new String[]{"kg", "gramo", "unidad", "atado", "paquete", "litro"});
    private final JComboBox<Categoria> cboCategoria = new JComboBox<>();
    private final JComboBox<Proveedor> cboProveedor = new JComboBox<>();
    private final JCheckBox chkActivo = new JCheckBox("Activo", true);
    private final SearchField search  = new SearchField("Buscar por código o nombre...");

    private int selectedId = 0;

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Código", "Nombre", "Categoría", "Precio venta",
                         "Stock", "Mín.", "Unidad", "Proveedor", "Activo"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final AppTable tabla = new AppTable(modelo);

    public ProductosPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ColorPalette.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(new SectionHeader("Productos", "Inventario y precios de la verdulería"),
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

        recargarCombos();
        cargar();
    }

    private RoundedPanel buildForm() {
        RoundedPanel card = new RoundedPanel(14);
        card.setLayout(new BorderLayout(0, 12));

        JPanel grid = new JPanel(new GridLayout(4, 3, 12, 12));
        grid.setOpaque(false);
        grid.add(new FormField("Código",           txtCodigo));
        grid.add(new FormField("Nombre",           txtNombre));
        grid.add(new FormField("Descripción",      txtDescripcion));
        grid.add(new FormField("Precio compra",    txtPrecioCompra));
        grid.add(new FormField("Precio venta",     txtPrecioVenta));
        grid.add(new FormField("Unidad de medida", cboUnidad));
        grid.add(new FormField("Stock actual",     txtStock));
        grid.add(new FormField("Stock mínimo",     txtStockMin));
        grid.add(new FormField("Categoría",        cboCategoria));
        grid.add(new FormField("Proveedor",        cboProveedor));
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
        btnRecargar.addActionListener(e -> { recargarCombos(); search.setText(""); cargar(); });
        btnEliminar.addActionListener(e -> eliminar());
        return card;
    }

    private RoundedPanel buildTable() {
        RoundedPanel card = new RoundedPanel(14);
        card.setLayout(new BorderLayout(0, 10));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        search.setPreferredSize(new Dimension(320, 36));
        topBar.add(search, BorderLayout.EAST);
        card.add(topBar, BorderLayout.NORTH);

        DefaultTableCellRenderer stockRenderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (sel) return c;
                int modelRow = t.convertRowIndexToModel(row);
                try {
                    int stock = Integer.parseInt(String.valueOf(modelo.getValueAt(modelRow, 5)));
                    int min   = Integer.parseInt(String.valueOf(modelo.getValueAt(modelRow, 6)));
                    if (stock <= 0)        c.setForeground(ColorPalette.DANGER);
                    else if (stock <= min) c.setForeground(ColorPalette.WARNING);
                    else                   c.setForeground(ColorPalette.TEXT_PRIMARY);
                } catch (Exception ex) {
                    c.setForeground(ColorPalette.TEXT_PRIMARY);
                }
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xFAFAFA));
                ((JLabel) c).setHorizontalAlignment(SwingConstants.LEFT);
                ((JLabel) c).setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        };
        tabla.setRenderer(5, stockRenderer);

        card.add(new JScrollPane(tabla), BorderLayout.CENTER);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = tabla.getSelectedRow();
            if (row < 0) return;
            int r = tabla.convertRowIndexToModel(row);
            try {
                Producto p = service.buscar(String.valueOf(modelo.getValueAt(r, 1)))
                        .stream().findFirst().orElse(null);
                if (p != null) cargarEnFormulario(p);
            } catch (Exception ignored) { }
        });
        return card;
    }

    private void cargarEnFormulario(Producto p) {
        selectedId = p.getId();
        txtCodigo.setText(p.getCodigo());
        txtNombre.setText(p.getNombre());
        txtDescripcion.setText(p.getDescripcion() != null ? p.getDescripcion() : "");
        txtPrecioCompra.setText(p.getPrecioCompra() != null ? p.getPrecioCompra().toPlainString() : "0");
        txtPrecioVenta.setText(p.getPrecioVenta() != null ? p.getPrecioVenta().toPlainString() : "0");
        txtStock.setText(String.valueOf(p.getStock()));
        txtStockMin.setText(String.valueOf(p.getStockMinimo()));
        cboUnidad.setSelectedItem(p.getUnidadMedida());
        seleccionarCategoria(p.getIdCategoria());
        seleccionarProveedor(p.getIdProveedor());
        chkActivo.setSelected(p.isActivo());
    }

    private void recargarCombos() {
        DefaultComboBoxModel<Categoria> mc = new DefaultComboBoxModel<>();
        for (Categoria c : categoriaService.listarActivas()) mc.addElement(c);
        cboCategoria.setModel(mc);

        DefaultComboBoxModel<Proveedor> mp = new DefaultComboBoxModel<>();
        Proveedor sin = new Proveedor(); sin.setNombre("(Sin proveedor)"); mp.addElement(sin);
        for (Proveedor p : proveedorService.listarActivos()) mp.addElement(p);
        cboProveedor.setModel(mp);
    }

    private void seleccionarCategoria(int id) {
        for (int i = 0; i < cboCategoria.getItemCount(); i++) {
            if (cboCategoria.getItemAt(i).getId() == id) {
                cboCategoria.setSelectedIndex(i);
                return;
            }
        }
    }

    private void seleccionarProveedor(Integer id) {
        if (id == null) { cboProveedor.setSelectedIndex(0); return; }
        for (int i = 0; i < cboProveedor.getItemCount(); i++) {
            if (cboProveedor.getItemAt(i).getId() == id) {
                cboProveedor.setSelectedIndex(i);
                return;
            }
        }
    }

    private void cargar() {
        modelo.setRowCount(0);
        List<Producto> productos = service.buscar(search.getQuery());
        for (Producto p : productos) {
            modelo.addRow(new Object[]{
                    p.getId(),
                    p.getCodigo(),
                    p.getNombre(),
                    p.getNombreCategoria(),
                    CurrencyUtils.formatPlain(p.getPrecioVenta()),
                    p.getStock(),
                    p.getStockMinimo(),
                    p.getUnidadMedida(),
                    p.getNombreProveedor() != null ? p.getNombreProveedor() : "",
                    p.isActivo() ? "Sí" : "No"
            });
        }
    }

    private void guardar() {
        try {
            Producto p = new Producto();
            p.setId(selectedId);
            p.setCodigo(txtCodigo.getText().trim());
            p.setNombre(txtNombre.getText().trim());
            p.setDescripcion(txtDescripcion.getText().trim());
            p.setPrecioCompra(parseDecimal(txtPrecioCompra.getText(), "Precio compra"));
            p.setPrecioVenta(parseDecimal(txtPrecioVenta.getText(),  "Precio venta"));
            p.setStock(parseInt(txtStock.getText(), "Stock"));
            p.setStockMinimo(parseInt(txtStockMin.getText(), "Stock mínimo"));
            p.setUnidadMedida(String.valueOf(cboUnidad.getSelectedItem()));
            Categoria cat = (Categoria) cboCategoria.getSelectedItem();
            if (cat == null) throw new BusinessException("Seleccione una categoría.");
            p.setIdCategoria(cat.getId());
            Proveedor prov = (Proveedor) cboProveedor.getSelectedItem();
            p.setIdProveedor(prov != null && prov.getId() != 0 ? prov.getId() : null);
            p.setActivo(chkActivo.isSelected());

            if (selectedId == 0) {
                service.crear(p);
                Toast.success(this, "Producto creado.");
            } else {
                service.actualizar(p);
                Toast.success(this, "Producto actualizado.");
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
        if (selectedId == 0) { Toast.warn(this, "Seleccione un producto."); return; }
        if (!Toast.confirm(this, "¿Eliminar el producto seleccionado?")) return;
        try {
            service.eliminar(selectedId);
            Toast.success(this, "Producto eliminado.");
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
        txtCodigo.setText(""); txtNombre.setText(""); txtDescripcion.setText("");
        txtPrecioCompra.setText("0"); txtPrecioVenta.setText("0");
        txtStock.setText("0"); txtStockMin.setText("5");
        cboUnidad.setSelectedIndex(0);
        if (cboCategoria.getItemCount() > 0) cboCategoria.setSelectedIndex(0);
        if (cboProveedor.getItemCount() > 0) cboProveedor.setSelectedIndex(0);
        chkActivo.setSelected(true);
        tabla.clearSelection();
    }

    private BigDecimal parseDecimal(String raw, String field) {
        try { return new BigDecimal(raw.trim()); }
        catch (Exception ex) {
            throw new BusinessException("El campo \"" + field + "\" debe ser numérico.");
        }
    }

    private int parseInt(String raw, String field) {
        try { return Integer.parseInt(raw.trim()); }
        catch (Exception ex) {
            throw new BusinessException("El campo \"" + field + "\" debe ser entero.");
        }
    }
}
