package ru.itmo.common.network.responses;

public class ClearResponse extends Response {
    private final boolean success;
    private final int linesRemoved;

    public ClearResponse(boolean success, int linesRemoved) {
        this.success = success;
        this.linesRemoved = linesRemoved;
    }

    public int getLinesRemoved() {
        return linesRemoved;
    }

    public boolean isSuccess() {
        return success;
    }
}
