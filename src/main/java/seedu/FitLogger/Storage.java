package seedu.fitlogger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import seedu.fitlogger.workout.Workout;

/**
 * Handles persistence of workout data to and from the file system.
 * Workout entries are stored in a plain-text file, one entry per line,
 * using each workout's {@code toFileFormat()} representation.
 */
public class Storage {

    /** Path to the file where workout data is persisted. */
    private static final String FILE_PATH = "data/fitlogger.txt";

    /**
     * Saves all workouts in the provided list to {@code data/fitlogger.txt}.
     * The parent directory is created automatically if it does not exist.
     * Each workout is written on its own line using {@link Workout#toFileFormat()}.
     *
     * @param workouts The list of {@link Workout} objects to persist.
     */
    public void saveData(List<Workout> workouts) {
        File file = new File(FILE_PATH);

        // Ensure the data/ directory exists before writing
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(file)) {
            for (Workout workout : workouts) {
                writer.write(workout.toFileFormat());
                writer.write(System.lineSeparator());
            }
            System.out.println("Workouts saved to " + FILE_PATH);
        } catch (IOException e) {
            System.out.println("Error saving workouts: " + e.getMessage());
        }
    }
}
