package com.greengrocer.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Carga las propiedades de la aplicación desde {@code /app.properties}
 * en el classpath. Cualquier valor puede ser sobrescrito mediante
 * una propiedad de sistema con el mismo nombre (útil para QA y testing).
 */
public final class AppConfig {

    private static final String RESOURCE = "/app.properties";
    private static final AppConfig INSTANCE = new AppConfig();

    private final Properties props = new Properties();

    private AppConfig() {
        try (InputStream in = AppConfig.class.getResourceAsStream(RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException(
                        "No se encontró " + RESOURCE + " en el classpath.");
            }
            // Se lee como UTF-8 (no el ISO-8859-1 por defecto de Properties)
            // para admitir tildes en valores como business.name.
            Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
            props.load(reader);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo leer " + RESOURCE, ex);
        }
    }

    public static AppConfig get() {
        return INSTANCE;
    }

    public String getString(String key, String fallback) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) return sys;
        return props.getProperty(key, fallback);
    }

    public int getInt(String key, int fallback) {
        try {
            return Integer.parseInt(getString(key, String.valueOf(fallback)).trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    public BigDecimal getDecimal(String key, BigDecimal fallback) {
        try {
            return new BigDecimal(getString(key, fallback.toPlainString()).trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    // --- atajos de uso frecuente ---------------------------------------

    public String dbUrl() {
        String host = getString("db.host", "localhost");
        int    port = getInt("db.port", 3306);
        String db   = getString("db.name", "greengrocer_db");
        String ssl  = getString("db.useSSL", "false");
        String tz   = getString("db.serverTimezone", "America/Lima");
        return "jdbc:mysql://" + host + ":" + port + "/" + db
                + "?useSSL=" + ssl
                + "&allowPublicKeyRetrieval=true"
                + "&useUnicode=true"
                + "&characterEncoding=UTF-8"
                + "&serverTimezone=" + tz;
    }

    public String dbUser()     { return getString("db.user", "root"); }
    public String dbPassword() { return getString("db.password", ""); }

    public String businessName()     { return getString("business.name", "Verdulería"); }
    public BigDecimal taxRate()      { return getDecimal("business.taxRate", new BigDecimal("0.19")); }
    public String currencySymbol()   { return getString("business.currencySymbol", "$"); }
}
