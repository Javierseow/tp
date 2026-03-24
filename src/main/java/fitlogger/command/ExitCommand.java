package fitlogger.command;

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
     * @param ui UI used to display output messages. *
     * @param storage FitLogger.command.Storage handler used to save workout data.
     * @param workouts Workout list to be saved.
     */
    public void execute(Storage storage, WorkoutList workouts, Ui ui) {
        storage.saveData(workouts.getWorkouts());
        ui.showMessage("Workouts saved.");
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
