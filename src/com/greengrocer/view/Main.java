package com.greengrocer.view;

import com.formdev.flatlaf.FlatLightLaf;
import com.greengrocer.service.AuthService;
import com.greengrocer.util.DatabaseConnection;
import com.greengrocer.view.components.Toast;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Punto de entrada de la aplicación. */
public final class Main {

    private Main() { }

    public static void main(String[] args) {
        configureLookAndFeel();
        registerShutdownHook();

        SwingUtilities.invokeLater(() -> {
            try {
                new AuthService().ensureDefaultAdmin();
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
            } catch (Exception ex) {
                Toast.error(null,
                        "No se pudo iniciar la aplicación.\n\n" + ex.getMessage()
                        + "\n\nRevise el archivo app.properties y la conexión a MySQL.");
            }
        });
    }

    private static void configureLookAndFeel() {
        try {
            // Look & Feel plano y moderno (tema claro) para toda la aplicación.
            FlatLightLaf.setup();
        } catch (Exception primary) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Cae al Look & Feel por defecto si todo falla.
            }
        }
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> DatabaseConnection.get().close(), "db-shutdown"));
    }
}
