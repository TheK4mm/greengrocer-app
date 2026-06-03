package com.greengrocer.util;

import com.greengrocer.exception.BusinessException;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/** Validaciones reutilizables para datos de entrada del usuario. */
public final class ValidationUtils {

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private ValidationUtils() { }

    public static String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException("El campo \"" + field + "\" es obligatorio.");
        }
        return value.trim();
    }

    public static int requirePositiveInt(String raw, String field) {
        try {
            int n = Integer.parseInt(raw.trim());
            if (n < 0) {
                throw new BusinessException("El campo \"" + field + "\" no puede ser negativo.");
            }
            return n;
        } catch (NumberFormatException ex) {
            throw new BusinessException("El campo \"" + field + "\" debe ser un número entero.");
        }
    }

    public static BigDecimal requirePositiveDecimal(String raw, String field) {
        try {
            BigDecimal n = new BigDecimal(raw.trim());
            if (n.signum() < 0) {
                throw new BusinessException("El campo \"" + field + "\" no puede ser negativo.");
            }
            return n;
        } catch (NumberFormatException ex) {
            throw new BusinessException("El campo \"" + field + "\" debe ser un número válido.");
        }
    }

    public static BigDecimal requireStrictPositiveDecimal(String raw, String field) {
        BigDecimal n = requirePositiveDecimal(raw, field);
        if (n.signum() == 0) {
            throw new BusinessException("El campo \"" + field + "\" debe ser mayor que cero.");
        }
        return n;
    }

    public static String optionalEmail(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        String trimmed = value.trim();
        if (!EMAIL.matcher(trimmed).matches()) {
            throw new BusinessException("Correo electrónico inválido: " + trimmed);
        }
        return trimmed;
    }

    public static String optional(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
