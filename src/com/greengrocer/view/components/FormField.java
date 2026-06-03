package com.greengrocer.view.components;

import com.greengrocer.view.theme.ColorPalette;
import com.greengrocer.view.theme.FontPalette;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;

/** Etiqueta + campo apilados verticalmente para formularios. */
public class FormField extends JPanel {

    private final JLabel label;
    private final JComponent input;

    public FormField(String labelText, JComponent input) {
        super(new BorderLayout(0, 4));
        setOpaque(false);

        label = new JLabel(labelText);
        label.setFont(FontPalette.SMALL);
        label.setForeground(ColorPalette.TEXT_SECONDARY);

        this.input = input;
        styleInput(input);

        add(label, BorderLayout.NORTH);
        add(input, BorderLayout.CENTER);
    }

    /** Crea un FormField con un JTextField vacío. */
    public static FormField text(String labelText) {
        JTextField tf = new JTextField();
        return new FormField(labelText, tf);
    }

    public JComponent input() { return input; }
    public JLabel labelComponent() { return label; }

    private void styleInput(JComponent c) {
        c.setFont(FontPalette.BODY);
        c.setBorder(new CompoundBorder(
                new LineBorder(ColorPalette.BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)));
        c.setBackground(ColorPalette.CARD_BG);
        c.setPreferredSize(new Dimension(160, 36));
    }
}
