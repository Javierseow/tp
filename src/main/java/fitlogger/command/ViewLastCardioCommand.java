package fitlogger.command;

import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workout.RunWorkout;
import fitlogger.workout.Workout;
import fitlogger.workoutlist.WorkoutList;

public class ViewLastCardioCommand extends Command {

    private final String exerciseId;

    public ViewLastCardioCommand(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    @Override
    public void execute(Storage storage, WorkoutList workouts, Ui ui, UserProfile profile) {
        assert workouts != null : "WorkoutList must not be null";
        assert exerciseId != null : "Exercise ID must not be null";

        if (exerciseId.isBlank()) {
            ui.showMessage(
                    "Please specify a run name or shortcut ID. Usage: lastcardio <EXERCISE_NAME_OR_ID>");
            return;
        }

        for (int i = workouts.getSize() - 1; i >= 0; i--) {
            Workout workout = workouts.getWorkoutAtIndex(i);

            if (workout instanceof RunWorkout
                    && workout.getDescription().equalsIgnoreCase(exerciseId)) {
                ui.showLastCardio((RunWorkout) workout);
                return;
            }
        }

        ui.showMessage("No record found for exercise: " + exerciseId);
    }
}
