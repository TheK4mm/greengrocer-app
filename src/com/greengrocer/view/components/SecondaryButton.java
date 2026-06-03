package com.greengrocer.view.components;

import com.greengrocer.view.theme.ColorPalette;
import com.greengrocer.view.theme.FontPalette;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Botón secundario: borde verde, fondo blanco, texto verde. */
public class SecondaryButton extends JButton {

    private final int radius = 10;

    public SecondaryButton(String text) {
        super(text);
        setFont(FontPalette.BUTTON);
        setForeground(ColorPalette.PRIMARY);
        setBorder(new EmptyBorder(10, 22, 10, 22));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color bg = ColorPalette.CARD_BG;
        if (getModel().isPressed())       bg = new Color(0xE0E0E0);
        else if (getModel().isRollover()) bg = new Color(0xF1F8E9);
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(ColorPalette.PRIMARY);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}
