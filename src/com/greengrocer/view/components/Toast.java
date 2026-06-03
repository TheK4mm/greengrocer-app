package com.greengrocer.view.components;

import com.greengrocer.view.theme.ColorPalette;
import com.greengrocer.view.theme.FontPalette;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.awt.Font;

/**
 * Atajos consistentes para mostrar mensajes al usuario.
 *
 * <p>Centraliza el uso de {@link JOptionPane} para que todos los mensajes
 * de la app compartan el mismo formato. En el futuro podría reemplazarse
 * por un toast no modal sin tocar las llamadas existentes.</p>
 */
public final class Toast {

    private Toast() { }

    static {
        UIManagerTweaks.apply();
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Información",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void success(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Operación exitosa",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void warn(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Atención",
                JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Confirmación",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
                == JOptionPane.YES_OPTION;
    }

    /** Ajustes globales de Swing aplicados una sola vez. */
    private static final class UIManagerTweaks {
        static void apply() {
            javax.swing.UIManager.put("OptionPane.messageFont",
                    new Font(FontPalette.BODY.getFamily(), Font.PLAIN, 14));
            javax.swing.UIManager.put("OptionPane.buttonFont", FontPalette.BUTTON);
            javax.swing.UIManager.put("OptionPane.background", ColorPalette.BACKGROUND);
            javax.swing.UIManager.put("Panel.background",       ColorPalette.BACKGROUND);
        }
    }
}
