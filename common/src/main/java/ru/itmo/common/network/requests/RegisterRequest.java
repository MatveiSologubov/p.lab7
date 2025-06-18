package ru.itmo.common.network.requests;

import ru.itmo.common.models.User;

public class RegisterRequest extends Request {
    private final User user;

    public RegisterRequest(User user) {
        super("register");
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}

