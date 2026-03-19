package fitlogger.parser;

import fitlogger.command.Command;
import fitlogger.command.DeleteCommand;
import fitlogger.command.ExitCommand;
import fitlogger.exception.FitLoggerException;
import fitlogger.workoutlist.WorkoutList;
import fitlogger.storage.Storage;

// import java.util.logging.Level;
// import java.util.logging.Logger;

public class Parser {
    // temporary for now, change later when we know how much description there is
    private static final int MAX_LIFT_INFO = 100;
    private static final int MAX_RUN_INFO = 50;
    // private static Logger logger = Logger.getLogger("Foo");

    public static Command parse(String fullCommand, WorkoutList workouts, Storage storage)
            throws FitLoggerException {
        //logger.log(Level.INFO, "going to start parsing");
        assert fullCommand != null : "Parser.parse was called with a null string!";
        String[] parts = fullCommand.trim().split(" ", 2);
        String commandWord = parts[0].toLowerCase();
        String arguments = (parts.length > 1) ? parts[1].trim() : "";

        switch (commandWord) {
        case "delete":
            return new DeleteCommand(workouts, arguments);

        case "exit":
            return new ExitCommand(storage, workouts);

        // case "help":
        // return new HelpCommand();

        default:
            throw new FitLoggerException("I'm sorry, I don't know what '" + commandWord
                    + "' means.\n" + "See 'help'");
        }
    }
}
