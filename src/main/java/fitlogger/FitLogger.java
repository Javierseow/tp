package fitlogger;

import fitlogger.command.Command;
import fitlogger.exception.FitLoggerException;
import fitlogger.logging.LoggingConfig;
import fitlogger.parser.Parser;
import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workoutlist.WorkoutList;
import fitlogger.exercisedictionary.ExerciseDictionary;

public class FitLogger {
    private Ui ui;
    private Storage storage;
    private WorkoutList workouts;
    private UserProfile profile;
    private ExerciseDictionary dictionary;

    public FitLogger() {
        LoggingConfig.configure();
        ui = new Ui();
        storage = new Storage();
        workouts = new WorkoutList();
        profile = new UserProfile();
        dictionary = new ExerciseDictionary();
        storage.setDictionary(dictionary);

        storage.loadData(profile).forEach(workouts::addWorkout);
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
                Command c = Parser.parse(command, workouts, dictionary);
                c.execute(storage, workouts, ui, profile);
                isExit = c.isExit();
            } catch (FitLoggerException e) {
                ui.showError(e.getMessage());
            } catch (Exception e) {
                ui.showError("Unexpected error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new FitLogger().run();
    }
}
