package ru.itmo.common.network.requests;

import ru.itmo.common.models.User;

import java.io.Serializable;

/**
 * Base class for all client requests
 */
public class Request implements Serializable {
    private final String commandName;
    private User user;

    public Request(String commandName) {
        this.commandName = commandName;
    }

    public String name() {
        return commandName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
