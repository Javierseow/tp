package fitlogger;

import fitlogger.command.Command;
import fitlogger.exception.FitLoggerException;
import fitlogger.parser.Parser;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workoutlist.WorkoutList;

public class FitLogger {
    private Ui ui;
    private Storage storage;
    private WorkoutList workouts;

    public FitLogger() {
        ui = new Ui();
        storage = new Storage();
        workouts = new WorkoutList();
        storage.loadData().forEach(workouts::addWorkout);
    }

    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            try {
                String command = ui.readCommand();
                if (command.isBlank()) {
                    continue;
                }
                Command c = Parser.parse(command, workouts, storage);
                c.execute(storage, workouts, ui);
                isExit = c.isExit();
            } catch (FitLoggerException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new FitLogger().run();
    }
}
