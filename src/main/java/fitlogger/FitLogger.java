package fitlogger;

import fitlogger.command.Command;
import fitlogger.command.ExitCommand;
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
        storage = new Storage(); // temp: please change
        workouts = new WorkoutList(); // temp: please change
    }

    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            String command = ui.readCommand();

            //later change this line to `Command c = parser.parse(command);` then parser calls based on keyword
            Parser.parse(command);

            //later remove this line after u change the line above
            Command c = new ExitCommand(storage, workouts);

            c.execute(ui);
            isExit = c.isExit();
        }
        
    }

    public static void main(String[] args) {
        new FitLogger().run();
    }
}
