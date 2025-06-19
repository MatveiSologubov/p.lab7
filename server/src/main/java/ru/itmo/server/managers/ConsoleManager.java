package ru.itmo.server.managers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.itmo.common.util.RandomStringGenerator;
import ru.itmo.server.db.DatabaseInitializer;
import ru.itmo.server.db.TicketRepository;

import java.util.Scanner;

/**
 * Manages server console input and command execution.
 * Provides commands for server administration.
 */
public class ConsoleManager implements Runnable {
    private static final Logger logger = LogManager.getLogger(ConsoleManager.class);

    private final Runnable serverShutdownHook;
    private final Scanner scanner = new Scanner(System.in);
    private final CollectionManager collectionManager;
    private volatile boolean running;
    private volatile boolean commandMode = false;

    public ConsoleManager(Runnable serverShutdownHook, CollectionManager collectionManager) {
        this.serverShutdownHook = serverShutdownHook;
        this.collectionManager = collectionManager;
        this.running = true;
    }

    /**
     * Entry point to Console Manager which contains its main loop
     */
    @Override
    public void run() {
        System.out.println("Press Enter to enter command mode...");
        while (running) {
            scanner.nextLine();
            enterCommandMode();
            while (commandMode && running) {
                System.out.print("server> ");
                String cmd = scanner.nextLine().trim();
                handle(cmd);
            }
        }
    }

    /**
     * Enters command mode
     */
    private void enterCommandMode() {
        LogModeManager.disableConsoleLogging();
        commandMode = true;
        System.out.println("[Command Mode]");
        System.out.println("  watch   - Enable live logging");
        System.out.println("  shut    - Shutdown server");
        System.out.println("  reinit  - Reinitialize database");
    }

    /**
     * Handles command based on string typed
     *
     * @param command string to handle
     */
    private void handle(String command) {
        switch (command) {
            case "shut" -> {
                logger.info("Shutting down server due to command");
                LogModeManager.enableConsoleLogging();
                running = false;
                serverShutdownHook.run();
            }
            case "watch" -> {
                LogModeManager.enableConsoleLogging();
                System.out.println("Live logging enabled");
                commandMode = false;
            }
            case "reinit" -> {
                System.out.println("Wow. Are you sure you want to reinitialize database?");
                System.out.println("It will delete all user data. We are not gitlab!");
                String verificationString = RandomStringGenerator.generate(5);
                System.out.println("To verify type this string: " + verificationString);
                String input = scanner.nextLine().trim();
                if (input.equals(verificationString)) {
                    System.out.println("Dropping DB...");
                    LogModeManager.enableConsoleLogging();
                    DatabaseInitializer.reinitialize();
                    LogModeManager.disableConsoleLogging();
                    collectionManager.setCollection(TicketRepository.getAllTickets());
                    return;
                }
                System.out.println("Verification failed. Good luck next time");
            }
            default -> System.out.println("Unknown command");
        }
    }
}
