package ru.itmo.client.commands;

import ru.itmo.client.builders.UserBuilder;
import ru.itmo.client.managers.ScannerManager;
import ru.itmo.client.managers.UserHandler;
import ru.itmo.client.network.UPDClient;
import ru.itmo.common.exceptions.WrongAmountOfArgumentsException;
import ru.itmo.common.models.User;
import ru.itmo.common.network.requests.RegisterRequest;
import ru.itmo.common.network.responses.RegisterResponse;

import java.io.IOException;

/**
 * Registers user on server
 */
public class Register extends NetworkCommand {
    public Register(UPDClient client) {
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

        RegisterResponse response = (RegisterResponse) client.sendAndReceive(new RegisterRequest(user));

        if (response.isSuccess()) {
            System.out.println("Registration successful!");
            UserHandler.setCurrentUser(user);
            return;
        }

        System.out.println("Registration failed");
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "Registers user on server";
    }
}
