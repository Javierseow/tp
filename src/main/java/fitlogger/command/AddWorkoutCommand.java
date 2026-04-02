package fitlogger.command;

import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workout.Workout;
import fitlogger.workoutlist.WorkoutList;

/**
 * Adds a newly created workout to the workout list.
 */
public class AddWorkoutCommand extends Command {
    private final Workout workoutToAdd;

    /**
     * Creates an add-workout command for one workout instance.
     *
     * @param workoutToAdd Workout to be appended to the list.
     */
    public AddWorkoutCommand(Workout workoutToAdd) {
        this.workoutToAdd = workoutToAdd;
    }

    /**
     * Adds the workout, persists the updated list, and prints confirmation.
     *
     * @param storage Storage component used to persist workouts and profile.
     * @param workouts In-memory workout list to mutate.
     * @param ui UI used to display command feedback.
     * @param profile User profile saved together with workouts.
     */
    @Override
    public void execute(Storage storage, WorkoutList workouts, Ui ui, UserProfile profile) {
        workouts.addWorkout(workoutToAdd);
        boolean isSaved = storage.saveData(workouts.getWorkouts(), profile);
        if (!isSaved) {
            ui.showError("Failed to save workouts to disk. Changes remain only in memory.");
            return;
        }

        ui.showMessage("Got it. I've added this workout:");
        ui.printWorkout(workoutToAdd);
        ui.showMessage("Now you have " + workouts.getSize() + " workouts in the list.");
    }
}
