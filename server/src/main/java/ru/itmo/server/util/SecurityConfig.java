package ru.itmo.server.util;

public final class SecurityConfig {
    private static final String PEPPER = "pepper";

    public static String getPepper() {
        return PEPPER;
    }
}
