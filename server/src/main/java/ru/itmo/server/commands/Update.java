package ru.itmo.server.commands;

import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.requests.UpdateRequest;
import ru.itmo.common.network.responses.Response;
import ru.itmo.common.network.responses.UpdateResponse;
import ru.itmo.server.db.TicketRepository;
import ru.itmo.server.managers.CollectionManager;

/**
 * 'Update' command updates Ticket with specified id with data from new Ticket
 */
public class Update extends Command {
    private final CollectionManager collectionManager;

    public Update(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * execute command
     *
     * @param request request from client
     */
    @Override
    public Response execute(Request request) {
        UpdateRequest updateRequest = (UpdateRequest) request;
        if (collectionManager.getCollection().isEmpty()) return new UpdateResponse(false, "Collection is empty");

        long idToUpdate = updateRequest.getId();
        boolean success = TicketRepository.update(idToUpdate, updateRequest.getTicket(), request.getUser().getLogin());

        if (success) {
            for (Ticket ticket : collectionManager.getCollection()) {
                if (ticket.getId() == idToUpdate) {
                    ticket.update(updateRequest.getTicket());
                    break;
                }
            }
            return new UpdateResponse(true, "Ticket with id " + idToUpdate + " was successfully updated");
        }

        return new UpdateResponse(false, "Ticket with id " + idToUpdate + " not found. Or you are not the owner");
    }
}
