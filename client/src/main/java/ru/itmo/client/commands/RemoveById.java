package ru.itmo.client.commands;

import ru.itmo.client.network.UPDClient;
import ru.itmo.common.exceptions.WrongAmountOfArgumentsException;
import ru.itmo.common.network.requests.RemoveByIdRequest;
import ru.itmo.common.network.responses.RemoveByIdResponse;
import ru.itmo.common.network.responses.Response;

import java.io.IOException;

/**
 * 'Remove By ID' command removes Ticket with specified id from collection
 */
public class RemoveById extends NetworkCommand {
    public RemoveById(UPDClient client) {
        super(client);
    }

    /**
     * execute command
     *
     * @param args id of ticket to be removed
     */
    @Override
    public void execute(String[] args) throws WrongAmountOfArgumentsException, IOException {
        if (args.length != 1) throw new WrongAmountOfArgumentsException(1, args.length);
        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Error: ID must be an integer.");
            return;
        }

        Response receivedResponse = client.sendAndReceive(new RemoveByIdRequest(id));
        RemoveByIdResponse response = handleResponse(receivedResponse, RemoveByIdResponse.class);
        if (response == null) return;

        if (response.isSuccess()) {
            System.out.println("Ticket with id " + id + " removed");
            return;
        }

        System.out.println(response.getMessage());
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "Removes element from collection with specified id";
    }
}
