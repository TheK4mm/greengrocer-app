package com.greengrocer.service;

import com.greengrocer.model.Usuario;

/** Sesión del usuario autenticado durante la ejecución de la aplicación. */
public final class Session {

    private static Usuario current;

    private Session() { }

    public static void set(Usuario u) { current = u; }

    public static Usuario get() { return current; }

    public static boolean isLogged() { return current != null; }

    public static boolean isAdmin() { return current != null && current.isAdmin(); }

    public static void clear() { current = null; }
}
