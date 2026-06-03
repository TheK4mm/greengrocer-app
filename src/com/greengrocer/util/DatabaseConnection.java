package com.greengrocer.util;

import com.greengrocer.config.AppConfig;
import com.greengrocer.exception.DataAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton responsable de proveer la conexión JDBC.
 *
 * <p>Mantiene una sola conexión compartida para toda la aplicación
 * (suficiente para un cliente Swing de escritorio mono-usuario) y
 * la reabre transparentemente si quedó cerrada o inválida.</p>
 */
public final class DatabaseConnection {

    private static final DatabaseConnection INSTANCE = new DatabaseConnection();

    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new DataAccessException(
                    "Driver JDBC de MySQL no encontrado en el classpath.", ex);
        }
    }

    public static DatabaseConnection get() {
        return INSTANCE;
    }

    /** Devuelve la conexión activa, creándola si es necesario. */
    public synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                AppConfig cfg = AppConfig.get();
                connection = DriverManager.getConnection(
                        cfg.dbUrl(), cfg.dbUser(), cfg.dbPassword());
                connection.setAutoCommit(true);
            }
            return connection;
        } catch (SQLException ex) {
            throw new DataAccessException(
                    "No se pudo conectar a la base de datos: " + ex.getMessage(), ex);
        }
    }

    /** Cierra la conexión actual (al apagar la aplicación). */
    public synchronized void close() {
        if (connection != null) {
            try { connection.close(); } catch (SQLException ignored) { }
            connection = null;
        }
    }
}
