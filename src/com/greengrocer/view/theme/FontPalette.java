package com.greengrocer.view.theme;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** Fuentes tipográficas estándar de la aplicación. */
public final class FontPalette {

    private FontPalette() { }

    private static final String FAMILY = pickFamily();

    public static final Font HEADER_XL = new Font(FAMILY, Font.BOLD,  26);
    public static final Font HEADER    = new Font(FAMILY, Font.BOLD,  20);
    public static final Font SUBHEADER = new Font(FAMILY, Font.BOLD,  16);
    public static final Font BODY      = new Font(FAMILY, Font.PLAIN, 14);
    public static final Font BODY_BOLD = new Font(FAMILY, Font.BOLD,  14);
    public static final Font SMALL     = new Font(FAMILY, Font.PLAIN, 12);
    public static final Font BUTTON    = new Font(FAMILY, Font.BOLD,  14);
    public static final Font TABLE     = new Font(FAMILY, Font.PLAIN, 13);
    public static final Font TABLE_HEAD= new Font(FAMILY, Font.BOLD,  13);
    public static final Font MONEY_XL  = new Font(FAMILY, Font.BOLD,  28);

    private static String pickFamily() {
        Set<String> available = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getAvailableFontFamilyNames()));
        for (String candidate : new String[]{
                "Segoe UI", "Inter", "SF Pro Text", "Roboto",
                "Helvetica Neue", "Helvetica", "Arial"}) {
            if (available.contains(candidate)) return candidate;
        }
        return Font.SANS_SERIF;
    }
}
