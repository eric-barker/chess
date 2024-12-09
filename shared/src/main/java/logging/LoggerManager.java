package logging;

import logging.ColorFormatter;

import java.awt.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerManager {
    private static final Logger GLOBAL_LOGGER = Logger.getLogger(LoggerManager.class.getName());

    static {
        configureLogger();
    }

    private static void configureLogger() {
        try {
            // Get logging level from environment variable or default to INFO
            String logLevel = System.getenv("LOG_LEVEL");
            Level level = logLevel != null ? Level.parse(logLevel) : Level.INFO;

            // Set global logging level
            GLOBAL_LOGGER.setLevel(level);

            // Configure console handler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.OFF);
            consoleHandler.setFormatter(new ColorFormatter());
            GLOBAL_LOGGER.addHandler(consoleHandler);

            GLOBAL_LOGGER.setUseParentHandlers(false);
        } catch (Exception e) {
            System.err.println("Failed to configure logger: " + e.getMessage());
        }
    }

    public static Logger getLogger(String name) {
        Logger logger = Logger.getLogger(name);
        logger.setParent(GLOBAL_LOGGER);
        return logger;
    }

    public static void setLogLevel(Level level) {
        GLOBAL_LOGGER.setLevel(level);
    }
}
