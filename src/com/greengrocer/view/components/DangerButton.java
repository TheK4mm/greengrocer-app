package com.greengrocer.view.components;

import com.greengrocer.view.theme.ColorPalette;

/** Variante roja del PrimaryButton para acciones destructivas. */
public class DangerButton extends PrimaryButton {
    public DangerButton(String text) {
        super(text);
        withColors(ColorPalette.DANGER,
                   new java.awt.Color(0xE53935),
                   new java.awt.Color(0xB71C1C));
    }
}
