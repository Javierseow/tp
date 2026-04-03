package fitlogger.command;

import fitlogger.exercisedictionary.ExerciseDictionary;
import fitlogger.musclegroup.MuscleGroup;
import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workoutlist.WorkoutList;

public class TagMuscleCommand extends Command {
    private final int id;
    private final MuscleGroup muscles;
    private final ExerciseDictionary dictionary;

    public TagMuscleCommand(int id, MuscleGroup muscle, ExerciseDictionary dictionary) {
        this.id         = id;
        this.muscles    = muscle;
        this.dictionary = dictionary;
    }

    @Override
    public void execute(Storage storage, WorkoutList workouts, Ui ui, UserProfile profile) {

    }
}
