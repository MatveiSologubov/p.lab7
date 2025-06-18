package ru.itmo.common.network.requests;

import ru.itmo.common.models.User;

public class AuthRequest extends Request {
    private final User user;

    public AuthRequest(User user) {
        super("auth");
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
