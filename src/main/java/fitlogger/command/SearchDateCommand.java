package fitlogger.command;

import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workout.Workout;
import fitlogger.workoutlist.WorkoutList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Searches workouts by a specific date.
 */
public class SearchDateCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(SearchDateCommand.class.getName());

    private final LocalDate targetDate;

    /**
     * Creates a search command for one target date.
     *
     * @param targetDate Date to match against each workout's date.
     */
    public SearchDateCommand(LocalDate targetDate) {
        assert targetDate != null : "Target date must not be null";
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
        assert storage != null : "Storage must not be null";
        assert workouts != null : "WorkoutList must not be null";
        assert ui != null : "Ui must not be null";
        assert profile != null : "UserProfile must not be null";

        LOGGER.log(Level.INFO, "Search-date requested for {0}", targetDate);

        ArrayList<Workout> matchingWorkouts = new ArrayList<>();

        for (int i = 0; i < workouts.getSize(); i++) {
            Workout workout = workouts.getWorkoutAtIndex(i);
            if (targetDate.equals(workout.getDate())) {
                matchingWorkouts.add(workout);
            }
        }

        if (matchingWorkouts.isEmpty()) {
            LOGGER.log(Level.INFO, "Search-date found no workouts for {0}", targetDate);
            ui.showWorkoutList(matchingWorkouts);
            return;
        }

        LOGGER.log(Level.INFO, "Search-date found {0} workouts for {1}",
                new Object[]{matchingWorkouts.size(), targetDate});
        ui.showMessage("Workouts on " + targetDate + ":");
        ui.showLine();
        ui.showWorkoutList(matchingWorkouts);
        ui.showLine();
    }
}
