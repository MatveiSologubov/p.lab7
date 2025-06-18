package ru.itmo.common.network.responses;

public class RegisterResponse extends Response {
    private final boolean success;

    public RegisterResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
