package fitlogger.command;

import fitlogger.exercisedictionary.ExerciseDictionary;
import fitlogger.musclegroup.MuscleGroup;
import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workoutlist.WorkoutList;

import java.util.Set;

public class LiftMuscleGroupsCommand extends Command {
    private final int id;
    private final ExerciseDictionary dictionary;

    public LiftMuscleGroupsCommand(int id, ExerciseDictionary dictionary) {
        assert id >= 0 : "id is negative, possible error in ExerciseDictionary";
        this.id = id;
        this.dictionary = dictionary;
    }

    @Override
    public void execute(Storage storage, WorkoutList workouts, Ui ui, UserProfile profile) {
        String exerciseName = dictionary.getLiftName(id);
        Set<MuscleGroup> muscles = dictionary.getMusclesFor(id);

        if (muscles.isEmpty()) {
            ui.showMessage("No muscle groups tagged for " + exerciseName + " (ID: " + id + ").");
        } else {
            ui.showMessage("Muscle groups for " + exerciseName + ": " + muscles);
        }
    }
}