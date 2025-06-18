package ru.itmo.server.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class for initializing database
 */
public class DatabaseInitializer {
    private static final String[] DROP_DB_SQL = {
            """ 
            DROP TABLE IF EXISTS tickets, users CASCADE
            """,

            """
            DROP SEQUENCE IF EXISTS ticket_id_seq CASCADE
            """
    };

    private static final String[] INIT_DB_SQL = {
            "CREATE SEQUENCE IF NOT EXISTS ticket_id_seq START 1",

            """
            CREATE TABLE IF NOT EXISTS tickets (
                    id BIGINT PRIMARY KEY DEFAULT nextval('ticket_id_seq'),
                    name VARCHAR NOT NULL,
                    coord_x INTEGER NOT NULL,
                    coord_y REAL NOT NULL,
                    creation_date TIMESTAMP WITH TIME ZONE NOT NULL,
                    price REAL,
                    comment VARCHAR(855),
                    refundable BOOLEAN NOT NULL,
                    type VARCHAR,
                    person_birthday TIMESTAMP,
                    person_height INTEGER NOT NULL,
                    person_weight REAL NOT NULL,
                    person_passport VARCHAR
                    )
            """,

            """
            CREATE TABLE IF NOT EXISTS users (
                    login VARCHAR PRIMARY KEY,
                    password_hash CHAR(32) NOT NULL
                    )
            """
    };

    /**
     * Initializes database. Does nothing if already initialized
     */
    public static void init() {
        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement()) {

            for (String ddl : INIT_DB_SQL) {
                statement.execute(ddl);
            }

            System.out.println("DB initialized");
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing db", e);
        }
    }

    /**
     * Drops database and reinitialize new one
     */
    public static void reinitialize() {
        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement()) {

            for (String ddl : DROP_DB_SQL) {
                statement.execute(ddl);
            }

            System.out.println("DB dropped");

            init();
        } catch (SQLException e) {
            throw new RuntimeException("Error dropping database", e);
        }
    }
}
