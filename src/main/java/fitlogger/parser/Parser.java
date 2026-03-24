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

public class Parser {

    public static Command parse(String fullCommand, WorkoutList workouts, Storage storage)
            throws FitLoggerException {
        assert fullCommand != null : "Parser.parse was called with a null string!";
        String[] parts = splitInput(fullCommand, " ", 2);
        String commandWord = parts[0].toLowerCase();
        String arguments = (parts.length > 1) ? parts[1].trim() : "";

        switch (commandWord) {
            case "delete":
                return new DeleteCommand(arguments);

            case "exit":
                return new ExitCommand();

            case "add-run":
                return parseAddRun(arguments, workouts);

            case "add-lift":
                return parseAddLift(arguments, workouts);

            case "list":
                // fallthrough intentional — same behaviour as history
            case "history":
                return new ViewHistoryCommand();

            case "help":
                return new HelpCommand();

            default:
                throw new FitLoggerException(
                        "I'm sorry, I don't know what '" + commandWord + "' means.\nSee 'help'");
        }
    }

    /**
     * Parses an add-run command.
     *
     * <p>
     * Expected format: {@code add-run <name> d/<distance> t/<durationMinutes>}
     *
     * @param arguments Everything after "add-run ".
     * @param workouts The active workout list.
     * @return An {@link AddWorkoutCommand} wrapping a new {@link RunWorkout}.
     * @throws FitLoggerException if arguments are missing, malformed, or contain illegal storage
     *         characters.
     */
    private static Command parseAddRun(String arguments, WorkoutList workouts)
            throws FitLoggerException {
        if (arguments.isBlank()) {
            throw new FitLoggerException("Missing arguments for add-run.\n"
                    + "Usage: add-run <name> d/<distance> t/<durationMinutes>");
        }

        String[] runInfo = splitInput(arguments, "d/|t/", 3);

        if (runInfo.length < 3) {
            throw new FitLoggerException("Invalid format for add-run.\n"
                    + "Usage: add-run <name> d/<distance> t/<durationMinutes>");
        }

        String name = runInfo[0].trim();
        validateNoStorageDelimiters(name, "Run name");

        double distance;
        double durationMinutes;
        try {
            distance = Double.parseDouble(runInfo[1].trim());
            durationMinutes = Double.parseDouble(runInfo[2].trim());
        } catch (NumberFormatException e) {
            throw new FitLoggerException("Distance and duration must be valid numbers.\n"
                    + "Usage: add-run <name> d/<distance> t/<durationMinutes>");
        }

        if (distance <= 0) {
            throw new FitLoggerException("Distance must be a positive number.");
        }
        if (durationMinutes <= 0) {
            throw new FitLoggerException("Duration must be a positive number.");
        }

        Workout run = new RunWorkout(name, LocalDate.now(), distance, durationMinutes);
        return new AddWorkoutCommand(run);
    }

    /**
     * Parses an add-lift command.
     *
     * <p>
     * Expected format: {@code add-lift <name> w/<weightKg> s/<sets> r/<reps>}
     *
     * @param arguments Everything after "add-lift ".
     * @param workouts The active workout list.
     * @return An {@link AddWorkoutCommand} wrapping a new {@link StrengthWorkout}.
     * @throws FitLoggerException if arguments are missing, malformed, or contain illegal storage
     *         characters.
     */
    private static Command parseAddLift(String arguments, WorkoutList workouts)
            throws FitLoggerException {
        if (arguments.isBlank()) {
            throw new FitLoggerException("Missing arguments for add-lift.\n"
                    + "Usage: add-lift <name> w/<weightKg> s/<sets> r/<reps>");
        }

        String[] info = splitInput(arguments, "w/|s/|r/", 4);

        if (info.length < 4) {
            throw new FitLoggerException("Invalid format for add-lift.\n"
                    + "Usage: add-lift <name> w/<weightKg> s/<sets> r/<reps>");
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
                            + "Usage: add-lift <name> w/<weightKg> s/<sets> r/<reps>");
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
        return new AddWorkoutCommand(strength);
    }

    /**
     * Rejects strings containing "|" or "/" because those characters are used as delimiters in the
     * storage file and would corrupt it on save/load.
     *
     * @param value The string to validate.
     * @param fieldName Human-readable field name used in the error message.
     * @throws FitLoggerException if the value contains a forbidden character.
     */
    public static void validateNoStorageDelimiters(String value, String fieldName)
            throws FitLoggerException {
        if (value.contains("|") || value.contains("/")) {
            throw new FitLoggerException(fieldName + " must not contain '|' or '/' — "
                    + "these characters are reserved by the storage format.");
        }
    }

    /**
     * Splits {@code line} on {@code splitCharacter} (treated as a regex), stripping surrounding
     * whitespace, returning at most {@code maxSplit} parts.
     */
    public static String[] splitInput(String line, String splitCharacter, int maxSplit) {
        return line.trim().split("\\s*" + splitCharacter + "\\s*", maxSplit);
    }
}
