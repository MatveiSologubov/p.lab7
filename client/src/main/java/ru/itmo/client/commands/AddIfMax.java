package ru.itmo.client.commands;

import ru.itmo.client.builders.TicketBuilder;
import ru.itmo.client.managers.ScannerManager;
import ru.itmo.client.network.UPDClient;
import ru.itmo.common.exceptions.WrongAmountOfArgumentsException;
import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.AddIfMaxRequest;
import ru.itmo.common.network.responses.AddIfMaxResponse;
import ru.itmo.common.network.responses.Response;

import java.io.IOException;

/**
 * 'Add If Max' command builds and adds Ticket to collection if its valid and has maximum price
 */
public class AddIfMax extends NetworkCommand {
    public AddIfMax(UPDClient updClient) {
        super(updClient);
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

        Response receivedResponse = client.sendAndReceive(new AddIfMaxRequest(ticket));
        AddIfMaxResponse response = handleResponse(receivedResponse, AddIfMaxResponse.class);
        if (response == null) return;

        if (response.isSuccess()) {
            System.out.println("Ticket is added to collection");
            return;
        }

        System.out.println(response.getMessage());
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "Adds element to collection; Usage add {element}";
    }
}
