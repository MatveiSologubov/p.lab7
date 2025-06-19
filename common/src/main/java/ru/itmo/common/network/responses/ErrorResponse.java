package ru.itmo.common.network.responses;

public class ErrorResponse extends Response {
    private final String err_message;

    public ErrorResponse(String err_message) {
        this.err_message = err_message;
    }

    public String message() {
        return err_message;
    }
}
