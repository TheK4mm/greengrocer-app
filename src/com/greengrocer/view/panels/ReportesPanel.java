package com.greengrocer.view.panels;

import com.greengrocer.model.Producto;
import com.greengrocer.service.ProductoService;
import com.greengrocer.service.ReporteService;
import com.greengrocer.util.CurrencyUtils;
import com.greengrocer.view.components.AppTable;
import com.greengrocer.view.components.PrimaryButton;
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
import java.util.List;

/** Indicadores clave del negocio y productos con stock crítico. */
public class ReportesPanel extends JPanel {

    private final ReporteService  reporte  = new ReporteService();
    private final ProductoService producto = new ProductoService();

    private final StatCard cardProductos;
    private final StatCard cardStock;
    private final StatCard cardVentasHoy;
    private final StatCard cardIngresosMes;

    private final DefaultTableModel modelTop = new DefaultTableModel(
            new Object[]{"Código", "Producto", "Unidades", "Ingresos"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final DefaultTableModel modelStock = new DefaultTableModel(
            new Object[]{"Código", "Producto", "Categoría", "Stock", "Mínimo"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    public ReportesPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(ColorPalette.BACKGROUND);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(new SectionHeader("Reportes", "Métricas del negocio y alertas de stock"),
                BorderLayout.WEST);
        PrimaryButton btnRefrescar = new PrimaryButton("Actualizar");
        btnRefrescar.addActionListener(e -> refrescar());
        header.add(btnRefrescar, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        cardProductos   = new StatCard("Productos activos", "0",       "Catálogo vigente",  ColorPalette.PRIMARY);
        cardStock       = new StatCard("Stock crítico",     "0",       "A reponer",         ColorPalette.WARNING);
        cardVentasHoy   = new StatCard("Ventas de hoy",     "0",       "Comprobantes",      ColorPalette.SUCCESS);
        cardIngresosMes = new StatCard("Ingresos del mes",  "$0", "Acumulado mensual", ColorPalette.INFO);

        JPanel kpis = new JPanel(new GridLayout(1, 4, 16, 0));
        kpis.setOpaque(false);
        kpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        kpis.add(cardProductos);
        kpis.add(cardStock);
        kpis.add(cardVentasHoy);
        kpis.add(cardIngresosMes);

        RoundedPanel cardTop = sectionCard("Top productos vendidos");
        cardTop.add(new JScrollPane(new AppTable(modelTop)), BorderLayout.CENTER);

        RoundedPanel cardStockTbl = sectionCard("Productos por reponer");
        cardStockTbl.add(new JScrollPane(new AppTable(modelStock)), BorderLayout.CENTER);

        JPanel tablas = new JPanel(new GridLayout(1, 2, 16, 0));
        tablas.setOpaque(false);
        tablas.add(cardTop);
        tablas.add(cardStockTbl);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(kpis);
        center.add(javax.swing.Box.createVerticalStrut(16));
        center.add(tablas);
        add(center, BorderLayout.CENTER);

        refrescar();
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

    private void refrescar() {
        cardProductos.setValue(String.valueOf(reporte.productosActivos()));
        cardStock.setValue(String.valueOf(reporte.stockCritico()));
        cardVentasHoy.setValue(String.valueOf(reporte.ventasDelDia()));
        cardIngresosMes.setValue(CurrencyUtils.format(reporte.ingresosDelMes()));

        modelTop.setRowCount(0);
        for (Object[] r : reporte.topProductos(15)) {
            modelTop.addRow(new Object[]{
                    r[0], r[1], r[2], CurrencyUtils.format((BigDecimal) r[3])
            });
        }

        modelStock.setRowCount(0);
        List<Producto> stockBajo = producto.listarStockCritico();
        for (Producto p : stockBajo) {
            modelStock.addRow(new Object[]{
                    p.getCodigo(), p.getNombre(),
                    p.getNombreCategoria(), p.getStock(), p.getStockMinimo()
            });
        }
    }
}
