package fitlogger.command;

import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workout.StrengthWorkout;
import fitlogger.workout.Workout;
import fitlogger.workoutlist.WorkoutList;

/**
 * Command that finds and displays the most recent {@link StrengthWorkout}
 * matching a given exercise name.
 *
 * <p>The workout list is searched in reverse order so the latest entry
 * is found first.
 */
public class ViewLastLiftCommand extends Command {

    /** The exercise name to match against (case-insensitive). */
    private final String exerciseId;

    /**
     * Creates a {@code ViewLastLiftCommand} for the given exercise name.
     *
     * @param exerciseId The exercise name to look up (e.g. "Bench Press").
     */
    public ViewLastLiftCommand(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    /**
     * Searches the workout list in reverse order for the most recent
     * {@link StrengthWorkout} whose description matches the target exercise ID.
     * Displays the result via the UI, or a not-found message if none exists.
     *
     * @param storage  Unused by this command.
     * @param workouts The workout list to search through.
     * @param ui       The UI used to display results.
     * @param profile  Unused by this command.
     */
    @Override
    public void execute(Storage storage, WorkoutList workouts, Ui ui, UserProfile profile) {
        assert workouts != null : "WorkoutList must not be null";
        assert exerciseId != null : "Exercise ID must not be null";

        if (exerciseId.isBlank()) {
            ui.showMessage("Please specify an exercise name. Usage: lastlift <EXERCISE_NAME>");
            return;
        }

        for (int i = workouts.getSize() - 1; i >= 0; i--) {
            Workout workout = workouts.getWorkoutAtIndex(i);

            if (workout instanceof StrengthWorkout
                    && workout.getDescription().equalsIgnoreCase(exerciseId)) {
                ui.showLastLift((StrengthWorkout) workout);
                return;
            }
        }

        ui.showMessage("No record found for exercise: " + exerciseId);
    }
}
