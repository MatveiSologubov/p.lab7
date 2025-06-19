package ru.itmo.common.network.responses;

public class ClearResponse extends Response {
    private final int linesRemoved;

    public ClearResponse(int linesRemoved) {
        this.linesRemoved = linesRemoved;
    }

    public int getLinesRemoved() {
        return linesRemoved;
    }
}
