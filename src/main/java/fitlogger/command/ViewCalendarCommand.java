package fitlogger.command;

import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workout.Workout;
import fitlogger.workoutlist.WorkoutList;

import java.time.YearMonth;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a command to display an ASCII calendar for a specific month. This command filters all
 * recorded workouts to identify "active days" (days with at least one logged workout) and passes
 * them to the UI for rendering.
 */
public class ViewCalendarCommand extends Command {
    private final YearMonth targetMonth;

    /**
     * Initializes a new ViewCalendarCommand for the specified month and year.
     *
     * @param targetMonth The year and month to be displayed (e.g., 2026-04).
     */
    public ViewCalendarCommand(YearMonth targetMonth) {
        this.targetMonth = targetMonth;
    }

    /**
     * Executes the calendar view logic. It iterates through the workout list, identifies workouts
     * occurring in the target month, and collects their day-of-month values into a Set to be
     * highlighted in the ASCII calendar.
     *
     * @param storage The storage handler (unused in this command).
     * @param workouts The list of all recorded workouts.
     * @param ui The user interface used to display the formatted calendar.
     * @param profile The user profile information (unused in this command).
     */
    @Override
    public void execute(Storage storage, WorkoutList workouts, Ui ui, UserProfile profile) {
        Set<Integer> activeDays = new HashSet<>();

        for (Workout w : workouts.getWorkouts()) {
            if (YearMonth.from(w.getDate()).equals(targetMonth)) {
                activeDays.add(w.getDate().getDayOfMonth());
            }
        }

        ui.showCalendar(targetMonth, activeDays);
    }
}
