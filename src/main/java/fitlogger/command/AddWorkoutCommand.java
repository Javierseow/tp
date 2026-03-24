package fitlogger.command;

import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workout.Workout;
import fitlogger.workoutlist.WorkoutList;

public class AddWorkoutCommand extends Command {
    private final Workout workoutToAdd;

    public AddWorkoutCommand(Workout workoutToAdd) {
        this.workoutToAdd = workoutToAdd;
    }

    @Override
    public void execute(Storage storage, WorkoutList workouts, Ui ui) {
        workouts.addWorkout(workoutToAdd);

        ui.showMessage("Got it. I've added this workout:");
        ui.printWorkout(workoutToAdd);
        ui.showMessage("Now you have " + workouts.getSize() + " workouts in the list.");
    }
}
