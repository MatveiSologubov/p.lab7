package ru.itmo.server.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Util class for hashing strings with MD5 algorithm
 */
public final class MD5Util {
    private static final SecureRandom random = new SecureRandom();

    /**
     * Hashes given string and returns hash with length of 32
     *
     * @param password String to hash
     * @return hashed password
     */
    public static String hash(String password, String salt) {
        String input = SecurityConfig.getPepper() + password + salt;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(input.getBytes());

            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : digest) {
                stringBuilder.append(String.format("%02x", b & 0xff));
            }

            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates salt with length of 16
     *
     * @return generated salt
     */
    public static String generateSalt() {
        byte[] salt = new byte[8];
        random.nextBytes(salt);

        StringBuilder sb = new StringBuilder();
        for (byte b : salt) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}
