package ru.itmo.client.commands;

import ru.itmo.client.builders.TicketBuilder;
import ru.itmo.client.managers.ScannerManager;
import ru.itmo.client.network.UPDClient;
import ru.itmo.common.exceptions.WrongAmountOfArgumentsException;
import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.RemoveGreaterRequest;
import ru.itmo.common.network.responses.RemoveGreaterResponse;
import ru.itmo.common.network.responses.Response;

import java.io.IOException;

/**
 * 'Remove Greater' command builds Ticket and then removes all Tickets with bigger price than built Ticket
 */
public class RemoveGreater extends NetworkCommand {
    public RemoveGreater(UPDClient client) {
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

        TicketBuilder builder = new TicketBuilder(ScannerManager.getScanner());
        Ticket ticket = builder.build();

        Response receivedResponse = client.sendAndReceive(new RemoveGreaterRequest(ticket));
        RemoveGreaterResponse response = handleResponse(receivedResponse, RemoveGreaterResponse.class);
        if (response == null) return;

        if (!response.isSuccess()) {
            System.out.println("Something went wrong");
            return;
        }

        System.out.println(response.getMessage());
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "Removes all tickets bigger than specified element";
    }
}
