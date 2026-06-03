package com.greengrocer.view.components;

import com.greengrocer.view.theme.ColorPalette;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Panel con esquinas redondeadas y un borde sutil. */
public class RoundedPanel extends JPanel {

    private final int radius;
    private Color borderColor = ColorPalette.BORDER;

    public RoundedPanel(int radius) {
        this(radius, ColorPalette.CARD_BG);
    }

    public RoundedPanel(int radius, Color background) {
        this.radius = radius;
        setOpaque(false);
        setBackground(background);
        setBorder(new EmptyBorder(16, 16, 16, 16));
    }

    public void setBorderColor(Color c) { this.borderColor = c; repaint(); }

    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
