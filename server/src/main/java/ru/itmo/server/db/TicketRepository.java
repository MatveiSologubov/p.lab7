package ru.itmo.server.db;

import ru.itmo.common.models.Coordinates;
import ru.itmo.common.models.Person;
import ru.itmo.common.models.Ticket;
import ru.itmo.common.models.TicketType;

import java.sql.*;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

/**
 * Class for managing tickets in database
 */
public class TicketRepository {
    /**
     * Pulls all the tickets from database
     *
     * @return set of tickets from database
     */
    public static Set<Ticket> getAllTickets() {
        String sql = "SELECT * FROM tickets";
        Set<Ticket> result = new HashSet<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(resultSet.getLong("id"));
                ticket.setName(resultSet.getString("name"));
                ticket.setCoordinates(new Coordinates(resultSet.getInt("coord_x"), resultSet.getFloat("coord_y")));
                ticket.setCreationDate(resultSet.getTimestamp("creation_date").toInstant().atZone(ZoneId.systemDefault()));
                ticket.setPrice(resultSet.getObject("price", Float.class));
                ticket.setComment(resultSet.getString("comment"));
                ticket.setRefundable(resultSet.getBoolean("refundable"));
                String type = resultSet.getString("type");
                ticket.setType(type == null ? null : TicketType.valueOf(type));

                Person person = new Person();
                Timestamp bd = resultSet.getTimestamp("person_birthday");
                person.setBirthday(bd == null ? null : bd.toLocalDateTime());
                person.setHeight(resultSet.getInt("person_height"));
                person.setWeight(resultSet.getFloat("person_weight"));
                person.setPassportID(resultSet.getString("person_passport"));
                ticket.setPerson(person);

                result.add(ticket);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reading from db", e);
        }

        return result;
    }
}
