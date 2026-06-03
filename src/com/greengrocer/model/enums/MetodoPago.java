package com.greengrocer.model.enums;

/** Métodos de pago aceptados en una venta. */
public enum MetodoPago {

    EFECTIVO("Efectivo"),
    TARJETA("Tarjeta"),
    TRANSFERENCIA("Transferencia"),
    YAPE_PLIN("Yape / Plin");

    private final String label;

    MetodoPago(String label) { this.label = label; }

    public String getLabel() { return label; }

    @Override public String toString() { return label; }

    public static MetodoPago fromString(String raw) {
        if (raw == null) return EFECTIVO;
        try { return MetodoPago.valueOf(raw.trim().toUpperCase()); }
        catch (IllegalArgumentException ex) { return EFECTIVO; }
    }
}
