package com.greengrocer.view.components;

import com.greengrocer.view.theme.ColorPalette;
import com.greengrocer.view.theme.FontPalette;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Component;

/** Encabezado de panel con título y subtítulo. */
public class SectionHeader extends JPanel {

    public SectionHeader(String title, String subtitle) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FontPalette.HEADER_XL);
        titleLbl.setForeground(ColorPalette.TEXT_PRIMARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(titleLbl);

        if (subtitle != null && !subtitle.isBlank()) {
            JLabel sub = new JLabel(subtitle);
            sub.setFont(FontPalette.BODY);
            sub.setForeground(ColorPalette.TEXT_SECONDARY);
            sub.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(sub);
        }
    }
}
