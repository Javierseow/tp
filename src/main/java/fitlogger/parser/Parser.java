package fitlogger.parser;

import fitlogger.command.AddWorkoutCommand;
import fitlogger.command.Command;
import fitlogger.command.DeleteCommand;
import fitlogger.command.ExitCommand;
import fitlogger.command.HelpCommand;
import fitlogger.command.ViewHistoryCommand;
import fitlogger.exception.FitLoggerException;
import fitlogger.workout.RunWorkout;
import fitlogger.workout.Workout;
import fitlogger.workoutlist.WorkoutList;
import fitlogger.storage.Storage;

import java.time.LocalDate;

// import java.util.logging.Level;
// import java.util.logging.Logger;

public class Parser {
    // temporary for now, change later when we know how much description there is
    private static final int MAX_LIFT_INFO = 100;
    private static final int MAX_RUN_INFO = 50;
    // private static Logger logger = Logger.getLogger("Foo");

    public static Command parse(String fullCommand, WorkoutList workouts, Storage storage)
            throws FitLoggerException {
        // logger.log(Level.INFO, "going to start parsing");
        assert fullCommand != null : "Parser.parse was called with a null string!";
        String[] parts = splitInput(fullCommand, " ", 2);
        String commandWord = parts[0].toLowerCase();
        String arguments = (parts.length > 1) ? parts[1].trim() : "";

        switch (commandWord) {
        case "delete":
            return new DeleteCommand(workouts, arguments);

        case "exit":
            return new ExitCommand(storage, workouts);

        case "add-run":
            assert parts[1] != null : "Description is missing";
            String[] runInfo = splitInput(parts[1], "d/|t/", 3);
            assert runInfo[1].trim().matches("\\d+(\\.\\d+)?") : "Expected a number but got: " + runInfo[1];
            assert runInfo[2].trim().matches("\\d+(\\.\\d+)?") : "Expected a number but got: " + runInfo[2];
            Workout runToBeAdded = new RunWorkout(runInfo[0], LocalDate.now(),
                    Double.parseDouble(runInfo[1]), Double.parseDouble(runInfo[2]));
            return new AddWorkoutCommand(workouts, runToBeAdded);

        case "list":
            return new ViewHistoryCommand(workouts);
        // case "help":
        // return new HelpCommand();
        case "help":
            return new HelpCommand();

        case "history":
            return new ViewHistoryCommand(workouts);

        default:
            throw new FitLoggerException("I'm sorry, I don't know what '" + commandWord
                    + "' means.\n" + "See 'help'");
        }
    }

    public static String[] splitInput(String line, String splitCharacter, int maxSplit) {
        //use \\s* to remove whitespace
        return line.trim().split("\\s*" + splitCharacter + "\\s*", maxSplit);
    }
}
