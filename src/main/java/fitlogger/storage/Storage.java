package fitlogger.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import fitlogger.exception.FitLoggerException;
import fitlogger.musclegroup.MuscleGroup;
import fitlogger.parser.Parser;
import fitlogger.profile.UserProfile;
import fitlogger.workout.RunWorkout;
import fitlogger.workout.StrengthWorkout;
import fitlogger.workout.Workout;
import fitlogger.exercisedictionary.ExerciseDictionary;

/**
 * Handles persistence of workout data to and from the file system.
 * Workout entries are stored in a plain-text file, one entry per line,
 * using each workout's {@code toFileFormat()} representation.
 *
 * <p>Supported file formats:
 * <pre>
 *   R | 0/1 | description | yyyy-MM-dd | distance | duration
 *   L | 0/1 | description | yyyy-MM-dd | weight   | sets | reps
 * </pre>
 */
public class Storage {

    /** Path to the file where workout data is persisted. */
    private static final String FILE_PATH = "data/fitlogger.txt";

    /** Expected number of fields for each workout type. */
    private static final int RUN_FIELD_COUNT = 5;
    private static final int STRENGTH_FIELD_COUNT = 6;

    /** Index constants for shared fields. */
    private static final int INDEX_TYPE = 0;
    private static final int INDEX_DESCRIPTION = 1;
    private static final int INDEX_DATE = 2;

    /** Index constants for RunWorkout-specific fields. */
    private static final int INDEX_RUN_DISTANCE = 3;
    private static final int INDEX_RUN_DURATION = 4;

    /** Index constants for StrengthWorkout-specific fields. */
    private static final int INDEX_STRENGTH_WEIGHT = 3;
    private static final int INDEX_STRENGTH_SETS = 4;
    private static final int INDEX_STRENGTH_REPS = 5;

    private ExerciseDictionary dictionary;

    public void setDictionary(ExerciseDictionary dictionary) { // <-- ADD THIS
        this.dictionary = dictionary;
    }

    // ── saveData ─────────────────────────────────────────────────────────────

    /**
     * Saves all workouts in the provided list to {@code data/fitlogger.txt}.
     * The parent directory is created automatically if it does not exist.
     * Each workout is written on its own line using {@link Workout#toFileFormat()}.
     *
     * @param workouts The list of {@link Workout} objects to persist.
     */
    public boolean saveData(List<Workout> workouts, UserProfile profile) {
        assert workouts != null : "Workout list passed to saveData() must not be null";

        File file = new File(FILE_PATH);

        // Ensure the data/ directory exists before writing
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(profile.toFileFormat());
            writer.write(System.lineSeparator());

            if (this.dictionary != null) {
                for (java.util.Map.Entry<Integer, String> entry : this.dictionary.getLiftShortcuts().entrySet()) {
                    writer.write(generateLiftString(entry.getKey(), entry.getValue()));
                    writer.write(System.lineSeparator());
                }
                for (java.util.Map.Entry<Integer, String> entry : this.dictionary.getRunShortcuts().entrySet()) {
                    writer.write("S | run | " + entry.getKey() + " | " + entry.getValue() + System.lineSeparator());
                }
            }

            for (Workout workout : workouts) {
                writer.write(workout.toFileFormat());
                writer.write(System.lineSeparator());
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving workouts: " + e.getMessage());
            return false;
        }
    }

    private String generateLiftString(int id, String name) {
        String base = "S | lift | " + id + " | " + name;
        if (dictionary == null) {
            return base;
        }
        java.util.Set<MuscleGroup> muscles = dictionary.getMusclesFor(id);
        if (muscles.isEmpty()) {
            return base;
        }

        StringBuilder sb = new StringBuilder();
        for (MuscleGroup mg : muscles) {
            if (!sb.isEmpty()) {
                sb.append(",");
            }
            sb.append(mg.displayName());
        }
        return base + " | " + sb;
    }

    // ── loadData ─────────────────────────────────────────────────────────────

    /**
     * Loads workouts from {@code data/fitlogger.txt} and returns them as a list.
     * If the file or directory does not exist yet, an empty list is returned
     * silently — this is expected on first run.
     * Corrupted or unrecognised lines are skipped with a warning.
     *
     * @return A {@link List} of {@link Workout} objects read from disk,
     *         or an empty list if the file does not exist or cannot be read.
     */
    public List<Workout> loadData(UserProfile profile) {
        List<Workout> workouts = new ArrayList<>();
        File file = new File(FILE_PATH);

        // First run: file doesn't exist yet — return empty list silently
        if (!file.exists()) {
            return workouts;
        }

        try (Scanner scanner = new Scanner(file)) {
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine().trim();

                // Skip blank lines
                if (line.isEmpty()) {
                    continue;
                }

                try {
                    if (lineNumber == 1) {
                        parseProfile(line, profile);
                    } else {
                        Workout workout = parseLine(line);
                        if (workout != null) {
                            workouts.add(workout);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Warning: Skipping corrupted line " + lineNumber
                            + " in save file: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Save file not found: " + e.getMessage());
        }

        return workouts;
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private void parseProfile(String line, UserProfile profile) {
        //errors handled outside
        String[] weightData = Parser.splitInput(line, "weight: ", 2);
        String[] heightData = Parser.splitInput(weightData[0], "height: ", 2);
        String[] nameData = Parser.splitInput(heightData[0], "name: ", 2);

        double height = Double.parseDouble(heightData[1].trim());
        double weight = Double.parseDouble(weightData[1].trim());

        //errors in double values handled above, errors in name handled with if statement
        if (!nameData[1].trim().equals("null")) {
            profile.setName(nameData[1].trim());
        }
        profile.setHeight(height);
        profile.setWeight(weight);
    }

    /**
     * Parses a single line from the save file into a {@link Workout} object.
     * Dispatches to the appropriate parse method based on the type field.
     *
     * @param line A raw line read from the file.
     * @return The reconstructed {@link Workout}, or {@code null} if the type
     *         is unrecognised.
     * @throws IllegalArgumentException If required fields are missing or malformed.
     */
    private Workout parseLine(String line) {
        String[] fields = line.split("\\s*\\|\\s*");
        String type = fields[INDEX_TYPE].trim();

        switch (type) {
        case "R":
            return parseRunWorkout(fields);
        case "L":
            return parseStrengthWorkout(fields);
        case "S":
            if (this.dictionary != null) {
                parseShortcut(fields);
            }
            return null;
        default:
            System.out.println("Warning: Unknown workout type '" + type + "' — skipping.");
            return null;
        }
    }

    /**
     * Reconstructs a {@link RunWorkout} from split save-file fields.
     * Expected format: {@code R | description | yyyy-MM-dd | distance | duration}
     *
     * @param fields Fields split from a single save-file line.
     * @return The reconstructed {@link RunWorkout}.
     * @throws IllegalArgumentException If the field count is wrong or values are malformed.
     */
    private RunWorkout parseRunWorkout(String[] fields) {
        if (fields.length < RUN_FIELD_COUNT) {
            throw new IllegalArgumentException("RunWorkout entry has too few fields.");
        }

        String description = fields[INDEX_DESCRIPTION].trim();
        LocalDate date = parseDate(fields[INDEX_DATE]);
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Run date cannot be in the future: " + date);
        }

        double distance = parseDouble(fields[INDEX_RUN_DISTANCE], "distance");
        double duration = parseDouble(fields[INDEX_RUN_DURATION], "duration");

        try {
            return new RunWorkout(description, date, distance, duration);
        } catch (FitLoggerException exception) {
            throw new IllegalArgumentException(exception.getMessage(), exception);
        }
    }

    /**
     * Reconstructs a {@link StrengthWorkout} from split save-file fields.
     * Expected format: {@code L | description | yyyy-MM-dd | weight | sets | reps}
     *
     * @param fields Fields split from a single save-file line.
     * @return The reconstructed {@link StrengthWorkout}.
     * @throws IllegalArgumentException If the field count is wrong or values are malformed.
     */
    private StrengthWorkout parseStrengthWorkout(String[] fields) {
        if (fields.length < STRENGTH_FIELD_COUNT) {
            throw new IllegalArgumentException("StrengthWorkout entry has too few fields.");
        }

        String description = fields[INDEX_DESCRIPTION].trim();
        LocalDate date = parseDate(fields[INDEX_DATE]);
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Lift date cannot be in the future: " + date);
        }
        double weight = parseDouble(fields[INDEX_STRENGTH_WEIGHT], "weight");
        int sets = parseInt(fields[INDEX_STRENGTH_SETS], "sets");
        int reps = parseInt(fields[INDEX_STRENGTH_REPS], "reps");

        try {
            return new StrengthWorkout(description, weight, sets, reps, date);
        } catch (FitLoggerException exception) {
            throw new IllegalArgumentException(exception.getMessage(), exception);
        }
    }

    private void parseShortcut(String[] fields) {
        if (fields.length < 4) {
            System.out.println("Warning: Corrupted shortcut line.");
            return;
        }

        String type = fields[1].trim();
        int id;
        try {
            id = Integer.parseInt(fields[2].trim());
            if (id < 0) {
                System.out.println("Warning: ID is negative.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Warning: Invalid shortcut ID format.");
            return;
        }
        String name = fields[3].trim();

        if (type.equals("lift")) {
            this.dictionary.addLiftShortcut(id, name);
        } else if (type.equals("run")) {
            this.dictionary.addRunShortcut(id, name);
        }

        if (fields.length == 5 && type.equals("lift")) {
            //muscle group tags present
            String muscleField = fields[4].trim();
            if (muscleField.isEmpty()) {
                return;
            }
            assert this.dictionary.getLiftShortcuts().containsKey(id) : "ID " + id + " not found in dictionary!";
            for (String rawMuscleGroup : muscleField.split(",")) {
                String cleanMuscleGroup = rawMuscleGroup.trim().toUpperCase().replace(' ', '_');
                if (MuscleGroup.isValid(cleanMuscleGroup)) {
                    this.dictionary.tagMuscles(id, MuscleGroup.valueOf(cleanMuscleGroup));
                }
            }
        }
    }

    /**
     * Parses a date string in {@code yyyy-MM-dd} format.
     *
     * @param raw The raw field string from the save file.
     * @return The parsed {@link LocalDate}.
     * @throws IllegalArgumentException If the date format is invalid.
     */
    private LocalDate parseDate(String raw) {
        try {
            return LocalDate.parse(raw.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + raw);
        }
    }

    /**
     * Parses a double value from a raw field string.
     *
     * @param raw       The raw field string from the save file.
     * @param fieldName The field name used in the error message.
     * @return The parsed double value.
     * @throws IllegalArgumentException If the value is not a valid number.
     */
    private double parseDouble(String raw, String fieldName) {
        try {
            return Double.parseDouble(raw.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid " + fieldName + " value: " + raw);
        }
    }

    /**
     * Parses an integer value from a raw field string.
     *
     * @param raw       The raw field string from the save file.
     * @param fieldName The field name used in the error message.
     * @return The parsed integer value.
     * @throws IllegalArgumentException If the value is not a valid integer.
     */
    private int parseInt(String raw, String fieldName) {
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid " + fieldName + " value: " + raw);
        }
    }
}
