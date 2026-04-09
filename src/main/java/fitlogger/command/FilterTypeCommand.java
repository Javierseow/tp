package fitlogger.command;

import fitlogger.exercisedictionary.ExerciseDictionary;
import fitlogger.musclegroup.MuscleGroup;
import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workout.StrengthWorkout;
import fitlogger.workout.Workout;
import fitlogger.workoutlist.WorkoutList;

import java.util.ArrayList;
import java.util.Set;

/**
 * Represents a command to filter the workout history based on a specific muscle group. This command
 * iterates through the list of workouts and identifies those that are associated with the target
 * muscle group via the exercise dictionary.
 */
public class FilterTypeCommand extends Command {
    private final String targetCategory;
    private final ExerciseDictionary dictionary;

    /**
     * Initializes a new FilterTypeCommand with the specified muscle group and dictionary.
     *
     * @param targetCategory The name of the muscle group to filter by (e.g., "pecs").
     * @param dictionary The dictionary used to look up muscle group tags for exercises.
     */
    public FilterTypeCommand(String targetCategory, ExerciseDictionary dictionary) {
        this.targetCategory = targetCategory;
        this.dictionary = dictionary;
    }

    /**
     * Executes the filtering logic. It iterates through the history, checks for matching muscle
     * group tags, and displays the filtered list to the user.
     *
     * @param storage The storage handler (unused in this command).
     * @param workouts The list of all recorded workouts to filter from.
     * @param ui The user interface to display the results.
     * @param profile The user profile information (unused in this command).
     */
    @Override
    public void execute(Storage storage, WorkoutList workouts, Ui ui, UserProfile profile) {
        assert workouts != null : "WorkoutList must not be null";
        assert targetCategory != null : "Target Category must not be null";

        if (targetCategory.isBlank()) {
            ui.showMessage("Please specify a muscle group. Usage: filter <muscle_group>");
            return;
        }

        ArrayList<Workout> filteredList = new ArrayList<>();

        for (Workout workout : workouts.getWorkouts()) {
            if (workout instanceof StrengthWorkout) {
                int shortcutId = dictionary.getShortcutIdFor(workout.getDescription());

                if (shortcutId != -1) {
                    Set<MuscleGroup> muscleGroups = dictionary.getMusclesFor(shortcutId);
                    if (matchesCategory(muscleGroups)) {
                        filteredList.add(workout);
                    }
                }
            }
        }

        ui.showLine();
        ui.showMessage("Workouts matching category [" + targetCategory + "]:");
        ui.showWorkoutList(filteredList);
        ui.showLine();
    }

    /**
     * Checks if any of the muscle groups in the provided set match the target category. The
     * comparison is case-insensitive to ensure a user-friendly filtering experience.
     *
     * @param groups The set of muscle groups associated with a specific exercise.
     * @return true if a match is found; false otherwise.
     */
    private boolean matchesCategory(Set<MuscleGroup> groups) {
        for (MuscleGroup group : groups) {
            if (group.displayName().equalsIgnoreCase(targetCategory)) {
                return true;
            }
        }
        return false;
    }
}
