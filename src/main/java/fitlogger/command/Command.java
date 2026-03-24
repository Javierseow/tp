package fitlogger.command;

import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workoutlist.WorkoutList;

public abstract class Command {
    public abstract void execute(Storage storage, WorkoutList workouts, Ui ui);

    public boolean isExit() {
        return false;
    }
}
