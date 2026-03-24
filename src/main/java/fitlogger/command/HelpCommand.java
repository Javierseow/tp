package fitlogger.command;

import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workoutlist.WorkoutList;

public class HelpCommand extends Command {

    public HelpCommand() {
        super();
    }

    public void execute(Storage storage, WorkoutList workouts, Ui ui) {
        ui.showHelpMenu();
    }
}
