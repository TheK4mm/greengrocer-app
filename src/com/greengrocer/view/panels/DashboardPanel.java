package com.greengrocer.view.panels;

import com.greengrocer.model.Venta;
import com.greengrocer.service.ReporteService;
import com.greengrocer.service.Session;
import com.greengrocer.service.VentaService;
import com.greengrocer.util.CurrencyUtils;
import com.greengrocer.view.components.AppTable;
import com.greengrocer.view.components.RoundedPanel;
import com.greengrocer.view.components.SectionHeader;
import com.greengrocer.view.components.StatCard;
import com.greengrocer.view.theme.ColorPalette;
import com.greengrocer.view.theme.FontPalette;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Pantalla principal: KPIs + listas recientes. */
public class DashboardPanel extends JPanel {

    private final ReporteService reporte = new ReporteService();
    private final VentaService    ventas  = new VentaService();
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    private final StatCard cardIngresosDia;
    private final StatCard cardIngresosMes;
    private final StatCard cardVentasDia;
    private final StatCard cardStockCritico;
    private final DefaultTableModel modelVentas = new DefaultTableModel(
            new Object[]{"Comprobante", "Fecha", "Cliente", "Pago", "Total"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final DefaultTableModel modelTop = new DefaultTableModel(
            new Object[]{"Código", "Producto", "Unidades", "Ingresos"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    public DashboardPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ColorPalette.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        // ---------- Cabecera ----------
        String saludo = "Hola, " + (Session.get() != null ? Session.get().getNombre() : "");
        add(new SectionHeader("Panel principal", saludo + " · resumen del negocio en tiempo real"),
                BorderLayout.NORTH);

        // ---------- Tarjetas KPI ----------
        cardIngresosDia  = new StatCard("Ingresos del día",  "$0", "Ventas completadas hoy",   ColorPalette.PRIMARY);
        cardIngresosMes  = new StatCard("Ingresos del mes",  "$0", "Acumulado del mes actual", ColorPalette.INFO);
        cardVentasDia    = new StatCard("Ventas del día",    "0",       "Comprobantes emitidos",    ColorPalette.SUCCESS);
        cardStockCritico = new StatCard("Stock crítico",     "0",       "Productos por reponer",    ColorPalette.WARNING);

        JPanel kpis = new JPanel(new GridLayout(1, 4, 16, 0));
        kpis.setOpaque(false);
        kpis.add(cardIngresosDia);
        kpis.add(cardIngresosMes);
        kpis.add(cardVentasDia);
        kpis.add(cardStockCritico);

        // ---------- Tablas (ventas recientes + top productos) ----------
        RoundedPanel cardRecientes = sectionCard("Ventas recientes");
        AppTable tblVentas = new AppTable(modelVentas);
        cardRecientes.add(new JScrollPane(tblVentas), BorderLayout.CENTER);

        RoundedPanel cardTop = sectionCard("Productos más vendidos");
        AppTable tblTop = new AppTable(modelTop);
        cardTop.add(new JScrollPane(tblTop), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(1, 2, 16, 0));
        bottom.setOpaque(false);
        bottom.add(cardRecientes);
        bottom.add(cardTop);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        kpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        center.add(kpis);
        center.add(javax.swing.Box.createVerticalStrut(16));
        center.add(bottom);

        add(center, BorderLayout.CENTER);

        refrescar();
    }

    public void refrescar() {
        BigDecimal ingDia = reporte.ingresosDelDia();
        BigDecimal ingMes = reporte.ingresosDelMes();
        cardIngresosDia.setValue(CurrencyUtils.format(ingDia));
        cardIngresosMes.setValue(CurrencyUtils.format(ingMes));
        cardVentasDia.setValue(String.valueOf(reporte.ventasDelDia()));
        cardStockCritico.setValue(String.valueOf(reporte.stockCritico()));

        modelVentas.setRowCount(0);
        List<Venta> recientes = ventas.listarRecientes(8);
        for (Venta v : recientes) {
            modelVentas.addRow(new Object[]{
                    v.getNumeroComprobante(),
                    v.getFechaVenta() != null ? v.getFechaVenta().format(DT_FMT) : "",
                    v.getNombreCliente() != null ? v.getNombreCliente() : "(mostrador)",
                    v.getMetodoPago().getLabel(),
                    CurrencyUtils.format(v.getTotal())
            });
        }

        modelTop.setRowCount(0);
        for (Object[] row : reporte.topProductos(8)) {
            modelTop.addRow(new Object[]{
                    row[0], row[1], row[2], CurrencyUtils.format((BigDecimal) row[3])
            });
        }
    }

    private RoundedPanel sectionCard(String title) {
        RoundedPanel card = new RoundedPanel(14);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel lbl = new JLabel(title);
        lbl.setFont(FontPalette.SUBHEADER);
        lbl.setForeground(ColorPalette.TEXT_PRIMARY);
        card.add(lbl, BorderLayout.NORTH);
        return card;
    }
}
