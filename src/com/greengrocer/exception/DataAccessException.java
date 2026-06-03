package com.greengrocer.exception;

/** Errores irrecuperables al interactuar con la base de datos. */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
