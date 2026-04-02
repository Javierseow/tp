package fitlogger.command;

import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workoutlist.WorkoutList;

/**
 * Command that saves current workout data and exits the application.
 */
public class ExitCommand extends Command {

    /**
     * Creates an {@code ExitCommand}
     */
    public ExitCommand() {
        super();
    }

    /**
     * Saves workout data and displays a goodbye message to the user.
     *
     * @param storage Storage handler used to save workout data.
     * @param workouts Workout list to be saved.
     * @param ui UI used to display output messages.
     * @param profile User profile saved together with workouts.
     */
    public void execute(Storage storage, WorkoutList workouts, Ui ui, UserProfile profile) {
        boolean isSaved = storage.saveData(workouts.getWorkouts(), profile);
        if (isSaved) {
            ui.showMessage("Workouts saved.");
        } else {
            ui.showError("Failed to save workouts to disk before exit.");
        }
        ui.showGoodbye();
    }

    /**
     * Indicates that this command should terminate the application loop.
     *
     * @return {@code true} always.
     */
    public boolean isExit() {
        return true;
    }
}
