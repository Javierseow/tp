package fitlogger.parser;


import fitlogger.command.AddWorkoutCommand;
import fitlogger.command.Command;
import fitlogger.command.DeleteCommand;
import fitlogger.command.EditCommand;
import fitlogger.command.ExitCommand;
import fitlogger.command.HelpCommand;
import fitlogger.command.UpdateProfileCommand;
import fitlogger.command.ViewDatabaseCommand;
import fitlogger.command.ViewHistoryCommand;
import fitlogger.command.ViewLastLiftCommand;
import fitlogger.command.ViewProfileCommand;
import fitlogger.command.ViewShoeMileageCommand;
import fitlogger.exception.FitLoggerException;
import fitlogger.workout.RunWorkout;
import fitlogger.workout.StrengthWorkout;
import fitlogger.workout.Workout;
import fitlogger.workoutlist.WorkoutList;
import fitlogger.exercisedictionary.ExerciseDictionary;

import java.time.LocalDate;

public class Parser {

    public static Command parse(String fullCommand, WorkoutList workouts, ExerciseDictionary dictionary)
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

        case "profile":
            return parseProfile(arguments);

        case "view-total-mileage":
            return new ViewShoeMileageCommand();

        case "edit":
            return parseEdit(arguments, workouts);

        case "add-run":
            return parseAddRun(arguments, workouts, dictionary);

        case "add-lift":
            return parseAddLift(arguments, workouts, dictionary);

        case "list":
            // fallthrough intentional — same behaviour as history
        case "history":
            return new ViewHistoryCommand();

        case "help":
            return new HelpCommand();

        case "view-database":
            return new ViewDatabaseCommand(dictionary);

        case "lastlift":
            return new ViewLastLiftCommand(arguments);

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
    private static Command parseAddRun(String arguments, WorkoutList workouts, ExerciseDictionary dictionary)
            throws FitLoggerException {
        if (arguments.isBlank()) {
            throw new FitLoggerException("Missing arguments for add-run.\n"
                    + "Usage: add-run <name_or_id> d/<distance> t/<durationMinutes>");
        }

        String[] runInfo = splitInput(arguments, "d/|t/", 3);

        if (runInfo.length < 3) {
            throw new FitLoggerException("Invalid format for add-run.\n"
                    + "Usage: add-run <name_or_id> d/<distance> t/<durationMinutes>");
        }

        String name = runInfo[0].trim();
        try {
            int shortcutId = Integer.parseInt(name);
            String dictionaryName = dictionary.getRunName(shortcutId);

            if (dictionaryName == null) {
                throw new FitLoggerException("Shortcut ID [" + shortcutId + "] does not exist. "
                        + "Type 'view-database' to see available shortcuts.");
            }
            name = dictionaryName;

        } catch (NumberFormatException e) {
            // normal string like "Easy Run"
        }

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
        if (!Double.isFinite(distance) || !Double.isFinite(durationMinutes)) {
            throw new FitLoggerException(
                    "Distance and duration must be realistic positive numbers.");
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
    private static Command parseAddLift(String arguments, WorkoutList workouts, ExerciseDictionary dictionary)
            throws FitLoggerException {
        if (arguments.isBlank()) {
            throw new FitLoggerException("Missing arguments for add-lift.\n"
                    + "Usage: add-lift <name_or_id> w/<weightKg> s/<sets> r/<reps>");
        }

        String[] info = splitInput(arguments, "w/|s/|r/", 4);

        if (info.length < 4) {
            throw new FitLoggerException("Invalid format for add-lift.\n"
                    + "Usage: add-lift <name_or_id> w/<weightKg> s/<sets> r/<reps>");
        }

        String name = info[0].trim();
        try {
            int shortcutId = Integer.parseInt(name);
            String dictionaryName = dictionary.getLiftName(shortcutId);

            if (dictionaryName == null) {
                throw new FitLoggerException("Shortcut ID [" + shortcutId + "] does not exist in the database. "
                        + "Type 'view-database' to see available shortcuts.");
            }
            name = dictionaryName;

        } catch (NumberFormatException e) {
            // normal string like "Bench Press".
        }

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
     * Parses an edit command.
     *
     * <p>
     * Expected format: {@code edit <index> <field>/<value>}
     * </p>
     *
     * @param arguments Everything after "edit ".
     * @param workouts The active workout list.
     * @return An {@link EditCommand} that updates one workout field.
     * @throws FitLoggerException if arguments are missing or malformed.
     */
    private static Command parseEdit(String arguments, WorkoutList workouts)
            throws FitLoggerException {
        if (arguments.isBlank()) {
            throw new FitLoggerException(
                    "Missing arguments for edit.\n" + "Usage: edit <index> <field>/<value>");
        }

        String[] editParts = splitInput(arguments, " ", 2);
        if (editParts.length < 2) {
            throw new FitLoggerException(
                    "Invalid format for edit.\n" + "Usage: edit <index> <field>/<value>");
        }

        int index;
        try {
            index = Integer.parseInt(editParts[0].trim());
        } catch (NumberFormatException exception) {
            throw new FitLoggerException("Workout index must be a positive integer.");
        }

        if (index <= 0) {
            throw new FitLoggerException("Workout index must be a positive integer.");
        }

        String[] fieldValue = splitInput(editParts[1], "/", 2);
        if (fieldValue.length < 2) {
            throw new FitLoggerException(
                    "Invalid format for edit.\n" + "Usage: edit <index> <field>/<value>");
        }

        String fieldName = fieldValue[0].trim();
        String newValue = fieldValue[1].trim();

        if (fieldName.isBlank() || newValue.isBlank()) {
            throw new FitLoggerException(
                    "Invalid format for edit.\n" + "Usage: edit <index> <field>/<value>");
        }

        return new EditCommand(index, fieldName, newValue);
    }

    private static Command parseProfile(String arguments) throws FitLoggerException {
        if (arguments.isBlank()) {
            throw new FitLoggerException(
                    "Missing arguments for viewing/setting up profile.\n"
                            + "Usage: profile view OR profile set <field> <value>");
        }
        String[] info = splitInput(arguments, " ", 3);
        assert info.length > 0 : "Profile arguments are missing";

        try {
            switch (info[0]) {
            case "view":
                //ignores all entries after it
                return new ViewProfileCommand();
            case "set":
                if (info.length < 2) {
                    throw new FitLoggerException("Field not provided. \n"
                            + "Available fields: name / height / weight");
                }
                assert !info[1].isEmpty();
                assert !info[1].isBlank();

                double updatedHeightOrWeight = -1;

                switch (info[1]) {
                case "name":
                    return new UpdateProfileCommand(info[2], -1, -1);
                case "height":
                    updatedHeightOrWeight = updateHeightOrWeight(info[2], 0.3, 3);
                    return new UpdateProfileCommand(null, updatedHeightOrWeight, -1);
                case "weight":
                    updatedHeightOrWeight = updateHeightOrWeight(info[2], 10, 500);
                    return new UpdateProfileCommand(null, -1, updatedHeightOrWeight);
                default:
                    throw new FitLoggerException("Invalid field provided. \n"
                            + "Available fields: name / height / weight");
                }
            default:
                throw new FitLoggerException("Invalid profile action. \n"
                        + "Usage: profile view OR profile set <field> <value>");
            }
        } catch (IndexOutOfBoundsException e) {
            throw new FitLoggerException("No value provided. \n"
                    + "Please provide a value to be updated.");
        }
    }

    private static double updateHeightOrWeight(String value, double lowerBound, double upperBound)
            throws FitLoggerException {
        try {
            double newValue = Double.parseDouble(value);
            if (newValue < lowerBound || newValue > upperBound) {
                throw new FitLoggerException("Your Height/Weight is too low/high.\n" +
                        "Please ensure your values are correctly inputted");
            }
            return newValue;
        } catch (NumberFormatException e) {
            throw new FitLoggerException("Please provide a valid number for height/weight");
        }
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
