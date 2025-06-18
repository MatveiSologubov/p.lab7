package ru.itmo.server.db;

import ru.itmo.server.util.MD5Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {
    public static boolean register(String login, String plainPassword) throws SQLException {
        String salt = MD5Util.generateSalt();
        String hashedPassword = MD5Util.hash(plainPassword, salt);
        String sql = "INSERT INTO users (login, password_hash, salt) VALUES (?, ?, ?)";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, login);
            statement.setString(2, hashedPassword);
            statement.setString(3, salt);

            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Authenticates user based on login and password
     *
     * @param login         user login
     * @param plainPassword user password in plain text
     * @return true if user authenticated. False otherwise
     * @throws SQLException if encounters one
     */
    public static boolean authenticate(String login, String plainPassword) throws SQLException {
        String sql = "SELECT password_hash, salt FROM users WHERE login = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) return false;

            String expectedHash = resultSet.getString("password_hash");
            String salt = resultSet.getString("salt");
            String calculatedHash = MD5Util.hash(plainPassword, salt);

            return calculatedHash.equals(expectedHash);
        }
    }
}
