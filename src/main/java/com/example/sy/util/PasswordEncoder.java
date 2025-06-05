package com.example.sy.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Custom password encoder with SHA-256 hashing and salt
 */
public class PasswordEncoder {
    private static final int SALT_LENGTH = 16;
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String DELIMITER = ":";

    /**
     * Encodes raw password with salt and SHA-256 hashing
     * @param rawPassword The plaintext password
     * @return Encoded password string (salt:hash)
     */
    public static String encode(String rawPassword) {
        try {
            // Generate random salt
            byte[] salt = generateSalt();

            // Combine salt and password
            byte[] saltedPassword = concatenate(salt, rawPassword.getBytes(StandardCharsets.UTF_8));

            // Create hash
            byte[] hash = createHash(saltedPassword);

            // Return salt:hash as Base64 strings
            return Base64.getEncoder().encodeToString(salt) + DELIMITER +
                    Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error encoding password", e);
        }
    }

    /**
     * Verifies raw password against encoded version
     * @param rawPassword Plaintext password to check
     * @param encodedPassword Previously encoded password (salt:hash)
     * @return true if passwords match
     */
    public static boolean verify(String rawPassword, String encodedPassword) {
        try {
            // Split salt and hash
            String[] parts = encodedPassword.split(DELIMITER);
            if (parts.length != 2) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHash = Base64.getDecoder().decode(parts[1]);

            // Recreate hash with same salt
            byte[] saltedPassword = concatenate(salt, rawPassword.getBytes(StandardCharsets.UTF_8));
            byte[] testHash = createHash(saltedPassword);

            // Compare hashes
            return MessageDigest.isEqual(storedHash, testHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error verifying password", e);
        }
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private static byte[] createHash(byte[] input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        return digest.digest(input);
    }

    private static byte[] concatenate(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}