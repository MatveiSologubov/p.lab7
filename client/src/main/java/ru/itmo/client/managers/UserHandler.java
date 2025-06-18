package ru.itmo.client.managers;

import ru.itmo.common.models.User;

public class UserHandler {
    private static User currentUser = null;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        UserHandler.currentUser = currentUser;
    }
}

