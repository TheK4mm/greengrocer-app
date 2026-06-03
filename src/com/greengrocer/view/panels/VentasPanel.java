package com.greengrocer.view.panels;

import com.greengrocer.exception.BusinessException;
import com.greengrocer.model.DetalleVenta;
import com.greengrocer.model.Venta;
import com.greengrocer.service.Session;
import com.greengrocer.service.VentaService;
import com.greengrocer.util.CurrencyUtils;
import com.greengrocer.view.components.AppTable;
import com.greengrocer.view.components.DangerButton;
import com.greengrocer.view.components.FormField;
import com.greengrocer.view.components.PrimaryButton;
import com.greengrocer.view.components.RoundedPanel;
import com.greengrocer.view.components.SecondaryButton;
import com.greengrocer.view.components.SectionHeader;
import com.greengrocer.view.components.Toast;
import com.greengrocer.view.theme.ColorPalette;
import com.greengrocer.view.theme.FontPalette;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/** Historial de ventas con filtros por fecha y opción de anular. */
public class VentasPanel extends JPanel {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final VentaService service = new VentaService();

    private final JTextField txtDesde = new JTextField(LocalDate.now().withDayOfMonth(1).toString());
    private final JTextField txtHasta = new JTextField(LocalDate.now().toString());
    private int selectedId = 0;

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Comprobante", "Fecha", "Cliente", "Vendedor",
                         "Método", "Total", "Estado"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final AppTable tabla = new AppTable(modelo);

    public VentasPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ColorPalette.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(new SectionHeader("Ventas", "Historial de comprobantes emitidos"),
                BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(buildFiltros());
        center.add(javax.swing.Box.createVerticalStrut(16));
        center.add(buildTable());
        add(center, BorderLayout.CENTER);

        cargarRecientes();
    }

    private RoundedPanel buildFiltros() {
        RoundedPanel card = new RoundedPanel(14);
        card.setLayout(new BorderLayout(0, 12));

        JPanel grid = new JPanel(new GridLayout(1, 4, 12, 12));
        grid.setOpaque(false);
        grid.add(new FormField("Desde (YYYY-MM-DD)", txtDesde));
        grid.add(new FormField("Hasta (YYYY-MM-DD)", txtHasta));
        grid.add(new JPanel() {{ setOpaque(false); }});
        grid.add(new JPanel() {{ setOpaque(false); }});

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        PrimaryButton btnFiltrar = new PrimaryButton("Filtrar");
        SecondaryButton btnRecientes = new SecondaryButton("Recientes");
        SecondaryButton btnDetalle   = new SecondaryButton("Ver detalle");
        DangerButton btnAnular       = new DangerButton("Anular");
        actions.add(btnFiltrar);
        actions.add(javax.swing.Box.createHorizontalStrut(8));
        actions.add(btnRecientes);
        actions.add(javax.swing.Box.createHorizontalStrut(8));
        actions.add(btnDetalle);
        actions.add(javax.swing.Box.createHorizontalGlue());
        actions.add(btnAnular);

        card.add(grid, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);

        btnFiltrar.addActionListener(e -> filtrar());
        btnRecientes.addActionListener(e -> cargarRecientes());
        btnDetalle.addActionListener(e -> verDetalle());
        btnAnular.addActionListener(e -> anular());
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
            selectedId = (int) modelo.getValueAt(tabla.convertRowIndexToModel(row), 0);
        });
        return card;
    }

    private void cargarRecientes() {
        List<Venta> lista = service.listarRecientes(100);
        pintar(lista);
    }

    private void filtrar() {
        try {
            LocalDate desde = LocalDate.parse(txtDesde.getText().trim());
            LocalDate hasta = LocalDate.parse(txtHasta.getText().trim());
            pintar(service.listarPorRango(desde, hasta));
        } catch (BusinessException be) {
            Toast.warn(this, be.getMessage());
        } catch (Exception ex) {
            Toast.warn(this, "Fechas inválidas. Use formato YYYY-MM-DD.");
        }
    }

    private void pintar(List<Venta> lista) {
        modelo.setRowCount(0);
        for (Venta v : lista) {
            modelo.addRow(new Object[]{
                    v.getId(),
                    v.getNumeroComprobante(),
                    v.getFechaVenta() != null ? v.getFechaVenta().format(DT_FMT) : "",
                    v.getNombreCliente() != null ? v.getNombreCliente() : "(mostrador)",
                    v.getNombreUsuario(),
                    v.getMetodoPago().getLabel(),
                    CurrencyUtils.format(v.getTotal()),
                    v.getEstado().getLabel()
            });
        }
    }

    private void verDetalle() {
        if (selectedId == 0) { Toast.warn(this, "Seleccione una venta."); return; }
        Optional<Venta> opt = service.buscar(selectedId);
        if (opt.isEmpty()) { Toast.warn(this, "La venta no existe."); return; }
        Venta v = opt.get();
        List<DetalleVenta> detalles = v.getDetalles();

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-22s  %-8s  %-12s  %-12s%n",
                "Producto", "Cant.", "P. Unit.", "Subtotal"));
        sb.append("-".repeat(64)).append('\n');
        for (DetalleVenta d : detalles) {
            sb.append(String.format("%-22s  %-8s  %-12s  %-12s%n",
                    truncar(d.getNombreProducto(), 22),
                    d.getCantidad().stripTrailingZeros().toPlainString(),
                    CurrencyUtils.formatPlain(d.getPrecioUnitario()),
                    CurrencyUtils.formatPlain(d.getSubtotal())));
        }
        sb.append("-".repeat(64)).append('\n');
        sb.append(String.format("%50s %s%n", "Subtotal:", CurrencyUtils.format(v.getSubtotal())));
        sb.append(String.format("%50s %s%n", "Impuesto:", CurrencyUtils.format(v.getImpuesto())));
        sb.append(String.format("%50s %s%n", "TOTAL:",    CurrencyUtils.format(v.getTotal())));

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));
        area.setEditable(false);
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new java.awt.Dimension(560, 320));

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(label("Comprobante: "  + v.getNumeroComprobante()));
        top.add(label("Cliente: "      + (v.getNombreCliente() != null ? v.getNombreCliente() : "Mostrador")));
        top.add(label("Vendedor: "     + v.getNombreUsuario()));
        top.add(label("Fecha: "        + (v.getFechaVenta() != null ? v.getFechaVenta().format(DT_FMT) : "")));
        top.add(label("Estado: "       + v.getEstado().getLabel()));

        JPanel cont = new JPanel(new BorderLayout(0, 8));
        cont.add(top, BorderLayout.NORTH);
        cont.add(sp,  BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, cont,
                "Detalle de venta", JOptionPane.PLAIN_MESSAGE);
    }

    private void anular() {
        if (selectedId == 0) { Toast.warn(this, "Seleccione una venta."); return; }
        if (!Session.isAdmin()) { Toast.warn(this, "Solo un administrador puede anular ventas."); return; }
        if (!Toast.confirm(this, "¿Anular la venta seleccionada? Se devolverá el stock.")) return;
        try {
            service.anular(selectedId);
            Toast.success(this, "Venta anulada.");
            filtrar();
        } catch (BusinessException be) {
            Toast.warn(this, be.getMessage());
        } catch (Exception ex) {
            Toast.error(this, ex.getMessage());
        }
    }

    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setFont(FontPalette.BODY);
        return l;
    }

    private static String truncar(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }
}
