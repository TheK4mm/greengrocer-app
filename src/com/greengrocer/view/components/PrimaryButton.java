package com.greengrocer.view.components;

import com.greengrocer.view.theme.ColorPalette;
import com.greengrocer.view.theme.FontPalette;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Botón principal: fondo verde, texto blanco, esquinas redondeadas. */
public class PrimaryButton extends JButton {

    private Color base   = ColorPalette.PRIMARY;
    private Color hover  = ColorPalette.PRIMARY_LIGHT;
    private Color press  = ColorPalette.PRIMARY_DARK;
    private int   radius = 10;

    public PrimaryButton(String text) {
        super(text);
        setFont(FontPalette.BUTTON);
        setForeground(ColorPalette.TEXT_ON_PRIMARY);
        setBorder(new EmptyBorder(10, 22, 10, 22));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public PrimaryButton withColors(Color base, Color hover, Color press) {
        this.base = base; this.hover = hover; this.press = press;
        repaint();
        return this;
    }

    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color bg = base;
        if (!isEnabled())              bg = new Color(0xBDBDBD);
        else if (getModel().isPressed())  bg = press;
        else if (getModel().isRollover()) bg = hover;
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}
