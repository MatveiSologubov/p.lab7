package ru.itmo.client.commands;

import ru.itmo.client.builders.TicketBuilder;
import ru.itmo.client.managers.ScannerManager;
import ru.itmo.client.network.UPDClient;
import ru.itmo.common.exceptions.WrongAmountOfArgumentsException;
import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.UpdateRequest;
import ru.itmo.common.network.responses.Response;
import ru.itmo.common.network.responses.UpdateResponse;

import java.io.IOException;

/**
 * 'Update' command builds Ticket and the updates Ticket with specified id with data from new Ticket
 */
public class Update extends NetworkCommand {
    public Update(UPDClient client) {
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
        if (args.length != 1) throw new WrongAmountOfArgumentsException(1, args.length);

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Error: ID must be an integer.");
            return;
        }

        TicketBuilder builder = new TicketBuilder(ScannerManager.getScanner());
        Ticket ticket = builder.build();

        Response receivedResponse = client.sendAndReceive(new UpdateRequest(id, ticket));
        UpdateResponse response = handleResponse(receivedResponse, UpdateResponse.class);
        if (response == null) return;

        if (response.isSuccess()) {
            System.out.println("Ticket with id " + id + " has been updated");
            return;
        }

        System.out.println(response.getMessage());
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "Changes element with specified id. Usage: update id {element}";
    }
}
