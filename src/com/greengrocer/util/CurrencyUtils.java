package com.greengrocer.util;

import com.greengrocer.config.AppConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Formateo monetario y aritmético consistente para toda la aplicación. */
public final class CurrencyUtils {

    // Pesos colombianos (COP): miles con '.', sin centavos en pantalla.
    private static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(Locale.forLanguageTag("es-CO"));
    private static final DecimalFormat        FORMAT  = buildFormat();

    private static DecimalFormat buildFormat() {
        DecimalFormat f = new DecimalFormat("###,##0", SYMBOLS);
        f.setRoundingMode(RoundingMode.HALF_UP);
        return f;
    }

    private CurrencyUtils() { }

    public static String format(BigDecimal amount) {
        if (amount == null) amount = BigDecimal.ZERO;
        // Convención COP: símbolo pegado y sin decimales, p. ej. $1.235
        return AppConfig.get().currencySymbol() + FORMAT.format(amount);
    }

    public static String formatPlain(BigDecimal amount) {
        if (amount == null) amount = BigDecimal.ZERO;
        return FORMAT.format(amount);
    }

    public static BigDecimal round(BigDecimal value) {
        if (value == null) return BigDecimal.ZERO;
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
