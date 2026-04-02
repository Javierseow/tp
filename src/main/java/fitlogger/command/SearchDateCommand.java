package fitlogger.command;

import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workout.Workout;
import fitlogger.workoutlist.WorkoutList;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Searches workouts by a specific date.
 */
public class SearchDateCommand extends Command {
    private final LocalDate targetDate;

    /**
     * Creates a search command for one target date.
     *
     * @param targetDate Date to match against each workout's date.
     */
    public SearchDateCommand(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    /**
     * Displays workouts completed on the target date.
     *
     * @param storage Storage component (unused by this command).
     * @param workouts Workout list to search.
     * @param ui UI used to display the filtered results.
     * @param profile User profile (unused by this command).
     */
    @Override
    public void execute(Storage storage, WorkoutList workouts, Ui ui, UserProfile profile) {
        ArrayList<Workout> matchingWorkouts = new ArrayList<>();

        for (int i = 0; i < workouts.getSize(); i++) {
            Workout workout = workouts.getWorkoutAtIndex(i);
            if (targetDate.equals(workout.getDate())) {
                matchingWorkouts.add(workout);
            }
        }

        ui.showMessage("Workouts on " + targetDate + ":");
        ui.showLine();
        ui.showWorkoutList(matchingWorkouts);
        ui.showLine();
    }
}
