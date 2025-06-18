package ru.itmo.server.managers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

/**
 * Manages server console input and command execution.
 * Provides commands for server administration.
 */
public class ConsoleManager implements Runnable {
    private static final Logger logger = LogManager.getLogger(ConsoleManager.class);

    private final Runnable serverShutdownHook;
    private volatile boolean running;
    private volatile boolean commandMode = false;

    public ConsoleManager(Runnable serverShutdownHook) {
        this.serverShutdownHook = serverShutdownHook;
        this.running = true;
    }

    /**
     * Entry point to Console Manager which contains its main loop
     */
    @Override
    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
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
    }

    /**
     * Enters command mode
     */
    private void enterCommandMode() {
        LogModeManager.disableConsoleLogging();
        commandMode = true;
        System.out.println("[Command Mode]");
        System.out.println("  watch - Enable live logging");
        System.out.println("  shut  - Shutdown server");
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
                running = false;
                serverShutdownHook.run();
            }
            case "watch" -> {
                LogModeManager.enableConsoleLogging();
                System.out.println("Live logging enabled");
                commandMode = false;
            }
            default -> System.out.println("Unknown command");
        }
    }
}
