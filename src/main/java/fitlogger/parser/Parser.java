package fitlogger.parser;

import fitlogger.command.AddWorkoutCommand;
import fitlogger.command.Command;
import fitlogger.command.DeleteCommand;
import fitlogger.command.ExitCommand;
import fitlogger.command.HelpCommand;
import fitlogger.command.ViewHistoryCommand;
import fitlogger.exception.FitLoggerException;
import fitlogger.workout.RunWorkout;
import fitlogger.workout.StrengthWorkout;
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

        case "add-strength":
            return parseAddStrength(arguments, workouts);

        case "list":

        case "history":
            return new ViewHistoryCommand(workouts);

        case "help":
            return new HelpCommand();


        default:
            throw new FitLoggerException("I'm sorry, I don't know what '" + commandWord
                    + "' means.\n" + "See 'help'");
        }
    }

    /**
     * Parses an add-strength command.
     *
     * <p>Expected format:
     * {@code add-strength <name> w/<weightKg> s/<sets> r/<reps>}
     *
     * @param arguments Everything after "add-strength ".
     * @param workouts  The active workout list.
     * @return An {@link AddWorkoutCommand} wrapping a new {@link StrengthWorkout}.
     * @throws FitLoggerException if arguments are missing, malformed, or contain
     *                            illegal storage characters.
     */
    private static Command parseAddStrength(String arguments, WorkoutList workouts)
            throws FitLoggerException {
        if (arguments.isBlank()) {
            throw new FitLoggerException(
                    "Missing arguments for add-strength.\n"
                            + "Usage: add-strength <name> w/<weightKg> s/<sets> r/<reps>");
        }

        String[] info = splitInput(arguments, "w/|s/|r/", 4);

        if (info.length < 4) {
            throw new FitLoggerException(
                    "Invalid format for add-strength.\n"
                            + "Usage: add-strength <name> w/<weightKg> s/<sets> r/<reps>");
        }

        String name = info[0].trim();
        validateNoStorageDelimiters(name, "Exercise name");

        double weight;
        int sets;
        int reps;
        try {
            weight = Double.parseDouble(info[1].trim());
            sets = Integer.parseInt(info[2].trim());
            reps = Integer.parseInt(info[3].trim());
        } catch (NumberFormatException e) {
            throw new FitLoggerException(
                    "Weight must be a decimal number; sets and reps must be integers.\n"
                            + "Usage: add-strength <name> w/<weightKg> s/<sets> r/<reps>");
        }

        if (weight < 0) {
            throw new FitLoggerException("Weight cannot be negative.");
        }
        if (sets <= 0) {
            throw new FitLoggerException("Sets must be a positive integer.");
        }
        if (reps <= 0) {
            throw new FitLoggerException("Reps must be a positive integer.");
        }

        Workout strength = new StrengthWorkout(name, weight, sets, reps, LocalDate.now());
        return new AddWorkoutCommand(workouts, strength);
    }

    /**
     * Rejects strings containing "|" or "/" because those characters are used as
     * delimiters in the storage file and would corrupt it on save/load.
     *
     * @param value     The string to validate.
     * @param fieldName Human-readable field name used in the error message.
     * @throws FitLoggerException if the value contains a forbidden character.
     */
    static void validateNoStorageDelimiters(String value, String fieldName)
            throws FitLoggerException {
        if (value.contains("|") || value.contains("/")) {
            throw new FitLoggerException(
                    fieldName + " must not contain '|' or '/' — "
                            + "these characters are reserved by the storage format.");
        }
    }

    /**
     * Splits {@code line} on {@code splitCharacter} (treated as a regex),
     * stripping surrounding whitespace, returning at most {@code maxSplit} parts.
     */
    public static String[] splitInput(String line, String splitCharacter, int maxSplit) {
        //use \\s* to remove whitespace
        return line.trim().split("\\s*" + splitCharacter + "\\s*", maxSplit);
    }
}
