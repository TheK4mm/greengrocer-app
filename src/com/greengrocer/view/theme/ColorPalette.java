package com.greengrocer.view.theme;

import java.awt.Color;

/** Paleta de colores corporativa. Inspirada en una identidad fresca y verde. */
public final class ColorPalette {

    private ColorPalette() { }

    // Marca
    public static final Color PRIMARY       = new Color(0x2E7D32);  // verde principal
    public static final Color PRIMARY_DARK  = new Color(0x1B5E20);
    public static final Color PRIMARY_LIGHT = new Color(0x43A047);
    public static final Color ACCENT        = new Color(0xFFB300);  // naranja acento

    // Fondos
    public static final Color BACKGROUND    = new Color(0xF4F6F8);
    public static final Color SIDEBAR_BG    = new Color(0x1B5E20);
    public static final Color SIDEBAR_HOVER = new Color(0x2E7D32);
    public static final Color CARD_BG       = Color.WHITE;
    public static final Color HEADER_BG     = new Color(0xE8F5E9);

    // Texto
    public static final Color TEXT_PRIMARY   = new Color(0x212121);
    public static final Color TEXT_SECONDARY = new Color(0x616161);
    public static final Color TEXT_MUTED     = new Color(0x9E9E9E);
    public static final Color TEXT_ON_PRIMARY = Color.WHITE;
    public static final Color TEXT_ON_SIDEBAR = new Color(0xE8F5E9);

    // Estados
    public static final Color SUCCESS = new Color(0x388E3C);
    public static final Color WARNING = new Color(0xF57C00);
    public static final Color DANGER  = new Color(0xC62828);
    public static final Color INFO    = new Color(0x1976D2);

    // Bordes / divisores
    public static final Color BORDER  = new Color(0xE0E0E0);
    public static final Color DIVIDER = new Color(0xEEEEEE);
}
