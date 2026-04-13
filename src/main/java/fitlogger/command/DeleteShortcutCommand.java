package fitlogger.command;

import fitlogger.exercisedictionary.ExerciseDictionary;
import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workoutlist.WorkoutList;
import fitlogger.exception.FitLoggerException;

public class DeleteShortcutCommand extends Command {
    private final String type;
    private final int id;
    private final ExerciseDictionary dictionary;

    public DeleteShortcutCommand(String type, int id, ExerciseDictionary dictionary) {
        this.type = type;
        this.id = id;
        this.dictionary = dictionary;
    }

    @Override
    public void execute(Storage storage, WorkoutList workouts, Ui ui, UserProfile profile) {
        try {
            if (type.equals("lift")) {
                if (!dictionary.getLiftShortcuts().containsKey(id)) {
                    throw new FitLoggerException("Shortcut ID [L" + id + "] does not exist.");
                }
                String name = dictionary.getLiftName(id);
                dictionary.removeLiftShortcut(id);
                ui.showMessage("Success! Removed strength shortcut: [L" + id + "] -> " + name);
            } else if (type.equals("run")) {
                if (!dictionary.getRunShortcuts().containsKey(id)) {
                    throw new FitLoggerException("Shortcut ID [R" + id + "] does not exist.");
                }
                String name = dictionary.getRunName(id);
                dictionary.removeRunShortcut(id);
                ui.showMessage("Success! Removed run shortcut: [R" + id + "] -> " + name);
            }
        } catch (FitLoggerException e) {
            ui.showMessage(e.getMessage());
        }
    }
}
