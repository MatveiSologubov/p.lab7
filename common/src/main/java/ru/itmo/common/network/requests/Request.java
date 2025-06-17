package ru.itmo.common.network.requests;

import java.io.Serializable;

/**
 * Base class for all client requests
 */
public class Request implements Serializable {
    private final String commandName;

    public Request(String commandName) {
        this.commandName = commandName;
    }

    public String name() {
        return commandName;
    }
}
