package fitlogger.command;

import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workout.RunWorkout;
import fitlogger.workout.StrengthWorkout;
import fitlogger.workout.Workout;
import fitlogger.workoutlist.WorkoutList;

/**
 * Command that finds and displays the personal record for a specific exercise.
 *
 * <p>For {@link StrengthWorkout} entries, the PR is the entry with the highest
 * recorded weight. For {@link RunWorkout} entries, the PR is the entry with the
 * shortest recorded duration (fastest time), reflecting the standard running
 * definition of a personal record.
 */
public class ViewPrCommand extends Command {

    /** The exercise name to match against (case-insensitive). */
    private final String exerciseId;

    /**
     * Creates a {@code ViewPrCommand} for the given exercise name.
     *
     * @param exerciseId The exercise name to look up (e.g. "Bench Press").
     */
    public ViewPrCommand(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    /**
     * Scans the entire workout list for entries matching the target exercise ID,
     * finds the PR entry, and displays it via the UI.
     *
     * <p>PR definition:
     * <ul>
     *   <li>Strength: entry with the highest weight.</li>
     *   <li>Run: entry with the shortest duration (fastest time).</li>
     * </ul>
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
            ui.showMessage("Please specify an exercise name. Usage: pr <EXERCISE_NAME>");
            return;
        }

        Workout prWorkout = null;
        double maxWeight = Double.NEGATIVE_INFINITY;
        double minDuration = Double.POSITIVE_INFINITY;

        for (int i = 0; i < workouts.getSize(); i++) {
            Workout workout = workouts.getWorkoutAtIndex(i);

            if (!workout.getDescription().equalsIgnoreCase(exerciseId)) {
                continue;
            }

            if (workout instanceof StrengthWorkout) {
                double weight = ((StrengthWorkout) workout).getWeight();
                if (weight > maxWeight) {
                    maxWeight = weight;
                    prWorkout = workout;
                }
            } else if (workout instanceof RunWorkout) {
                double duration = ((RunWorkout) workout).getDurationMinutes();
                if (duration < minDuration) {
                    minDuration = duration;
                    prWorkout = workout;
                }
            }
        }

        if (prWorkout != null) {
            ui.showPr(prWorkout);
        } else {
            ui.showMessage("No record found for exercise: " + exerciseId);
        }
    }
}
