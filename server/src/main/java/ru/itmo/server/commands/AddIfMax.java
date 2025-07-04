package ru.itmo.server.commands;

import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.AddIfMaxRequest;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.AddIfMaxResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.server.db.TicketRepository;
import ru.itmo.server.managers.CollectionManager;

import java.util.Optional;

/**
 * 'Add If Max' command adds Ticket to collection if its valid and has maximum price
 */
public class AddIfMax extends Command {
    private final CollectionManager collectionManager;

    public AddIfMax(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * execute command
     *
     * @param request request from client
     */
    @Override
    public Response execute(Request request) {
        AddIfMaxRequest addRequest = (AddIfMaxRequest) request;
        Ticket ticket = new Ticket(addRequest.getTicket());

        Optional<Ticket> currentMax = collectionManager.getCollection()
                .stream().max(Ticket::compareTo);

        if (currentMax.isPresent() && ticket.compareTo(currentMax.get()) <= 0) {
            String message = "Ticket not added to collection. Current max price is " + currentMax.get().getPrice();
            return new AddIfMaxResponse(false, message);
        }

        Long generatedId = TicketRepository.insert(ticket);
        if (generatedId == null) return new AddIfMaxResponse(false, "Error adding ticket");

        ticket.setId(generatedId);
        collectionManager.add(ticket);
        return new AddIfMaxResponse(true, null);
    }
}
