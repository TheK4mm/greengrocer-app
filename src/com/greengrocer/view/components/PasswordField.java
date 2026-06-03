package com.greengrocer.view.components;

import com.greengrocer.view.icons.VectorIcon;
import com.greengrocer.view.theme.ColorPalette;
import com.greengrocer.view.theme.FontPalette;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionListener;

/**
 * Campo de contraseña con un botón de ojo para mostrar/ocultar el texto.
 *
 * <p>Envuelve un {@link JPasswordField} sin borde dentro de un panel; el borde
 * y el fondo los aporta el contenedor ({@code FormField}), de modo que se ve
 * como un único campo con el ícono a la derecha.</p>
 */
public class PasswordField extends JPanel {

    private final JPasswordField field  = new JPasswordField();
    private final JButton        toggle = new JButton();
    private final char           echoChar;
    private boolean              revealed = false;

    public PasswordField() {
        super(new BorderLayout(6, 0));
        setOpaque(false);
        echoChar = field.getEchoChar();

        field.setBorder(null);
        field.setOpaque(false);
        field.setFont(FontPalette.BODY);

        toggle.setIcon(eyeIcon());
        toggle.setFocusable(false);
        toggle.setBorderPainted(false);
        toggle.setContentAreaFilled(false);
        toggle.setOpaque(false);
        toggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggle.setToolTipText("Mostrar u ocultar la contraseña");
        toggle.addActionListener(e -> {
            revealed = !revealed;
            field.setEchoChar(revealed ? (char) 0 : echoChar);
            toggle.setIcon(eyeIcon());
        });

        add(field,  BorderLayout.CENTER);
        add(toggle, BorderLayout.EAST);
    }

    private Icon eyeIcon() {
        return new VectorIcon(revealed ? VectorIcon.Glyph.EYE_OFF : VectorIcon.Glyph.EYE,
                              16, ColorPalette.TEXT_SECONDARY);
    }

    // --- API equivalente a la de JPasswordField que usa la vista ---
    public char[] getPassword()                      { return field.getPassword(); }
    public void   setText(String text)               { field.setText(text); }
    public void   addActionListener(ActionListener l) { field.addActionListener(l); }
}
