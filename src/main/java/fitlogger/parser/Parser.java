package fitlogger.parser;


import fitlogger.command.AddWorkoutCommand;
import fitlogger.command.ClearProfileCommand;
import fitlogger.command.Command;
import fitlogger.command.DeleteCommand;
import fitlogger.command.EditCommand;
import fitlogger.command.ExitCommand;
import fitlogger.command.FilterTypeCommand;
import fitlogger.command.HelpCommand;
import fitlogger.command.LiftMuscleGroupsCommand;
import fitlogger.command.SearchDateCommand;
import fitlogger.command.TagMuscleCommand;
import fitlogger.command.TrainMuscleCommand;
import fitlogger.command.UntagMuscleCommand;
import fitlogger.command.UpdateProfileCommand;
import fitlogger.command.ViewCalendarCommand;
import fitlogger.command.ViewDatabaseCommand;
import fitlogger.command.ViewHistoryCommand;
import fitlogger.command.ViewLastLiftCommand;
import fitlogger.command.ViewMuscleGroupCommand;
import fitlogger.command.ViewProfileCommand;
import fitlogger.command.ViewShoeMileageCommand;
import fitlogger.command.AddShortcutCommand;
import fitlogger.command.DeleteShortcutCommand;
import fitlogger.command.ViewPrCommand;
import fitlogger.exception.FitLoggerException;
import fitlogger.musclegroup.MuscleGroup;
import fitlogger.workout.RunWorkout;
import fitlogger.workout.StrengthWorkout;
import fitlogger.workout.Workout;
import fitlogger.workoutlist.WorkoutList;
import fitlogger.exercisedictionary.ExerciseDictionary;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

public class Parser {
    public static final int MAX_INTEGER_INPUT = 1_000_000;

    public static Command parse(String fullCommand, WorkoutList workouts,
            ExerciseDictionary dictionary) throws FitLoggerException {
        assert fullCommand != null : "Parser.parse was called with a null string!";
        String[] parts = splitInput(fullCommand, " ", 2);
        String commandWord = parts[0].toLowerCase();
        String arguments = (parts.length > 1) ? parts[1].trim() : "";

        switch (commandWord) {
        case "delete":
            return parseDelete(arguments);

        case "search-date":
            return parseSearchDate(arguments);

        case "exit":
            return parseExit(arguments);

        case "train":
            return parseTrainMuscle(arguments, dictionary);

        case "view-muscle-groups":
            return new ViewMuscleGroupCommand();

        case "muscle-groups":
            return parseLiftMuscleGroup(arguments, dictionary);

        case "tag-muscle":
            return parseTagMuscle(arguments, dictionary, true);

        case "untag-muscle":
            return parseTagMuscle(arguments, dictionary, false);

        case "profile":
            return parseProfile(arguments);

        case "view-total-mileage":
            return parseViewShoeMileage(arguments);

        case "edit":
            return parseEdit(arguments);

        case "add-run":
            return parseAddRun(arguments, workouts, dictionary);

        case "add-lift":
            return parseAddLift(arguments, workouts, dictionary);

        case "history":
            return parseViewHistory(arguments);

        case "help":
            return new HelpCommand();

        case "view-database":
            return new ViewDatabaseCommand(dictionary, false);

        case "view-detailed-database":
            return new ViewDatabaseCommand(dictionary, true);

        case "add-shortcut":
            return parseAddShortcut(arguments, dictionary);

        case "delete-shortcut":
            return parseDeleteShortcut(arguments, dictionary);

        case "lastlift":
            return new ViewLastLiftCommand(arguments);

        case "filter":
            return new FilterTypeCommand(arguments, dictionary);

        case "view-calendar":
            return parseViewCalendar(arguments);

        case "pr":
            return new ViewPrCommand(arguments);

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
    private static Command parseAddRun(String arguments, WorkoutList workouts,
            ExerciseDictionary dictionary) throws FitLoggerException {
        if (arguments.isBlank()) {
            throw new FitLoggerException("Missing arguments for add-run.\n"
                    + "Usage: add-run <name_or_id> d/<distanceKm> t/<durationMinutes>");
        }

        String[] runInfo = splitInput(arguments, "d/|t/", 3);

        if (runInfo.length < 3) {
            throw new FitLoggerException("Invalid format for add-run.\n"
                    + "Usage: add-run <name_or_id> d/<distanceKm> t/<durationMinutes>");
        }

        String name = runInfo[0].trim();
        if (name.matches("\\d+")) {
            int shortcutId = parsePositiveIntegerWithinLimit(name, "Shortcut ID");
            String dictionaryName = dictionary.getRunName(shortcutId);

            if (dictionaryName == null) {
                throw new FitLoggerException("Shortcut ID [" + shortcutId + "] does not exist. "
                        + "Type 'view-database' to see available shortcuts.");
            }
            name = dictionaryName;
        }

        validateNoStorageDelimiters(name, "Run name");

        double distance;
        double durationMinutes;
        try {
            // check if d/comes before t/
            String[] checkDataIntegrity = splitInput(arguments.trim(), "d/", 0);
            if (checkDataIntegrity[0].contains("t/")) {
                throw new FitLoggerException("Invalid format for add-run.\n"
                        + "Usage: add-run <name_or_id> d/<distanceKm> t/<durationMinutes>");
            }

            String distanceText = runInfo[1].trim();
            String durationText = runInfo[2].trim();
            if (!isPlainDecimalNumber(distanceText) || !isPlainDecimalNumber(durationText)) {
                throw new NumberFormatException();
            }
            if (durationText.matches(".*\\s+.*")) {
                throw new FitLoggerException("Invalid format. No additional text allowed after duration.");
            }
            distance = Double.parseDouble(distanceText);
            durationMinutes = Double.parseDouble(durationText);
        } catch (NumberFormatException e) {
            throw new FitLoggerException("Distance and duration must be valid decimal numbers.\n"
                    + "Usage: add-run <name> d/<distanceKm> t/<durationMinutes>");
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
    private static Command parseAddLift(String arguments, WorkoutList workouts,
            ExerciseDictionary dictionary) throws FitLoggerException {
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
        if (name.matches("\\d+")) {
            int shortcutId = parsePositiveIntegerWithinLimit(name, "Shortcut ID");
            String dictionaryName = dictionary.getLiftName(shortcutId);

            if (dictionaryName == null) {
                throw new FitLoggerException(
                        "Shortcut ID [" + shortcutId + "] does not exist in the database. "
                                + "Type 'view-database' to see available shortcuts.");
            }
            name = dictionaryName;
        }

        validateNoStorageDelimiters(name, "Exercise name");

        double weight;
        int sets;
        int reps;
        try {
            // check if correct order
            String[] checkDataIntegrity = splitInput(arguments.trim(), "s/", 2);
            if (!checkDataIntegrity[0].contains("w/") || !checkDataIntegrity[1].contains("r/")) {
                throw new FitLoggerException("Invalid format for add-lift.\n"
                        + "Usage: add-lift <name_or_id> w/<weightKg> s/<sets> r/<reps>");
            }
            String weightText = info[1].trim();
            if (!isPlainDecimalNumber(weightText)) {
                throw new NumberFormatException();
            }
            weight = Double.parseDouble(weightText);
            if (!Double.isFinite(weight)) {
                throw new NumberFormatException();
            }
            sets = parsePositiveIntegerWithinLimit(info[2].trim(), "Sets");
            String repsText = info[3].trim();
            if (repsText.matches(".*\\s+.*")) {
                throw new FitLoggerException("Invalid format. No additional text allowed after reps.");
            }
            reps = parsePositiveIntegerWithinLimit(repsText, "Reps");

        } catch (NumberFormatException e) {
            throw new FitLoggerException(
                    "Weight must be a decimal number; sets and reps must be integers.\n"
                            + "Usage: add-lift <name> w/<weightKg> s/<sets> r/<reps>");
        }

        if (weight < 0) {
            throw new FitLoggerException("Weight cannot be negative.");
        }

        Workout strength = new StrengthWorkout(name, weight, sets, reps, LocalDate.now());
        return new AddWorkoutCommand(strength);
    }

    private static Command parseAddShortcut(String arguments, ExerciseDictionary dictionary)
            throws FitLoggerException {
        if (arguments.isBlank()) {
            throw new FitLoggerException(
                    "Missing arguments.\n" + "Usage: add-shortcut <lift/run> <ID> <Exercise Name>");
        }

        // Split into exactly 3 parts: type, ID, and the rest is the name
        String[] parts = splitInput(arguments, " ", 3);
        if (parts.length < 3) {
            throw new FitLoggerException(
                    "Invalid format.\n" + "Usage: add-shortcut <lift/run> <ID> <Exercise Name>");
        }

        String type = parts[0].toLowerCase();
        if (!type.equals("lift") && !type.equals("run")) {
            throw new FitLoggerException("Shortcut type must be 'lift' or 'run'.");
        }

        int id = parsePositiveIntegerWithinLimit(parts[1].trim(), "Shortcut ID");

        String name = parts[2].trim();
        validateNoStorageDelimiters(name, "Shortcut name");

        return new AddShortcutCommand(type, id, name, dictionary);
    }

    private static Command parseDeleteShortcut(String arguments, ExerciseDictionary dictionary)
            throws FitLoggerException {
        if (arguments.isBlank()) {
            throw new FitLoggerException("Missing arguments.\nUsage: delete-shortcut <lift/run> <ID>");
        }
        String[] parts = splitInput(arguments, " ", 2);
        if (parts.length < 2) {
            throw new FitLoggerException("Invalid format.\nUsage: delete-shortcut <lift/run> <ID>");
        }
        String type = parts[0].toLowerCase();
        if (!type.equals("lift") && !type.equals("run")) {
            throw new FitLoggerException("Shortcut type must be 'lift' or 'run'.");
        }
        int id = parsePositiveIntegerWithinLimit(parts[1].trim(), "Shortcut ID");
        return new DeleteShortcutCommand(type, id, dictionary);
    }

    /**
     * Parses an edit command.
     *
     * <p>
     * Expected format: {@code edit <index> <field>/<value>}
     * </p>
     *
     * @param arguments Everything after "edit ".
     * @return An {@link EditCommand} that updates one workout field.
     * @throws FitLoggerException if arguments are missing or malformed.
     */
    private static Command parseEdit(String arguments) throws FitLoggerException {
        if (arguments.isBlank()) {
            throw new FitLoggerException(
                    "Missing arguments for edit.\n" + "Usage: edit <index> <field>/<value>");
        }

        String[] editParts = splitInput(arguments, " ", 2);
        if (editParts.length < 2) {
            throw new FitLoggerException(
                    "Invalid format for edit.\n" + "Usage: edit <index> <field>/<value>");
        }

        int index = parsePositiveIntegerWithinLimit(editParts[0].trim(), "Workout index");

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

    /**
     * Parses a delete command.
     *
     * <p>
     * Expected format: {@code delete <index>}
     * </p>
     *
     * @param arguments Everything after {@code delete }.
     * @return A {@link DeleteCommand} for the parsed one-based index.
     * @throws FitLoggerException if the index is missing or not a positive integer.
     */
    private static Command parseDelete(String arguments) throws FitLoggerException {
        if (arguments.isBlank()) {
            throw new FitLoggerException(
                    "Please specify a workout index to delete. Usage: delete <index>");
        }
        if (splitInput(arguments, " ", 2).length > 1) {
            throw new FitLoggerException("Invalid format for delete.\nUsage: delete <index>");
        }

        final int oneBasedIndex =
                parsePositiveIntegerWithinLimit(arguments.trim(), "Workout index");

        return new DeleteCommand(oneBasedIndex);
    }

    private static Command parseExit(String arguments) throws FitLoggerException {
        if (!arguments.isBlank()) {
            throw new FitLoggerException("Invalid format for exit.\nUsage: exit");
        }
        return new ExitCommand();
    }

    private static Command parseTrainMuscle(String arguments, ExerciseDictionary dictionary)
            throws FitLoggerException {
        if (arguments.isBlank()) {
            throw new FitLoggerException("Missing muscle group.\nUsage: train <muscle group>\n"
                    + "Example: train delts   OR   train upper back");
        }

        String muscleGroup = arguments.trim().toUpperCase().replace(' ', '_');
        if (!MuscleGroup.isValid(muscleGroup)) {
            throw new FitLoggerException("Invalid muscle group.\n"
                    + "Type 'view-muscle-groups' to see all available muscle groups");
        }
        return new TrainMuscleCommand(MuscleGroup.valueOf(muscleGroup), dictionary);
    }

    private static Command parseTagMuscle(String arguments, ExerciseDictionary dictionary,
            boolean isTag) throws FitLoggerException {
        String usage = (isTag ? "tag" : "untag") + "-muscle <shortcut-ID> <muscle-group>";
        if (arguments.isBlank()) {
            throw new FitLoggerException("Missing arguments.\nUsage: " + usage);
        }
        String[] parts = splitInput(arguments, " ", 2);
        if (parts.length < 2) {
            throw new FitLoggerException("Invalid format.\nUsage: " + usage);
        }

        int id;
        try {
            id = parsePositiveIntegerWithinLimit(parts[0].trim(), "Shortcut ID");
        } catch (FitLoggerException e) {
            throw new FitLoggerException("Input a valid shortcut ID not greater than "
                    + MAX_INTEGER_INPUT + ".\n"
                    + "Perform 'view-database' for all available shortcuts");
        }
        if (!dictionary.getLiftShortcuts().containsKey(id)) {
            throw new FitLoggerException("Shortcut does not exist in database.\n"
                    + "Perform 'view-database' for all available shortcuts");
        }
        String muscleGroup = parts[1].trim().toUpperCase().replace(' ', '_');
        if (!MuscleGroup.isValid(muscleGroup)) {
            throw new FitLoggerException("Muscle group does not exist in database.\n"
                    + "Perform 'view-muscle-groups' for all available shortcuts");
        }
        if (isTag) {
            return new TagMuscleCommand(id, MuscleGroup.valueOf(muscleGroup), dictionary);
        }
        return new UntagMuscleCommand(id, MuscleGroup.valueOf(muscleGroup), dictionary);
    }

    private static Command parseLiftMuscleGroup(String arguments, ExerciseDictionary dictionary)
            throws FitLoggerException {
        if (arguments.isBlank()) {
            throw new FitLoggerException("Missing arguments.\nUsage: muscle-groups <shortcut_ID>");
        }
        int id;
        try {
            id = parsePositiveIntegerWithinLimit(arguments.trim(), "Shortcut ID");
        } catch (FitLoggerException e) {
            throw new FitLoggerException("Input a valid shortcut ID not greater than "
                    + MAX_INTEGER_INPUT + ".\n"
                    + "Perform 'view-database' for all available shortcuts");
        }
        if (!dictionary.getLiftShortcuts().containsKey(id)) {
            throw new FitLoggerException("Shortcut does not exist in database.\n"
                    + "Perform 'view-database' for all available shortcuts");
        }
        return new LiftMuscleGroupsCommand(id, dictionary);
    }

    /**
     * Parses a search-date command.
     *
     * <p>
     * Expected format: {@code search-date <YYYY-MM-DD>}
     * </p>
     *
     * @param arguments Everything after {@code search-date }.
     * @return A {@link SearchDateCommand} for the parsed date.
     * @throws FitLoggerException if the date is missing or not in ISO format.
     */
    private static Command parseSearchDate(String arguments) throws FitLoggerException {
        if (arguments.isBlank()) {
            throw new FitLoggerException(
                    "Missing arguments for search-date.\nUsage: search-date <YYYY-MM-DD>");
        }
        if (splitInput(arguments, " ", 2).length > 1) {
            throw new FitLoggerException(
                    "Invalid format for search-date.\nUsage: search-date <YYYY-MM-DD>");
        }

        try {
            return new SearchDateCommand(LocalDate.parse(arguments.trim()));
        } catch (DateTimeParseException exception) {
            throw new FitLoggerException(
                    "Invalid date format for search-date.\nUsage: search-date <YYYY-MM-DD>");
        }
    }

    private static Command parseProfile(String arguments) throws FitLoggerException {
        if (arguments.isBlank()) {
            throw new FitLoggerException("Missing arguments for viewing/setting up profile.\n"
                    + "Usage: profile view/clear OR profile set <field> <value>");
        }
        String[] info = splitInput(arguments, " ", 3);
        assert info.length > 0 : "Profile arguments are missing";

        try {
            switch (info[0].toLowerCase()) {
            case "view":
                // ignores all entries after it
                return new ViewProfileCommand();
            case "clear":
                //ignores all entries after it
                return new ClearProfileCommand();
            case "set":
                if (info.length < 2) {
                    throw new FitLoggerException("Field not provided. \n"
                            + "Available fields: name / height / weight");
                }
                assert !info[1].isEmpty();
                assert !info[1].isBlank();

                double updatedHeightOrWeight = -1;

                switch (info[1].toLowerCase()) {
                case "name":
                    return new UpdateProfileCommand(info[2], -1, -1);
                case "height":
                    updatedHeightOrWeight = updateHeightOrWeight(info[2], 0.3, 3);
                    return new UpdateProfileCommand(null, updatedHeightOrWeight, -1);
                case "weight":
                    updatedHeightOrWeight = updateHeightOrWeight(info[2], 10, 500);
                    return new UpdateProfileCommand(null, -1, updatedHeightOrWeight);
                default:
                    throw new FitLoggerException("Invalid profile action. \n"
                            + "Usage: profile view/clear OR profile set <field> <value>");
                }
            default:
                throw new FitLoggerException("Invalid profile action. \n"
                        + "Usage: profile view/clear OR profile set <field> <value>");
            }
        } catch (IndexOutOfBoundsException e) {
            throw new FitLoggerException(
                    "No value provided. \n" + "Please provide a value to be updated.");
        }
    }

    private static double updateHeightOrWeight(String value, double lowerBound, double upperBound)
            throws FitLoggerException {
        try {
            double newValue = Double.parseDouble(value);
            if (!Double.isFinite(newValue)) {
                throw new FitLoggerException("Invalid input. Please provide a real number.");
            }
            if (newValue < lowerBound || newValue > upperBound) {
                throw new FitLoggerException("Your Height/Weight is unrealistically low/high.\n"
                        + "Please ensure your values are correct, height in m and weight in kg");
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

    /**
     * Returns true only for ordinary decimal notation, without scientific notation.
     */
    public static boolean isPlainDecimalNumber(String value) {
        return value != null && value.trim().matches("-?\\d+(\\.\\d+)?");
    }

    /**
     * Parses positive integer input and rejects values above the application limit.
     */
    public static int parsePositiveIntegerWithinLimit(String value, String fieldName)
            throws FitLoggerException {
        try {
            int parsedValue = Integer.parseInt(value.trim());
            if (parsedValue <= 0) {
                throw new FitLoggerException(fieldName + " must be a positive integer.");
            }
            if (parsedValue > MAX_INTEGER_INPUT) {
                throw new FitLoggerException(fieldName + " must not exceed "
                        + MAX_INTEGER_INPUT + ".");
            }
            return parsedValue;
        } catch (NumberFormatException exception) {
            if (value.trim().matches("\\d+")) {
                throw new FitLoggerException(fieldName + " must not exceed "
                        + MAX_INTEGER_INPUT + ".");
            }
            throw new FitLoggerException(fieldName + " must be a positive integer.");
        }
    }

    private static Command parseViewCalendar(String arguments) throws FitLoggerException {
        try {
            if (arguments.isBlank()) {
                // Default to current month if no arguments
                return new ViewCalendarCommand(YearMonth.now());
            }
            // Expected format: YYYY-MM
            return new ViewCalendarCommand(YearMonth.parse(arguments));
        } catch (DateTimeParseException e) {
            throw new FitLoggerException(
                    "Invalid calendar format. Use YYYY-MM (e.g., view-calendar 2026-04)");
        }
    }

    private static Command parseViewHistory(String arguments) throws FitLoggerException {
        if (arguments.isBlank()) {
            return new ViewHistoryCommand();
        }

        try {
            int count = Integer.parseInt(arguments.trim());
            if (count <= 0) {
                throw new FitLoggerException("History count must be a positive integer.");
            }
            if (count >= Parser.MAX_INTEGER_INPUT) {
                throw new NumberFormatException();
            }
            return new ViewHistoryCommand(count);
        } catch (NumberFormatException e) {
            throw new FitLoggerException("Invalid format. Usage: history [NUMBER]\n" +
                    "NUMBER should be a positive integer and below " + Parser.MAX_INTEGER_INPUT + ".");
        }
    }

    private static Command parseViewShoeMileage(String arguments) throws FitLoggerException {
        if (arguments.isBlank()) {
            return new ViewShoeMileageCommand();
        }

        try {
            int days = Integer.parseInt(arguments.trim());
            if (days < 0) {
                throw new FitLoggerException("Number of days must be a non-negative integer.");
            }
            if (days >= Parser.MAX_INTEGER_INPUT) {
                throw new NumberFormatException();
            }
            return new ViewShoeMileageCommand(days);
        } catch (NumberFormatException e) {
            throw new FitLoggerException("Invalid format. Usage: view-total-mileage [DAYS]\n"
                    + "DAYS should be a non-negative integer and below " + Parser.MAX_INTEGER_INPUT + ".");
        }
    }
}
