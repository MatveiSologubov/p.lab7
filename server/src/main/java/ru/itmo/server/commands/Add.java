package ru.itmo.server.commands;

import ru.itmo.common.models.Person;
import ru.itmo.common.models.Ticket;
import ru.itmo.common.network.requests.AddRequest;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.AddResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.server.db.Database;
import ru.itmo.server.managers.CollectionManager;

import java.sql.*;

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

        String sql = """
                INSERT INTO tickets(name, coord_x, coord_y, creation_date,
                                    price, comment, refundable, type, person_birthday,
                                    person_height, person_weight, person_passport)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            PreparedStatement preparedStatement = prepareStatement(statement, ticket);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    long generatedId = resultSet.getLong(1);
                    ticket.setId(generatedId);
                    collectionManager.add(ticket);
                }
            }
        } catch (SQLException e) {
            return new AddResponse(false);
        }

        return new AddResponse(true);
    }

    private PreparedStatement prepareStatement(PreparedStatement statement, Ticket ticket) throws SQLException {
        statement.setString(1, ticket.getName());
        statement.setInt(2, ticket.getCoordinates().getX());
        statement.setFloat(3, ticket.getCoordinates().getY());
        statement.setTimestamp(4, Timestamp.from(ticket.getCreationDate().toInstant()));

        if (ticket.getPrice() != null) {
            statement.setFloat(5, ticket.getPrice());
        } else {
            statement.setNull(5, Types.REAL);
        }

        statement.setString(6, ticket.getComment());
        statement.setBoolean(7, ticket.getRefundable());

        if (ticket.getType() != null) {
            statement.setString(8, ticket.getType().name());
        } else {
            statement.setNull(8, Types.VARCHAR);
        }

        Person person = ticket.getPerson();

        if (person == null) {
            statement.setNull(9, Types.TIMESTAMP);
            statement.setNull(10, Types.INTEGER);
            statement.setNull(11, Types.REAL);
            statement.setNull(12, Types.VARCHAR);
            return statement;
        }

        if (person.getBirthday() != null) {
            statement.setTimestamp(9, Timestamp.valueOf(person.getBirthday()));
        } else {
            statement.setNull(9, Types.TIMESTAMP);
        }

        statement.setInt(10, person.getHeight());
        statement.setFloat(11, person.getWeight());
        statement.setString(12, person.getPassportID());

        return statement;
    }

}
