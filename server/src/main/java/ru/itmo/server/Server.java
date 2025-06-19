package ru.itmo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itmo.common.models.User;
import ru.itmo.common.network.requests.AuthRequest;
import ru.itmo.common.network.requests.RegisterRequest;
import ru.itmo.common.network.requests.Request;
import ru.itmo.common.network.responses.ErrorResponse;
import ru.itmo.common.network.responses.NullResponse;
import ru.itmo.common.network.responses.Response;
import ru.itmo.common.util.Config;
import ru.itmo.common.util.Serializer;
import ru.itmo.server.commands.*;
import ru.itmo.server.db.DatabaseInitializer;
import ru.itmo.server.db.TicketRepository;
import ru.itmo.server.db.UserRepository;
import ru.itmo.server.managers.CollectionManager;
import ru.itmo.server.managers.CommandRegistry;
import ru.itmo.server.managers.ConsoleManager;
import ru.itmo.server.managers.NetworkManager;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

/**
 * Server application entry point.
 * Initializes managers, handles shutdown hooks, and processes client requests.
 */
public final class Server {
    private final static Logger logger = LogManager.getLogger(Server.class);
    private final CommandRegistry commandRegistry;
    private final CollectionManager collectionManager = new CollectionManager();
    private final NetworkManager networkManager;

    private ForkJoinPool requestReadingPool;
    private ForkJoinPool requestProcessingPool;
    private ExecutorService responseSendingPool;

    private volatile boolean running = true;

    private Server() throws IOException {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> logger.info("Shutdown hook triggered")));

        logger.info("Initializing server");

        DatabaseInitializer.init();

        collectionManager.setCollection(TicketRepository.getAllTickets());

        networkManager = new NetworkManager(Config.defaultConfig());

        ConsoleManager consoleManager = new ConsoleManager(this::stop, collectionManager);
        new Thread(consoleManager).start();

        commandRegistry = new CommandRegistry() {{
            registerCommand("auth", new Auth());
            registerCommand("register", new Register());

            registerCommand("info", new Info(collectionManager));
            registerCommand("show", new Show(collectionManager));
            registerCommand("clear", new Clear(collectionManager));
            registerCommand("add", new Add(collectionManager));
            registerCommand("update", new Update(collectionManager));
            registerCommand("remove_by_id", new RemoveById(collectionManager));
            registerCommand("add_if_max", new AddIfMax(collectionManager));
            registerCommand("add_if_min", new AddIfMin(collectionManager));
            registerCommand("remove_greater", new RemoveGreater(collectionManager));
            registerCommand("min_by_creation_date", new MinByCreationDate(collectionManager));
            registerCommand("filter_less_than_type", new FilterLessThanType(collectionManager));
            registerCommand("filter_greater_than_price", new FilterGreaterThanPrice(collectionManager));
        }};
    }

    public static void main(String[] args) {
        try {
            new Server().start();
        } catch (IOException e) {
            logger.error("Server initialization failed", e);
            System.exit(1);
        }
    }

    private void stop() {
        running = false;
    }

    public void start() {
        logger.info("Starting server main loop");

        requestReadingPool = new ForkJoinPool();
        requestProcessingPool = new ForkJoinPool();
        responseSendingPool = Executors.newFixedThreadPool(4);

        try {
            while (running) {
                NetworkManager.Received rec = networkManager.receive(100);
                if (rec == null) {
                    if (!running) break;
                    continue;
                }

                requestReadingPool.execute(() -> handleRequestAsync(rec));
            }
        } catch (Exception e) {
            logger.error("Critical server error", e);
        } finally {
            shutdown();
        }
    }

    private void handleRequestAsync(NetworkManager.Received rec) {
        ByteBuffer buffer = rec.buffer;
        SocketAddress clientAddress = rec.clientAddress;

        try {
            byte[] data = new byte[buffer.limit()];
            buffer.get(data);

            Request request = (Request) Serializer.deserialize(data);

            requestProcessingPool.execute(() -> {
                Response response = processRequest(request);

                responseSendingPool.execute(() -> networkManager.sendResponse(response, clientAddress));
            });
        } catch (Exception e) {
            logger.error("Request processing error", e);

            responseSendingPool.execute(() -> networkManager.sendResponse(
                    new ErrorResponse("Internal server error"),
                    rec.clientAddress
            ));
        }
    }

    private Response processRequest(Request request) {
        logger.info("Processing command '{}'", request.name());

        if (!checkAuthorization(request)) {
            logger.info("Client don't have authorization.");
            return new ErrorResponse("Authentication required to issue this command");
        }

        Command command = commandRegistry.getCommand(request.name());
        if (command == null) {
            return new NullResponse();
        }

        try {
            return command.execute(request);
        } catch (Exception e) {
            logger.error("Error executing command", e);
            return new ErrorResponse("Command execution error");
        }
    }

    private boolean checkAuthorization(Request request) {
        if (request instanceof AuthRequest || request instanceof RegisterRequest) return true;
        User user = request.getUser();
        if (user == null) return false;
        try {
            return UserRepository.authenticate(user.getLogin(), user.getPassword());
        } catch (SQLException e) {
            logger.error("Failed to authorize user ({})", user, e);
            return false;
        }
    }

    private void shutdown() {
        logger.info("Starting server shutdown");
        if (requestReadingPool != null) requestReadingPool.shutdown();
        if (requestProcessingPool != null) requestProcessingPool.shutdown();
        if (responseSendingPool != null) responseSendingPool.shutdown();
        if (networkManager != null) networkManager.close();
        logger.info("Server shutdown completed");
    }
}
