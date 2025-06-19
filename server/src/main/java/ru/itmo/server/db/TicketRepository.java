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
                ticket.setOwner(resultSet.getString("owner_login"));

                result.add(ticket);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reading from db", e);
        }

        return result;
    }

    public static PreparedStatement prepareAddStatement(Ticket ticket) throws SQLException {
        String sql = """
                INSERT INTO tickets(name, coord_x, coord_y, creation_date,
                                    price, comment, refundable, type, person_birthday,
                                    person_height, person_weight, person_passport,
                                    owner_login)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        PreparedStatement statement = Database.getConnection().prepareStatement(sql);
        bindTicket(statement, ticket);
        return statement;
    }

    public static void bindTicket(PreparedStatement statement, Ticket ticket) throws SQLException {
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
            return;
        }

        if (person.getBirthday() != null) {
            statement.setTimestamp(9, Timestamp.valueOf(person.getBirthday()));
        } else {
            statement.setNull(9, Types.TIMESTAMP);
        }

        statement.setInt(10, person.getHeight());
        statement.setFloat(11, person.getWeight());
        statement.setString(12, person.getPassportID());

        statement.setString(13, ticket.getOwner());
    }

    /**
     * Delete all tickets owned by specified user
     *
     * @param login user that issued deletion
     * @return number of tickets deleted
     */
    public static int deleteAllByUser(String login) {
        String sql = "DELETE FROM tickets WHERE owner_login = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error clearing user tickets", e);
        }
    }

    /**
     * Delete Ticket with specified id
     *
     * @param id    ID of the ticket to delete
     * @param login user issuing command
     * @return true if deleted more than zero tickets
     */
    public static boolean deleteById(long id, String login) {
        String sql = "DELETE FROM tickets WHERE id = ? AND owner_login = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.setString(2, login);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting ticket", e);
        }
    }

    /**
     * Update Ticket with specified id if it's owned by specified user
     *
     * @param id     Ticket to update
     * @param ticket Ticket to update to
     * @param login  Owner login
     * @return true if operation is successful. False otherwise
     */
    public static boolean update(long id, Ticket ticket, String login) {
        String sql = """
                UPDATE tickets SET
                  name = ?, coord_x = ?, coord_y = ?, creation_date = ?,
                  price = ?, comment = ?, refundable = ?, type = ?,
                  person_birthday = ?, person_height = ?, person_weight = ?, person_passport = ?
                WHERE id = ? AND owner_login = ?
                """;
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            bindTicket(ps, ticket);

            ps.setLong(13, id);
            ps.setString(14, login);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating ticket", e);
        }
    }
}
