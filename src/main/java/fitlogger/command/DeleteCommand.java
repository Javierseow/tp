package fitlogger.command;

import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workoutlist.WorkoutList;

/**
 * Deletes a workout from the in-memory workout list by user-facing index.
 *
 * <p>
 * Index matching uses one-based user input (e.g., "3") and maps it to zero-based internal indexing.
 * </p>
 */
public class DeleteCommand extends Command {
    /** The workout index provided by the user for deletion. */
    private final String workoutIndex;

    /**
     * Creates a delete command with the target workout index.
     *
     * @param workoutIndex The workout index to delete.
     */
    public DeleteCommand(String workoutIndex) {
        this.workoutIndex = workoutIndex;
    }

    /**
     * Executes the delete operation and prints feedback to the user.
     *
     * <p>
      * If the index is missing, non-numeric, non-positive, or out of range, a validation
      * message is shown and no state is changed.
     * </p>
     *
      * @param storage Storage component used to persist workouts and profile.
      * @param workouts In-memory workout list to mutate.
      * @param ui UI component used to display command results.
      * @param profile User profile saved together with workouts.
     */
    @Override
    public void execute(Storage storage, WorkoutList workouts, Ui ui, UserProfile profile) {
        if (workoutIndex == null || workoutIndex.isBlank()) {
            ui.showMessage("Please specify a workout index to delete. Usage: delete <index>");
            return;
        }

        String normalizedIndex = workoutIndex.trim();
        final int oneBasedIndex;

        try {
            oneBasedIndex = Integer.parseInt(normalizedIndex);
        } catch (NumberFormatException exception) {
            ui.showMessage("Workout index must be a positive integer.");
            return;
        }

        if (oneBasedIndex <= 0) {
            ui.showMessage("Invalid workout index: " + oneBasedIndex);
            return;
        }

        int zeroBasedIndex = oneBasedIndex - 1;
        if (zeroBasedIndex < 0 || zeroBasedIndex >= workouts.getSize()) {
            ui.showMessage("Invalid workout index: " + oneBasedIndex);
            return;
        }

        String deletedWorkoutName = workouts.getWorkoutAtIndex(zeroBasedIndex).getDescription();
        workouts.deleteWorkout(zeroBasedIndex);
        boolean isSaved = storage.saveData(workouts.getWorkouts(), profile);
        if (!isSaved) {
            ui.showError("Failed to save workouts to disk. Changes remain only in memory.");
            return;
        }
        ui.showMessage("Deleted workout: " + deletedWorkoutName);
    }
}
