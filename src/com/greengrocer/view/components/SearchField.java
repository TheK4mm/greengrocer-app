package com.greengrocer.view.components;

import com.greengrocer.view.theme.ColorPalette;
import com.greengrocer.view.theme.FontPalette;

import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/** Caja de búsqueda con borde redondeado y texto placeholder. */
public class SearchField extends JTextField {

    private final String placeholder;
    private boolean showingPlaceholder = true;

    public SearchField(String placeholderText) {
        super();
        this.placeholder = placeholderText;
        setFont(FontPalette.BODY);
        setBorder(new EmptyBorder(8, 14, 8, 14));
        setOpaque(false);
        setForeground(ColorPalette.TEXT_MUTED);
        setText(placeholder);

        addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (showingPlaceholder) {
                    setText("");
                    setForeground(ColorPalette.TEXT_PRIMARY);
                    showingPlaceholder = false;
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (getText().isBlank()) {
                    setText(placeholder);
                    setForeground(ColorPalette.TEXT_MUTED);
                    showingPlaceholder = true;
                }
            }
        });
    }

    /** Devuelve el contenido real (sin placeholder). */
    public String getQuery() {
        return showingPlaceholder ? "" : getText();
    }

    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        g2.setColor(ColorPalette.BORDER);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        g2.dispose();
        super.paintComponent(g);
    }
}
