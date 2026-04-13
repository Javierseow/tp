package fitlogger.command;

import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workoutlist.WorkoutList;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Command that saves current workout data and exits the application.
 */
public class ExitCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(ExitCommand.class.getName());

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
        assert storage != null : "Storage must not be null";
        assert workouts != null : "WorkoutList must not be null";
        assert ui != null : "Ui must not be null";
        assert profile != null : "UserProfile must not be null";

        LOGGER.log(Level.INFO, "Exit requested; attempting final save");

        boolean isSaved = storage.saveData(workouts.getWorkouts(), profile);
        if (isSaved) {
            LOGGER.log(Level.INFO, "Exit save succeeded");
            ui.showMessage("Workouts saved.");
        } else {
            LOGGER.log(Level.WARNING, "Exit save failed");
            ui.showError("Failed to save workouts to disk before exit.");
        }
        LOGGER.log(Level.INFO, "Exit command completed; showing goodbye message");
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
