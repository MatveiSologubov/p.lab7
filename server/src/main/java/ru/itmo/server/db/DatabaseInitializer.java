package ru.itmo.server.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class for initializing database
 */
public class DatabaseInitializer {
    private static final Logger logger = LogManager.getLogger(DatabaseInitializer.class);

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
            CREATE TABLE IF NOT EXISTS users (
                    login VARCHAR(32) PRIMARY KEY,
                    password_hash CHAR(32) NOT NULL,
                    salt CHAR(16) NOT NULL
                    )
            """,

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
                    person_passport VARCHAR,
                    owner_login VARCHAR REFERENCES users(login) NOT NULL
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

            logger.info("Database initialized");
        } catch (SQLException e) {
            logger.error("Database initialization failed", e);
        }
    }

    /**
     * Drops database and reinitialize new one
     */
    public static void reinitialize() {
        logger.info("Starting database reinitialization");
        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement()) {

            for (String ddl : DROP_DB_SQL) {
                statement.execute(ddl);
            }

            logger.info("Database dropped");

            init();
        } catch (SQLException e) {
            logger.error("Database dropping failed", e);
        }
    }
}
