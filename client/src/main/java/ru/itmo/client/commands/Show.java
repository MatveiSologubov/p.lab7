package ru.itmo.client.commands;

import ru.itmo.client.network.UPDClient;
import ru.itmo.common.exceptions.WrongAmountOfArgumentsException;
import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.ShowRequest;
import ru.itmo.common.network.responses.Response;
import ru.itmo.common.network.responses.ShowResponse;

import java.io.IOException;
import java.util.Comparator;

/**
 * 'Show' command prints information of all the ticket in collection
 */
public class Show extends NetworkCommand {
    public Show(UPDClient client) {
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

        Response receivedResponse = client.sendAndReceive(new ShowRequest());
        ShowResponse response = handleResponse(receivedResponse, ShowResponse.class);
        if (response == null) return;


        if (response.getTickets() == null || response.getTickets().isEmpty()) {
            System.out.println("Collection is empty");
        }

        response.getTickets().stream()
                .sorted(Comparator.comparing((Ticket t) -> t.getCoordinates().getX())
                        .thenComparing(t -> t.getCoordinates().getY()))
                .forEach(System.out::println);
    }

    /**
     * @return Help message
     */
    @Override
    public String getHelp() {
        return "This command will show current collection";
    }
}
