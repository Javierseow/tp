package fitlogger.command;

import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workoutlist.WorkoutList;

public class ViewHistoryCommand extends Command {
    private final int numberOfEntriesToShow;

    /**
     * Constructor for showing all history.
     */
    public ViewHistoryCommand() {
        this.numberOfEntriesToShow = -1; // Sentinel for "all"
    }

    /**
     * Constructor for showing a specific number of recent workouts.
     */
    public ViewHistoryCommand(int count) {
        assert count > 0 : "History count must be positive";
        this.numberOfEntriesToShow = count;
    }

    @Override
    public void execute(Storage storage, WorkoutList workouts, Ui ui, UserProfile profile) {
        int size = workouts.getSize();

        // Determine where to start printing
        int startIndex = 0;
        if (numberOfEntriesToShow != -1 && numberOfEntriesToShow < size) {
            startIndex = size - numberOfEntriesToShow;
            ui.showMessage("Showing the last " + numberOfEntriesToShow + " exercise(s):");
        } else if (numberOfEntriesToShow >= size) {
            ui.showMessage("You only have " + size + " exercises, showing all past exercises:");
        } else {
            ui.showMessage("Here's your past exercises:");
        }

        ui.showLine();
        for (int i = startIndex; i < size; i++) {
            ui.showMessageNoNewline((i + 1) + ". ");
            ui.printWorkout(workouts.getWorkoutAtIndex(i));
        }
        ui.showLine();
    }
}
