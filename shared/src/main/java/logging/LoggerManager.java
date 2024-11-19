package logging;

import logging.ColorFormatter;

import java.awt.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerManager {
    private static final Logger globalLogger = Logger.getLogger(LoggerManager.class.getName());

    static {
        configureLogger();
    }

    private static void configureLogger() {
        try {
            // Get logging level from environment variable or default to INFO
            String logLevel = System.getenv("LOG_LEVEL");
            Level level = logLevel != null ? Level.parse(logLevel) : Level.INFO;

            // Set global logging level
            globalLogger.setLevel(level);

            // Configure console handler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            consoleHandler.setFormatter(new ColorFormatter());
            globalLogger.addHandler(consoleHandler);

            globalLogger.setUseParentHandlers(false);
        } catch (Exception e) {
            System.err.println("Failed to configure logger: " + e.getMessage());
        }
    }

    public static Logger getLogger(String name) {
        Logger logger = Logger.getLogger(name);
        logger.setParent(globalLogger);
        return logger;
    }

    public static void setLogLevel(Level level) {
        globalLogger.setLevel(level);
    }
}
