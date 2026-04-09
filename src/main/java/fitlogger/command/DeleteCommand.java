package fitlogger.command;

import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workoutlist.WorkoutList;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Deletes a workout from the in-memory workout list by user-facing index.
 *
 * <p>
 * Index matching uses one-based user input (e.g., "3") and maps it to zero-based internal indexing.
 * </p>
 */
public class DeleteCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(DeleteCommand.class.getName());

    /** The one-based workout index provided by the user for deletion. */
    private final int oneBasedIndex;

    /**
     * Creates a delete command with the target workout index.
     *
     * @param oneBasedIndex The one-based workout index to delete.
     */
    public DeleteCommand(int oneBasedIndex) {
        this.oneBasedIndex = oneBasedIndex;
    }

    /**
     * Executes the delete operation and prints feedback to the user.
     *
     * <p>
      * If the parsed index is out of range, a validation message is shown and no state is
      * changed.
     * </p>
     *
      * @param storage Storage component used to persist workouts and profile.
      * @param workouts In-memory workout list to mutate.
      * @param ui UI component used to display command results.
      * @param profile User profile saved together with workouts.
     */
    @Override
    public void execute(Storage storage, WorkoutList workouts, Ui ui, UserProfile profile) {
        assert storage != null : "Storage must not be null";
        assert workouts != null : "WorkoutList must not be null";
        assert ui != null : "Ui must not be null";
        assert profile != null : "UserProfile must not be null";

        LOGGER.log(Level.INFO, "Delete requested for one-based index {0}", oneBasedIndex);

        int zeroBasedIndex = oneBasedIndex - 1;
        if (zeroBasedIndex < 0 || zeroBasedIndex >= workouts.getSize()) {
            LOGGER.log(Level.WARNING, "Delete rejected: invalid one-based index {0}",
                    oneBasedIndex);
            ui.showMessage("Invalid workout index: " + oneBasedIndex);
            return;
        }

        String deletedWorkoutName = workouts.getWorkoutAtIndex(zeroBasedIndex).getDescription();
        workouts.deleteWorkout(zeroBasedIndex);
        boolean isSaved = storage.saveData(workouts.getWorkouts(), profile);
        if (!isSaved) {
            LOGGER.log(Level.WARNING, "Delete applied in memory but save failed for index {0}",
                    oneBasedIndex);
            ui.showError("Failed to save workouts to disk. Changes remain only in memory.");
            return;
        }
        LOGGER.log(Level.INFO, "Delete succeeded for workout \"{0}\"", deletedWorkoutName);
        ui.showMessage("Deleted workout: " + deletedWorkoutName);
    }
}
