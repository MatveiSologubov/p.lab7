package ru.itmo.server.commands;

import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.AddIfMinRequest;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.AddIfMinResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.server.db.TicketRepository;
import ru.itmo.server.managers.CollectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * 'Add If Min' command adds Ticket to collection if its valid and has minimum price
 */
public class AddIfMin extends Command {
    private final CollectionManager collectionManager;

    public AddIfMin(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * execute command
     *
     * @param request request from client
     */
    @Override
    public Response execute(Request request) {
        AddIfMinRequest addRequest = (AddIfMinRequest) request;
        Ticket ticket = new Ticket(addRequest.getTicket());

        Optional<Ticket> currentMin = collectionManager.getCollection()
                .stream().min(Ticket::compareTo);

        if (currentMin.isPresent() && ticket.compareTo(currentMin.get()) >= 0) {
            String message = "Ticket not added to collection. Current min price is " + currentMin.get().getPrice();
            return new AddIfMinResponse(false, message);
        }

        try (PreparedStatement statement = TicketRepository.prepareAddStatement(ticket);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                long generatedId = resultSet.getLong(1);
                ticket.setId(generatedId);
                collectionManager.add(ticket);
            }
        } catch (SQLException e) {
            return new AddIfMinResponse(false, e.getMessage());
        }

        return new AddIfMinResponse(true, null);
    }
}
