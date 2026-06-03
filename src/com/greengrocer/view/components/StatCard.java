package com.greengrocer.view.components;

import com.greengrocer.view.theme.ColorPalette;
import com.greengrocer.view.theme.FontPalette;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;

/**
 * Tarjeta para destacar una métrica del negocio
 * (ventas del día, productos activos, etc.).
 */
public class StatCard extends RoundedPanel {

    private final JLabel valueLabel;
    private final JLabel titleLabel;
    private final JLabel hintLabel;
    private final JPanel accentStripe;

    public StatCard(String title, String value, String hint, Color accent) {
        super(14);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        accentStripe = new JPanel();
        accentStripe.setBackground(accent);
        accentStripe.setMaximumSize(new java.awt.Dimension(36, 4));
        accentStripe.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleLabel = new JLabel(title);
        titleLabel.setFont(FontPalette.SMALL);
        titleLabel.setForeground(ColorPalette.TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel = new JLabel(value);
        valueLabel.setFont(FontPalette.MONEY_XL);
        valueLabel.setForeground(ColorPalette.TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        hintLabel = new JLabel(hint == null ? " " : hint);
        hintLabel.setFont(FontPalette.SMALL);
        hintLabel.setForeground(ColorPalette.TEXT_MUTED);
        hintLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(accentStripe);
        add(javax.swing.Box.createVerticalStrut(8));
        add(titleLabel);
        add(javax.swing.Box.createVerticalStrut(6));
        add(valueLabel);
        add(javax.swing.Box.createVerticalStrut(4));
        add(hintLabel);
    }

    public void setValue(String value)  { valueLabel.setText(value); }
    public void setHint(String hint)    { hintLabel.setText(hint == null ? " " : hint); }
    public void setAccent(Color color)  { accentStripe.setBackground(color); }
}
