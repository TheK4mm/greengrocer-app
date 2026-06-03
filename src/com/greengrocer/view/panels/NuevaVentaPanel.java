package com.greengrocer.view.panels;

import com.greengrocer.config.AppConfig;
import com.greengrocer.exception.BusinessException;
import com.greengrocer.model.Cliente;
import com.greengrocer.model.DetalleVenta;
import com.greengrocer.model.Producto;
import com.greengrocer.model.Venta;
import com.greengrocer.model.enums.MetodoPago;
import com.greengrocer.service.ClienteService;
import com.greengrocer.service.ProductoService;
import com.greengrocer.service.VentaService;
import com.greengrocer.util.CurrencyUtils;
import com.greengrocer.view.components.AppTable;
import com.greengrocer.view.components.DangerButton;
import com.greengrocer.view.components.FormField;
import com.greengrocer.view.components.PrimaryButton;
import com.greengrocer.view.components.RoundedPanel;
import com.greengrocer.view.components.SearchField;
import com.greengrocer.view.components.SectionHeader;
import com.greengrocer.view.components.Toast;
import com.greengrocer.view.theme.ColorPalette;
import com.greengrocer.view.theme.FontPalette;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/** Pantalla tipo punto de venta para registrar nuevas ventas. */
public class NuevaVentaPanel extends JPanel {

    private final ProductoService productoService = new ProductoService();
    private final ClienteService  clienteService  = new ClienteService();
    private final VentaService    ventaService    = new VentaService();

    private final SearchField searchProducto = new SearchField("Buscar producto por nombre o código...");
    private final DefaultTableModel modelProductos = new DefaultTableModel(
            new Object[]{"Código", "Producto", "Unidad", "Precio", "Stock"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final AppTable tblProductos = new AppTable(modelProductos);
    private final List<Producto> productosCache = new ArrayList<>();

    private final DefaultTableModel modelCart = new DefaultTableModel(
            new Object[]{"Producto", "Cant.", "P. Unit.", "Subtotal"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final AppTable tblCart = new AppTable(modelCart);
    private final List<DetalleVenta> carrito = new ArrayList<>();

    private final JComboBox<Cliente>    cboCliente = new JComboBox<>();
    private final JComboBox<MetodoPago> cboPago    = new JComboBox<>(MetodoPago.values());
    private final JTextArea txtObservaciones = new JTextArea(3, 20);

    private final JLabel lblComprobante = new JLabel();
    private final JLabel lblSubtotal    = new JLabel("$0");
    private final JLabel lblImpuesto    = new JLabel("$0");
    private final JLabel lblTotal       = new JLabel("$0");

    public NuevaVentaPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ColorPalette.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(new SectionHeader("Nueva venta", "Registro de venta tipo punto de caja"),
                BorderLayout.NORTH);

        JPanel split = new JPanel(new BorderLayout(16, 0));
        split.setOpaque(false);
        split.add(buildLeft(),  BorderLayout.CENTER);
        split.add(buildRight(), BorderLayout.EAST);
        add(split, BorderLayout.CENTER);

        searchProducto.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { recargarProductos(); }
            @Override public void removeUpdate(DocumentEvent e)  { recargarProductos(); }
            @Override public void changedUpdate(DocumentEvent e) { recargarProductos(); }
        });

        tblProductos.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) agregarProductoSeleccionado();
            }
        });

        tblCart.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblCart.getSelectedRow();
                    if (row >= 0) {
                        carrito.remove(tblCart.convertRowIndexToModel(row));
                        refrescarCarrito();
                    }
                }
            }
        });

        nuevaVenta();
    }

    private RoundedPanel buildLeft() {
        RoundedPanel card = new RoundedPanel(14);
        card.setLayout(new BorderLayout(0, 10));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel hint = new JLabel("Doble clic agrega al carrito");
        hint.setFont(FontPalette.SMALL);
        hint.setForeground(ColorPalette.TEXT_MUTED);
        top.add(hint, BorderLayout.WEST);
        top.add(searchProducto, BorderLayout.EAST);
        searchProducto.setPreferredSize(new Dimension(280, 36));

        card.add(top, BorderLayout.NORTH);
        card.add(new JScrollPane(tblProductos), BorderLayout.CENTER);
        return card;
    }

    private RoundedPanel buildRight() {
        RoundedPanel card = new RoundedPanel(14);
        card.setLayout(new BorderLayout(0, 10));
        card.setPreferredSize(new Dimension(360, 100));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Carrito");
        title.setFont(FontPalette.SUBHEADER);
        title.setForeground(ColorPalette.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblComprobante.setFont(FontPalette.SMALL);
        lblComprobante.setForeground(ColorPalette.TEXT_SECONDARY);
        lblComprobante.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(title);
        header.add(lblComprobante);

        JPanel topCombos = new JPanel(new GridLayout(2, 1, 0, 8));
        topCombos.setOpaque(false);
        topCombos.add(new FormField("Cliente",      cboCliente));
        topCombos.add(new FormField("Método pago",  cboPago));

        JPanel topWrap = new JPanel(new BorderLayout(0, 10));
        topWrap.setOpaque(false);
        topWrap.add(header,    BorderLayout.NORTH);
        topWrap.add(topCombos, BorderLayout.CENTER);

        // Totales + observaciones + acciones
        JPanel totals = new JPanel(new GridLayout(3, 2, 0, 4));
        totals.setOpaque(false);
        totals.setBorder(new EmptyBorder(8, 0, 0, 0));
        totals.add(money("Subtotal:", false));   totals.add(lblSubtotal);
        totals.add(money("Impuesto:", false));   totals.add(lblImpuesto);
        totals.add(money("Total:", true));       totals.add(asTotal(lblTotal));

        txtObservaciones.setFont(FontPalette.BODY);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setBorder(new EmptyBorder(6, 8, 6, 8));
        JScrollPane spObs = new JScrollPane(txtObservaciones);
        spObs.setPreferredSize(new Dimension(320, 60));

        PrimaryButton btnCobrar = new PrimaryButton("Cobrar venta");
        DangerButton  btnLimpiar = new DangerButton("Cancelar");
        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        actions.add(btnCobrar);
        actions.add(javax.swing.Box.createHorizontalStrut(8));
        actions.add(btnLimpiar);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        totals.setAlignmentX(Component.LEFT_ALIGNMENT);
        spObs.setAlignmentX(Component.LEFT_ALIGNMENT);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottom.add(totals);
        bottom.add(javax.swing.Box.createVerticalStrut(8));
        bottom.add(new FormField("Observaciones", spObs));
        bottom.add(javax.swing.Box.createVerticalStrut(10));
        bottom.add(actions);

        card.add(topWrap,                    BorderLayout.NORTH);
        card.add(new JScrollPane(tblCart),   BorderLayout.CENTER);
        card.add(bottom,                     BorderLayout.SOUTH);

        btnCobrar.addActionListener(e  -> cobrar());
        btnLimpiar.addActionListener(e -> nuevaVenta());

        return card;
    }

    private JLabel money(String t, boolean strong) {
        JLabel l = new JLabel(t);
        l.setFont(strong ? FontPalette.BODY_BOLD : FontPalette.BODY);
        l.setForeground(strong ? ColorPalette.TEXT_PRIMARY : ColorPalette.TEXT_SECONDARY);
        return l;
    }

    private JLabel asTotal(JLabel l) {
        l.setFont(FontPalette.MONEY_XL);
        l.setForeground(ColorPalette.PRIMARY_DARK);
        return l;
    }

    // ---- comportamiento ----

    public void nuevaVenta() {
        carrito.clear();
        lblComprobante.setText("Comprobante " + ventaService.siguienteComprobante());
        txtObservaciones.setText("");
        cboPago.setSelectedItem(MetodoPago.EFECTIVO);
        recargarClientes();
        recargarProductos();
        refrescarCarrito();
    }

    private void recargarClientes() {
        DefaultComboBoxModel<Cliente> m = new DefaultComboBoxModel<>();
        Cliente none = new Cliente(); none.setNombre("(Mostrador)"); m.addElement(none);
        for (Cliente c : clienteService.listarActivos()) m.addElement(c);
        cboCliente.setModel(m);
    }

    private void recargarProductos() {
        productosCache.clear();
        productosCache.addAll(productoService.buscar(searchProducto.getQuery()));
        modelProductos.setRowCount(0);
        for (Producto p : productosCache) {
            modelProductos.addRow(new Object[]{
                    p.getCodigo(),
                    p.getNombre(),
                    p.getUnidadMedida(),
                    CurrencyUtils.format(p.getPrecioVenta()),
                    p.getStock()
            });
        }
    }

    private void agregarProductoSeleccionado() {
        int row = tblProductos.getSelectedRow();
        if (row < 0) return;
        int modelRow = tblProductos.convertRowIndexToModel(row);
        Producto p = productosCache.get(modelRow);
        if (p.isStockAgotado()) {
            Toast.warn(this, "El producto está agotado.");
            return;
        }
        String raw = JOptionPane.showInputDialog(this,
                "Cantidad (" + p.getUnidadMedida() + ") para " + p.getNombre() + ":",
                "1");
        if (raw == null) return;
        BigDecimal cant;
        try {
            cant = new BigDecimal(raw.trim());
            if (cant.signum() <= 0) throw new BusinessException("Cantidad inválida.");
        } catch (Exception ex) {
            Toast.warn(this, "Cantidad inválida.");
            return;
        }
        int cantInt = cant.setScale(0, RoundingMode.CEILING).intValueExact();
        if (cantInt > p.getStock()) {
            Toast.warn(this, "No hay stock suficiente. Disponible: " + p.getStock());
            return;
        }

        // Si el producto ya está en el carrito, sumamos cantidad
        for (DetalleVenta d : carrito) {
            if (d.getIdProducto() == p.getId()) {
                d.setCantidad(d.getCantidad().add(cant));
                refrescarCarrito();
                return;
            }
        }
        DetalleVenta d = new DetalleVenta(p.getId(), cant, p.getPrecioVenta());
        d.setCodigoProducto(p.getCodigo());
        d.setNombreProducto(p.getNombre());
        d.setUnidadMedida(p.getUnidadMedida());
        carrito.add(d);
        refrescarCarrito();
    }

    private void refrescarCarrito() {
        modelCart.setRowCount(0);
        BigDecimal subtotal = BigDecimal.ZERO;
        for (DetalleVenta d : carrito) {
            modelCart.addRow(new Object[]{
                    d.getNombreProducto(),
                    d.getCantidad().stripTrailingZeros().toPlainString(),
                    CurrencyUtils.format(d.getPrecioUnitario()),
                    CurrencyUtils.format(d.getSubtotal())
            });
            subtotal = subtotal.add(d.getSubtotal());
        }
        BigDecimal impuesto = CurrencyUtils.round(subtotal.multiply(AppConfig.get().taxRate()));
        BigDecimal total    = CurrencyUtils.round(subtotal.add(impuesto));
        lblSubtotal.setText(CurrencyUtils.format(subtotal));
        lblImpuesto.setText(CurrencyUtils.format(impuesto));
        lblTotal.setText(CurrencyUtils.format(total));
    }

    private void cobrar() {
        try {
            Cliente sel = (Cliente) cboCliente.getSelectedItem();
            Integer idCliente = (sel != null && sel.getId() != 0) ? sel.getId() : null;
            Venta v = ventaService.registrar(
                    idCliente,
                    (MetodoPago) cboPago.getSelectedItem(),
                    txtObservaciones.getText(),
                    carrito);
            Toast.success(this, "Venta " + v.getNumeroComprobante() +
                    " registrada por " + CurrencyUtils.format(v.getTotal()));
            nuevaVenta();
        } catch (BusinessException be) {
            Toast.warn(this, be.getMessage());
        } catch (Exception ex) {
            Toast.error(this, "No se pudo registrar la venta: " + ex.getMessage());
        }
    }
}
