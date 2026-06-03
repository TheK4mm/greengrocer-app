package com.greengrocer.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Generación y verificación de contraseñas mediante PBKDF2-HMAC-SHA256.
 *
 * <p>Cada usuario tiene su propio {@code salt} aleatorio. El hash se
 * almacena en Base64 y la verificación es resistente a timing attacks.</p>
 */
public final class PasswordHasher {

    private static final String ALGORITHM    = "PBKDF2WithHmacSHA256";
    private static final int    ITERATIONS   = 100_000;
    private static final int    HASH_BITS    = 256;
    private static final int    SALT_BYTES   = 16;

    private static final SecureRandom RNG = new SecureRandom();

    private PasswordHasher() { }

    /** Genera un salt aleatorio en Base64. */
    public static String newSalt() {
        byte[] salt = new byte[SALT_BYTES];
        RNG.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /** Calcula el hash PBKDF2 de la contraseña con el salt dado. */
    public static String hash(String plain, String saltB64) {
        if (plain == null) plain = "";
        try {
            byte[] salt = Base64.getDecoder().decode(saltB64);
            KeySpec spec = new PBEKeySpec(
                    plain.toCharArray(), salt, ITERATIONS, HASH_BITS);
            byte[] hash = SecretKeyFactory.getInstance(ALGORITHM)
                    .generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "No se pudo calcular el hash de la contraseña.", ex);
        }
    }

    /** Verifica una contraseña contra el hash almacenado (constant-time). */
    public static boolean verify(String plain, String saltB64, String expectedHashB64) {
        String actual = hash(plain, saltB64);
        return constantTimeEquals(actual, expectedHashB64);
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) return false;
        int diff = 0;
        for (int i = 0; i < a.length(); i++) diff |= a.charAt(i) ^ b.charAt(i);
        return diff == 0;
    }
}
