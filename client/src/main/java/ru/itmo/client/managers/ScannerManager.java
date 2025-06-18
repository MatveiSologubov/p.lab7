package ru.itmo.client.managers;

import java.util.Scanner;

/**
 * Manages different scanners that are used in program
 */
public class ScannerManager {
    private static Scanner mainScanner;
    private static boolean scriptMode = false;
    private static Scanner fileScanner;

    /**
     * Set scanner for interactive mode
     *
     * @param mainScanner scanner to set
     */
    public static void setMainScanner(Scanner mainScanner) {
        ScannerManager.mainScanner = mainScanner;
    }

    /**
     * Set scanner for script mode
     *
     * @param fileScanner scanner to set
     */
    public static void setScriptScanner(Scanner fileScanner) {
        ScannerManager.fileScanner = fileScanner;
    }

    /**
     * Set script mode
     *
     * @param scriptMode true for script mode; false for interactive mode
     */
    public static void setScriptMode(boolean scriptMode) {
        ScannerManager.scriptMode = scriptMode;
    }

    /**
     * Gets scanner that are currently used
     *
     * @return active scanner
     */
    public static Scanner getScanner() {
        if (scriptMode) {
            return fileScanner;
        }
        return mainScanner;
    }
}
