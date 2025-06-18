package ru.itmo.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class for connecting to database
 */
public class Database {
    private static final String URL = "jdbc:postgresql://localhost:5432/studs";
    private static final String USER = System.getenv("PG_USER");
    private static final String PASS = System.getenv("PG_PASS");

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC Driver not found", e);
        }
    }

    /**
     * Connects to studs database
     *
     * @return connection
     * @throws SQLException if encounters one
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
