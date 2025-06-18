package ru.itmo.common.network.responses;

public class AuthResponse extends Response {
    private final boolean success;

    public AuthResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
