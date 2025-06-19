package ru.itmo.server.commands;

import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.AddRequest;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.AddResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.server.db.TicketRepository;
import ru.itmo.server.managers.CollectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

        try (PreparedStatement statement = TicketRepository.prepareAddStatement(ticket);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                long generatedId = resultSet.getLong(1);
                ticket.setId(generatedId);
                collectionManager.add(ticket);
            }
        } catch (SQLException e) {
            return new AddResponse(false);
        }

        return new AddResponse(true);
    }
}
