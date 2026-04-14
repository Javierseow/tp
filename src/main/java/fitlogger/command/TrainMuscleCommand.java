package fitlogger.command;

import fitlogger.exercisedictionary.ExerciseDictionary;
import fitlogger.musclegroup.MuscleGroup;
import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workoutlist.WorkoutList;

import java.util.Map;

/**
 * Command to filter and display exercises that target a specific muscle group. It searches the
 * exercise dictionary for lifts tagged with the specified muscle.
 */
public class TrainMuscleCommand extends Command {
    private final MuscleGroup targetMuscle;
    private final ExerciseDictionary dictionary;

    /**
     * Constructs a TrainMuscleCommand.
     *
     * @param targetMuscle The muscle group the user wants to train.
     * @param dictionary The dictionary containing exercise and muscle tag data.
     */
    public TrainMuscleCommand(MuscleGroup targetMuscle, ExerciseDictionary dictionary) {
        this.targetMuscle = targetMuscle;
        this.dictionary = dictionary;
    }

    /**
     * Executes the search for exercises targeting the specified muscle. Displays a list of matching
     * exercises or a helpful hint if none are found.
     *
     * @param storage The storage handler.
     * @param workouts The list of workouts.
     * @param ui The user interface used to display the results.
     * @param profile The user profile.
     */
    @Override
    public void execute(Storage storage, WorkoutList workouts, Ui ui, UserProfile profile) {
        ui.showLine();
        ui.showMessage("Exercises targeting: " + targetMuscle.displayName());
        boolean exerciseFound = false;

        for (Map.Entry<Integer, String> entry : dictionary.getLiftShortcuts().entrySet()) {
            if (dictionary.getMusclesFor(entry.getKey()).contains(targetMuscle)) {
                exerciseFound = true;
                ui.showMessage("   [" + entry.getKey() + "] -> " + entry.getValue());
            }
        }

        if (!exerciseFound) {
            ui.showMessage("No lift exercises currently targeting " + targetMuscle.displayName());
            ui.showMessage("Use 'tag-muscle <shortcut-ID> <muscle>' to tag an exercise");
        }
        ui.showLine();
    }
}
