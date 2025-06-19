package ru.itmo.client.commands;

import ru.itmo.client.network.UPDClient;
import ru.itmo.common.exceptions.WrongAmountOfArgumentsException;
import ru.itmo.common.network.requests.ClearRequest;
import ru.itmo.common.network.responses.ClearResponse;
import ru.itmo.common.network.responses.Response;

import java.io.IOException;

/**
 * 'Clear' command empties the collection
 */
public class Clear extends NetworkCommand {
    public Clear(UPDClient client) {
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

        Response receivedResponse = client.sendAndReceive(new ClearRequest());
        ClearResponse response = handleResponse(receivedResponse, ClearResponse.class);
        if (response == null) return;

        System.out.println("Tickets removed from collection: " + response.getLinesRemoved());
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "Clears the collection";
    }
}
