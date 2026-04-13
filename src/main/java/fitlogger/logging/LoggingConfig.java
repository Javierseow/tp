package fitlogger.logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Configures application logging so diagnostic logs do not appear in the CLI output.
 */
public class LoggingConfig {
    private static final Logger ROOT_LOGGER = Logger.getLogger("");
    private static final String LOG_FILE_PATH = "logs/fitlogger.log";
    private static boolean isConfigured;

    /**
     * Routes logs to a file and removes default console handlers.
     */
    public static void configure() {
        if (isConfigured) {
            return;
        }

        removeConsoleHandlers();
        ROOT_LOGGER.setLevel(Level.INFO);

        try {
            Files.createDirectories(Path.of("logs"));
            FileHandler fileHandler = new FileHandler(LOG_FILE_PATH, true);
            fileHandler.setLevel(Level.INFO);
            fileHandler.setFormatter(new SimpleFormatter());
            ROOT_LOGGER.addHandler(fileHandler);
        } catch (IOException exception) {
            ROOT_LOGGER.setLevel(Level.OFF);
        }

        isConfigured = true;
    }

    private static void removeConsoleHandlers() {
        for (Handler handler : ROOT_LOGGER.getHandlers()) {
            ROOT_LOGGER.removeHandler(handler);
            handler.close();
        }
    }
}
