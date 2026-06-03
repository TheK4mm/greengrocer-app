package com.greengrocer.exception;

/**
 * Errores de regla de negocio o de validación.
 *
 * <p>Distinta de {@link DataAccessException}: estos se muestran al usuario
 * como mensaje amigable, no son fallos del sistema.</p>
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
