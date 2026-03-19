package fitlogger.command;

import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workoutlist.WorkoutList;

/**
 * Command that saves current workout data and exits the application.
 */
public class ExitCommand extends Command {
    private final Storage storage;
    private final WorkoutList workouts;

    /**
     * Creates an {@code ExitCommand} that persists the current workout list before exit.
     *
     * @param storage FitLogger.command.Storage handler used to save workout data.
     * @param workouts Workout list to be saved.
     */
    public ExitCommand(Storage storage, WorkoutList workouts) {
        this.storage = storage;
        this.workouts = workouts;
    }

    /**
     * Saves workout data and displays a goodbye message to the user.
     *
     * @param ui UI used to display output messages.
     */
    public void execute(Ui ui) {
        storage.saveData(workouts.getWorkouts());
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
