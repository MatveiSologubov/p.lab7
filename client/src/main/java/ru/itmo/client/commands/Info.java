package ru.itmo.client.commands;

import ru.itmo.client.network.UPDClient;
import ru.itmo.common.exceptions.WrongAmountOfArgumentsException;
import ru.itmo.common.network.requests.InfoRequest;
import ru.itmo.common.network.responses.InfoResponse;
import ru.itmo.common.network.responses.Response;

import java.io.IOException;

/**
 * 'Info' command print information about current collection
 */
public class Info extends NetworkCommand {
    public Info(UPDClient client) {
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

        Response receivedResponse = client.sendAndReceive(new InfoRequest());
        InfoResponse response = handleResponse(receivedResponse, InfoResponse.class);
        if (response == null) return;

        System.out.println("Collection Info:");
        System.out.println(" Type: " + response.getCollectionType());
        System.out.println(" Collection size: " + response.getCollectionSize());
        System.out.println(" Initialization time: " + response.getInitTime());
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "This command print information about current collection.";
    }
}
