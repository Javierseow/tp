package fitlogger.command;

import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workout.Workout;
import fitlogger.workoutlist.WorkoutList;

/**
 * Deletes a workout from the in-memory workout list by name or by user-facing index.
 *
 * <p>
 * Name matching is case-insensitive and compares against the full workout name. Index matching uses
 * one-based user input (e.g., "3") and maps it to zero-based internal indexing.
 * </p>
 */
public class DeleteCommand extends Command {
    /** The workout name provided by the user for deletion. */
    private final String workoutName;

    /**
     * Creates a delete command with the target workout name.
     *
     * @param workoutName The workout name to delete.
     */
    public DeleteCommand(String workoutName) {
        this.workoutName = workoutName;
    }

    /**
     * Executes the delete operation and prints feedback to the user.
     *
     * <p>
     * If no workout name is provided, a usage hint is shown. If a matching workout exists, it is
     * removed from the list; otherwise, a not-found message is shown.
     * </p>
     *
     * @param ui UI component used to display command results.
     */
    @Override
    public void execute(Storage storage, WorkoutList workouts, Ui ui) {
        if (workoutName == null || workoutName.isBlank()) {
            ui.showMessage("Please specify a workout to delete. "
                    + "Usage: delete workout <WORKOUT_NAME> or delete <index>");
            return;
        }

        String normalizedName = workoutName.trim();
        int indexToDelete = findWorkoutIndex(normalizedName, workouts);

        if (indexToDelete != -1) {
            String deletedWorkoutName = workouts.getWorkoutAtIndex(indexToDelete).getDescription();
            workouts.deleteWorkout(indexToDelete);
            ui.showMessage("Deleted workout: " + deletedWorkoutName);
        } else {
            ui.showMessage("Workout not found: " + normalizedName);
        }
    }

    /**
     * Resolves the workout to delete from user input.
     *
     * <p>
     * The input is first treated as a one-based index (e.g., {@code 1} maps to internal index
     * {@code 0}). If it is not a valid integer, the input is treated as a workout name and matched
     * case-insensitively.
     * </p>
     *
     * @param input Raw user-provided value after the delete command.
     * @return The zero-based workout index if a match is found; {@code -1} otherwise.
     */
    private int findWorkoutIndex(String input, WorkoutList workouts) {
        Integer oneBasedIndex = parseUserProvidedIndex(input);
        if (oneBasedIndex != null) {
            int zeroBasedIndex = oneBasedIndex - 1;
            if (zeroBasedIndex >= 0 && zeroBasedIndex < workouts.getSize()) {
                return zeroBasedIndex;
            }
            return -1;
        }

        return findWorkoutIndexByName(input, workouts);
    }

    /**
     * Parses a one-based workout index from user input.
     *
     * @param input Raw user input that may represent an integer index.
     * @return Parsed one-based index, or {@code null} if input is not numeric.
     */
    private Integer parseUserProvidedIndex(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    /**
     * Finds a workout by exact description match (case-insensitive).
     *
     * @param workoutNameToFind The workout name to search for.
     * @return The zero-based index of the first matching workout, or {@code -1} if none matches.
     */
    private int findWorkoutIndexByName(String workoutNameToFind, WorkoutList workouts) {
        for (int i = 0; i < workouts.getSize(); i++) {
            Workout workout = workouts.getWorkoutAtIndex(i);
            if (workout.getDescription().equalsIgnoreCase(workoutNameToFind)) {
                return i;
            }
        }
        return -1;
    }
}
