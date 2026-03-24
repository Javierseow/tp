package fitlogger.command;

import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workoutlist.WorkoutList;

public class ViewHistoryCommand extends Command {

    public ViewHistoryCommand() {
        super();
    }

    @Override
    public void execute(Storage storage, WorkoutList workouts, Ui ui) {
        ui.showMessage("Here's your past exercises");
        ui.showLine();
        for (int i = 0; i < workouts.getSize(); i++) {
            ui.showMessageNoNewline(i + 1 + ". ");
            ui.printWorkout(workouts.getWorkoutAtIndex(i));
        }
        ui.showLine();
    }
}
