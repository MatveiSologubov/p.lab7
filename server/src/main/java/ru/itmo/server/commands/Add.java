package ru.itmo.server.commands;

import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.AddRequest;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.AddResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.server.db.TicketRepository;
import ru.itmo.server.managers.CollectionManager;

/**
 * 'Add' command adds Ticket to collection if it is valid
 */
public class Add extends Command {
    private final CollectionManager collectionManager;

    public Add(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * execute command
     *
     * @param request request from client
     */
    @Override
    public Response execute(Request request) {
        AddRequest addRequest = (AddRequest) request;
        Ticket ticket = new Ticket(addRequest.getTicket());

        Long generatedId = TicketRepository.insert(ticket);
        if (generatedId == null) return new AddResponse(false);

        ticket.setId(generatedId);
        collectionManager.add(ticket);
        return new AddResponse(true);
    }
}
