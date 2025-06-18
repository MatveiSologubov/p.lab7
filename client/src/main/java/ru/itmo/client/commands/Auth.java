package ru.itmo.client.commands;

import ru.itmo.client.builders.UserBuilder;
import ru.itmo.client.managers.ScannerManager;
import ru.itmo.client.managers.UserHandler;
import ru.itmo.client.network.UPDClient;
import ru.itmo.common.exceptions.WrongAmountOfArgumentsException;
import ru.itmo.common.models.User;
import ru.itmo.common.network.requests.AuthRequest;
import ru.itmo.common.network.responses.AuthResponse;

import java.io.IOException;

/**
 * Authenticates user
 */
public class Auth extends NetworkCommand {
    public Auth(UPDClient client) {
        super(client);
    }

    /**
     * execute command
     *
     * @param args arguments for command
     * @throws WrongAmountOfArgumentsException if user provides wrong amount of arguments
     */
    @Override
    public void execute(String[] args) throws WrongAmountOfArgumentsException, IOException {
        if (args.length != 0) throw new WrongAmountOfArgumentsException(0, args.length);

        User user = new UserBuilder(ScannerManager.getScanner()).build();

        AuthResponse response = (AuthResponse) client.sendAndReceive(new AuthRequest(user));

        if (response.isSuccess()) {
            System.out.println("Authentication successful!");
            UserHandler.setCurrentUser(user);
            return;
        }

        System.out.println("Wrong login or password!");
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "Authenticates user";
    }
}
