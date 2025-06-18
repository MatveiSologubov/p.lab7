package ru.itmo.client.builders;

import ru.itmo.common.models.User;

import java.util.Scanner;

public class UserBuilder implements Builder<User> {
    private final Scanner scanner;

    public UserBuilder(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Asks login and password. Then builds user
     *
     * @return user that was built
     */
    @Override
    public User build() {
        return new User(askLogin(), askPassword());
    }

    private String askLogin() {
        while (true) {
            System.out.print("Login: ");
            String login = scanner.nextLine().trim();
            if (!login.isEmpty() && login.length() <= 32) {
                return login;
            }
            System.out.println("ERROR: Login must not be empty or more than 32 characters!");
        }

    }

    private String askPassword() {
        System.out.print("Password: ");
        return scanner.nextLine().trim();
    }
}
