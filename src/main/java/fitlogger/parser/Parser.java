package fitlogger.parser;

import fitlogger.command.AddWorkoutCommand;
import fitlogger.command.Command;
import fitlogger.command.DeleteCommand;
import fitlogger.command.ExitCommand;
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
        //logger.log(Level.INFO, "going to start parsing");
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
            String[] runInfo = splitInput(parts[1], "([dt])/([\\d.]+)", 0);
            double duration = 0;
            double distance = 0;
            for (String runDetails : runInfo) {
                String[] runDetail = splitInput(runDetails, "/", 2);
                assert runDetail[2] == null;
                assert !runDetail[1].contains("/");
                assert runDetail[1].matches("\\d+(\\.\\d+)?") : "Expected a number but got: " + runDetail[1];
                assert runDetail[0].matches("[dt]");
                if (runDetail[0] == "t") {
                    duration = Integer.valueOf(runDetail[1]);
                    continue;
                }
                distance = Integer.valueOf(runDetail[1]);
            }
            Workout runToBeAdded = new RunWorkout(runInfo[0], LocalDate.now(), distance, duration);
            return new AddWorkoutCommand(workouts, runToBeAdded);

        // case "help":
        // return new HelpCommand();

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
