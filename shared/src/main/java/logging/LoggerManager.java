package utils;

import java.util.logging.*;

public class LoggerManager {
    private static final Logger globalLogger = Logger.getLogger(LoggerManager.class.getName());

    static {
        configureLogger();
    }

    private static void configureLogger() {
        try {
            // Set global logging level
            globalLogger.setLevel(Level.INFO);

            // Create and configure console handler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL); // Log all levels to console
            consoleHandler.setFormatter(new SimpleFormatter()); // Use simple formatting
            globalLogger.addHandler(consoleHandler);

            // Prevent logging duplication by removing default handlers
            globalLogger.setUseParentHandlers(false);
        } catch (Exception e) {
            System.err.println("Failed to configure logger: " + e.getMessage());
        }
    }

    public static Logger getLogger(String name) {
        Logger logger = Logger.getLogger(name);
        logger.setParent(globalLogger); // Use global logger as parent
        return logger;
    }

    public static void setLogLevel(Level level) {
        globalLogger.setLevel(level);
    }
}
