package fitlogger.command;

import fitlogger.exercisedictionary.ExerciseDictionary;
import fitlogger.musclegroup.MuscleGroup;
import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workoutlist.WorkoutList;

public class UntagMuscleCommand extends EditMuscleTagCommand {
    public UntagMuscleCommand(int id, MuscleGroup muscle, ExerciseDictionary dictionary) {
        super(id, muscle, dictionary);
    }

    @Override
    public void execute(Storage storage, WorkoutList workouts, Ui ui, UserProfile profile) {
        dictionary.untagMuscles(id, muscle);
        ui.showMessage("Removed " + muscle.displayName() + " from lift ID: " + id);
    }
}
