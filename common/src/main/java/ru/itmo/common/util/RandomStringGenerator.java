package ru.itmo.common.util;

import java.util.Random;

/**
 * Class for generating random strings
 */
public class RandomStringGenerator {
    /**
     * Generates random string with specified length
     *
     * @param length length of the string
     * @return random string generated
     */
    public static String generate(int length) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            char randomChar = (char) ('a' + random.nextInt(26));
            builder.append(randomChar);
        }

        return builder.toString();
    }
}
