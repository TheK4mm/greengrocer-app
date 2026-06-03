package com.greengrocer.view;

import com.greengrocer.config.AppConfig;
import com.greengrocer.model.Usuario;
import com.greengrocer.service.Session;
import com.greengrocer.view.components.SidebarButton;
import com.greengrocer.view.components.Toast;
import com.greengrocer.view.icons.VectorIcon;
import com.greengrocer.view.panels.CategoriasPanel;
import com.greengrocer.view.panels.ClientesPanel;
import com.greengrocer.view.panels.DashboardPanel;
import com.greengrocer.view.panels.NuevaVentaPanel;
import com.greengrocer.view.panels.ProductosPanel;
import com.greengrocer.view.panels.ProveedoresPanel;
import com.greengrocer.view.panels.ReportesPanel;
import com.greengrocer.view.panels.UsuariosPanel;
import com.greengrocer.view.panels.VentasPanel;
import com.greengrocer.view.theme.ColorPalette;
import com.greengrocer.view.theme.FontPalette;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana principal: sidebar de navegación + área de contenido con CardLayout.
 *
 * <p>El menú "Usuarios" solo es visible para administradores.</p>
 */
public class MainDashboard extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel content   = new JPanel(cards);

    private final List<SidebarButton> navButtons = new ArrayList<>();
    private DashboardPanel dashboardPanel;
    private NuevaVentaPanel nuevaVentaPanel;
    private ProductosPanel  productosPanel;

    public MainDashboard() {
        setTitle(AppConfig.get().businessName() + " · Sistema de gestión");
        setIconImage(new VectorIcon(VectorIcon.Glyph.LEAF, 32, ColorPalette.PRIMARY).toImage());
        setSize(1200, 760);                       // tamaño al restaurar
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);   // arranca maximizada
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        content.setBackground(ColorPalette.BACKGROUND);
        add(content, BorderLayout.CENTER);

        registerPanels();
        seleccionar("Inicio");
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BorderLayout());
        side.setBackground(ColorPalette.SIDEBAR_BG);
        side.setPreferredSize(new Dimension(230, 100));
        side.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ColorPalette.PRIMARY_DARK));

        // ---- Header con la marca ----
        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBorder(new EmptyBorder(24, 20, 18, 20));
        JLabel logo = new JLabel(AppConfig.get().businessName());
        logo.setFont(FontPalette.HEADER);
        logo.setForeground(ColorPalette.TEXT_ON_PRIMARY);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel tag = new JLabel("Punto de venta · Inventario");
        tag.setFont(FontPalette.SMALL);
        tag.setForeground(new java.awt.Color(0xC8E6C9));
        tag.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.add(logo);
        brand.add(tag);

        // ---- Botones de navegación ----
        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));

        nav.add(navButton(VectorIcon.Glyph.HOME,    "Inicio"));
        nav.add(navButton(VectorIcon.Glyph.CART,    "Nueva venta"));
        nav.add(navButton(VectorIcon.Glyph.BOX,     "Productos"));
        nav.add(navButton(VectorIcon.Glyph.TAG,     "Categorías"));
        nav.add(navButton(VectorIcon.Glyph.USER,    "Clientes"));
        nav.add(navButton(VectorIcon.Glyph.TRUCK,   "Proveedores"));
        nav.add(navButton(VectorIcon.Glyph.RECEIPT, "Ventas"));
        nav.add(navButton(VectorIcon.Glyph.CHART,   "Reportes"));
        if (Session.isAdmin()) {
            nav.add(navButton(VectorIcon.Glyph.USERS, "Usuarios"));
        }

        // ---- Footer con usuario logueado + logout ----
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBorder(new EmptyBorder(20, 20, 24, 20));
        Usuario u = Session.get();
        if (u != null) {
            JLabel name = new JLabel(u.getNombre());
            name.setFont(FontPalette.BODY_BOLD);
            name.setForeground(ColorPalette.TEXT_ON_PRIMARY);
            name.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel role = new JLabel(u.getRol().getLabel() + " · " + u.getNombreUsuario());
            role.setFont(FontPalette.SMALL);
            role.setForeground(new java.awt.Color(0xC8E6C9));
            role.setAlignmentX(Component.LEFT_ALIGNMENT);
            footer.add(name);
            footer.add(role);
            footer.add(javax.swing.Box.createVerticalStrut(12));
        }
        SidebarButton btnLogout = new SidebarButton(sidebarIcon(VectorIcon.Glyph.LOGOUT), "Cerrar sesión");
        btnLogout.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogout.addActionListener(e -> {
            if (Toast.confirm(this, "¿Cerrar la sesión y volver al inicio?")) {
                Session.clear();
                dispose();
                javax.swing.SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            }
        });
        footer.add(btnLogout);

        side.add(brand,  BorderLayout.NORTH);
        side.add(nav,    BorderLayout.CENTER);
        side.add(footer, BorderLayout.SOUTH);
        return side;
    }

    private SidebarButton navButton(VectorIcon.Glyph glyph, String label) {
        SidebarButton b = new SidebarButton(sidebarIcon(glyph), label);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.addActionListener(e -> seleccionar(label));
        navButtons.add(b);
        return b;
    }

    /** Ícono vectorial estándar del sidebar (tamaño y color consistentes). */
    private Icon sidebarIcon(VectorIcon.Glyph glyph) {
        return new VectorIcon(glyph, 18, ColorPalette.TEXT_ON_SIDEBAR);
    }

    private void registerPanels() {
        dashboardPanel  = new DashboardPanel();
        nuevaVentaPanel = new NuevaVentaPanel();
        productosPanel  = new ProductosPanel();
        content.add(wrap(dashboardPanel),       "Inicio");
        content.add(wrap(nuevaVentaPanel),      "Nueva venta");
        content.add(wrap(productosPanel),       "Productos");
        content.add(wrap(new CategoriasPanel()),  "Categorías");
        content.add(wrap(new ClientesPanel()),    "Clientes");
        content.add(wrap(new ProveedoresPanel()), "Proveedores");
        content.add(wrap(new VentasPanel()),      "Ventas");
        content.add(wrap(new ReportesPanel()),    "Reportes");
        if (Session.isAdmin()) {
            content.add(wrap(new UsuariosPanel()), "Usuarios");
        }
    }

    private JComponent wrap(JComponent c) {
        c.setOpaque(true);
        return c;
    }

    private void seleccionar(String name) {
        for (SidebarButton b : navButtons) {
            // El texto del SidebarButton es exactamente la etiqueta del módulo.
            b.setActive(b.getText().equals(name));
        }
        cards.show(content, name);
        if ("Inicio".equals(name) && dashboardPanel != null)        dashboardPanel.refrescar();
        if ("Nueva venta".equals(name) && nuevaVentaPanel != null)  nuevaVentaPanel.nuevaVenta();
    }
}
