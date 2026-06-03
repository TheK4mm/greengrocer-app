package com.greengrocer.model.enums;

/** Estado de una venta. */
public enum EstadoVenta {

    COMPLETADA("Completada"),
    ANULADA("Anulada");

    private final String label;

    EstadoVenta(String label) { this.label = label; }

    public String getLabel() { return label; }

    @Override public String toString() { return label; }

    public static EstadoVenta fromString(String raw) {
        if (raw == null) return COMPLETADA;
        try { return EstadoVenta.valueOf(raw.trim().toUpperCase()); }
        catch (IllegalArgumentException ex) { return COMPLETADA; }
    }
}
