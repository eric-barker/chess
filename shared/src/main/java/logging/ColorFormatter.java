package logging;

import java.text.MessageFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ColorFormatter extends Formatter {

    // ANSI color codes
    private static final String RESET = "\u001B[0m";
    private static final String COLOR_FILE = "\u001B[34m"; // Blue
    private static final String COLOR_METHOD = "\u001B[31m"; // Red
    private static final String COLOR_TIMESTAMP = "\u001B[32m"; // Green
    private static final String COLOR_LEVEL = "\u001B[35m"; // Purple
    private static final String COLOR_MESSAGE = "\u001B[37m"; // White

    @Override
    public String format(LogRecord record) {
        // Extract the file (class name) and method name
        String fileName = record.getSourceClassName().substring(record.getSourceClassName().lastIndexOf('.') + 1);
        String methodName = record.getSourceMethodName();

        // Generate a short timestamp (HH:MM:SS)
        String shortTimestamp = String.format("%1$tH:%1$tM:%1$tS", record.getMillis());

        // Safely format the message with parameters
        String resolvedMessage;
        if (record.getParameters() != null && record.getMessage().contains("{")) {
            try {
                resolvedMessage = MessageFormat.format(record.getMessage(), record.getParameters());
            } catch (IllegalArgumentException e) {
                // If formatting fails, fallback to raw message
                resolvedMessage = record.getMessage();
            }
        } else {
            resolvedMessage = record.getMessage();
        }

        // Format the log message
        return String.format(
                "%s[%s]%s%s(%s)%s %s- %s - %s%s[%s] %s%s- %s%s%n",
                COLOR_FILE, fileName, RESET, // File (Blue)
                COLOR_METHOD, methodName, RESET, // Method (Red)
                COLOR_TIMESTAMP, shortTimestamp, RESET, // Timestamp (Green)
                COLOR_LEVEL, record.getLevel(), RESET, // Log Level (Purple)
                COLOR_MESSAGE, resolvedMessage, RESET // Message (White)
        );
    }
}
