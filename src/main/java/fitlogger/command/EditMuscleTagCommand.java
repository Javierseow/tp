package fitlogger.command;

import fitlogger.exercisedictionary.ExerciseDictionary;
import fitlogger.musclegroup.MuscleGroup;

// The "Parent" that holds the shared data
public abstract class EditMuscleTagCommand extends Command {
    protected final int id;
    protected final MuscleGroup muscle;
    protected final ExerciseDictionary dictionary;

    public EditMuscleTagCommand(int id, MuscleGroup muscle, ExerciseDictionary dictionary) {
        this.id = id;
        this.muscle = muscle;
        this.dictionary = dictionary;
    }
}