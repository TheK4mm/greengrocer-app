package com.greengrocer.model.enums;

/** Roles de acceso al sistema. */
public enum RolUsuario {

    ADMIN("Administrador"),
    VENDEDOR("Vendedor");

    private final String label;

    RolUsuario(String label) { this.label = label; }

    public String getLabel() { return label; }

    @Override public String toString() { return label; }

    public static RolUsuario fromString(String raw) {
        if (raw == null) return VENDEDOR;
        try { return RolUsuario.valueOf(raw.trim().toUpperCase()); }
        catch (IllegalArgumentException ex) { return VENDEDOR; }
    }
}
