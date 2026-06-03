package com.greengrocer.view.components;

import com.greengrocer.view.theme.ColorPalette;
import com.greengrocer.view.theme.FontPalette;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Botón de navegación lateral. Mantiene un estado seleccionado/activo
 * que se pinta con una banda lateral de acento.
 */
public class SidebarButton extends JButton {

    private boolean active = false;

    public SidebarButton(Icon icon, String text) {
        super(text);
        setIcon(icon);
        setIconTextGap(14);
        setHorizontalAlignment(SwingConstants.LEFT);
        setFont(FontPalette.BODY_BOLD);
        setForeground(ColorPalette.TEXT_ON_SIDEBAR);
        setBorder(new EmptyBorder(12, 18, 12, 18));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void setActive(boolean active) {
        this.active = active;
        repaint();
    }

    public boolean isActive() { return active; }

    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color bg;
        if (active)                       bg = ColorPalette.PRIMARY;
        else if (getModel().isPressed())  bg = ColorPalette.PRIMARY;
        else if (getModel().isRollover()) bg = ColorPalette.SIDEBAR_HOVER;
        else                              bg = ColorPalette.SIDEBAR_BG;
        g2.setColor(bg);
        g2.fillRect(0, 0, getWidth(), getHeight());
        if (active) {
            g2.setColor(ColorPalette.ACCENT);
            g2.fillRect(0, 0, 4, getHeight());
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
