package ru.itmo.server.commands;

import ru.itmo.common.network.requests.RemoveByIdRequest;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.RemoveByIdResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.server.db.TicketRepository;
import ru.itmo.server.managers.CollectionManager;

/**
 * 'Remove By ID' command removes Ticket with specified id from collection
 */
public class RemoveById extends Command {
    private final CollectionManager collectionManager;

    public RemoveById(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * execute command
     *
     * @param request request from client
     */
    @Override
    public Response execute(Request request) {
        if (collectionManager.getCollection().isEmpty()) {
            return new RemoveByIdResponse(false, "Collection is empty");
        }
        RemoveByIdRequest removeByIdRequest = (RemoveByIdRequest) request;

        long idToRemove = removeByIdRequest.getId();
        boolean success = TicketRepository.deleteById(idToRemove, request.getUser().getLogin());
        if (success) {
            collectionManager.getCollection().removeIf(t -> t.getId() == idToRemove);
            return new RemoveByIdResponse(true, null);
        }

        String message = "Ticket with id " + idToRemove + " was not found. Or you are not the owner of this ticket";
        return new RemoveByIdResponse(false, message);
    }
}
