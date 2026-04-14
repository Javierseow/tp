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
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a command to filter the workout history based on one or more muscle groups. This
 * command iterates through the list of workouts and identifies those that are associated with any
 * of the target muscle groups via the exercise dictionary. Supports stacking filters (e.g., "filter
 * quads glutes").
 */
public class FilterTypeCommand extends Command {
    private final Set<String> targetCategories;
    private final ExerciseDictionary dictionary;

    /**
     * Initializes a new FilterTypeCommand with the specified muscle group(s) and dictionary.
     *
     * @param targetCategoriesInput The name(s) of muscle groups to filter by (e.g., "pecs" or
     *        "quads glutes" or "quads,glutes"). Supports space-separated and comma-separated
     *        formats.
     * @param dictionary The dictionary used to look up muscle group tags for exercises.
     */
    public FilterTypeCommand(String targetCategoriesInput, ExerciseDictionary dictionary) {
        this.targetCategories = parseCategories(targetCategoriesInput);
        this.dictionary = dictionary;
    }

    /**
     * Parses the input string to extract individual muscle group categories. Supports
     * space-separated, comma-separated, and underscore-separated (for multi-word groups) formats.
     * Multi-word muscle groups should use underscores: upper_back, lower_back.
     *
     * @param input The raw input string containing one or more muscle groups.
     * @return A set of individual muscle group names (lowercased with spaces instead of
     *         underscores).
     */
    private static Set<String> parseCategories(String input) {
        Set<String> categories = new HashSet<>();
        if (input == null || input.isBlank()) {
            return categories;
        }

        // First split by comma to handle comma-separated values
        String[] commaSeparated = input.split(",");

        for (String commaPart : commaSeparated) {
            if (commaPart.isBlank()) {
                continue;
            }

            // Then split each comma-separated part by whitespace
            // This supports both space-separated and comma-separated formats
            String[] spaceSeparated = commaPart.trim().split("\\s+");

            for (String part : spaceSeparated) {
                if (part.isBlank()) {
                    continue;
                }

                // Convert underscores to spaces to match displayName() format
                // This allows "upper_back" to be matched as "upper back"
                String normalized = part.replace('_', ' ').toLowerCase();

                if (!normalized.isBlank()) {
                    categories.add(normalized);
                }
            }
        }

        return categories;
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
        assert targetCategories != null : "Target Categories must not be null";

        if (targetCategories.isEmpty()) {
            ui.showMessage(
                    "Please specify a muscle group. Usage: filter <muscle_group> [<muscle_group2> ...]");
            return;
        }

        ArrayList<Workout> filteredList = new ArrayList<>();

        for (Workout workout : workouts.getWorkouts()) {
            if (workout instanceof StrengthWorkout) {
                int shortcutId = dictionary.getShortcutIdFor(workout.getDescription());

                if (shortcutId != -1) {
                    Set<MuscleGroup> muscleGroups = dictionary.getMusclesFor(shortcutId);
                    if (matchesAnyCategory(muscleGroups)) {
                        filteredList.add(workout);
                    }
                }
            }
        }

        ui.showLine();
        String displayCategories = String.join(", ", targetCategories);
        ui.showMessage("Workouts matching category [" + displayCategories + "]:");
        ui.showWorkoutList(filteredList);
        ui.showLine();
    }

    /**
     * Checks if any of the muscle groups in the provided set match any of the target categories.
     * The comparison is case-insensitive to ensure a user-friendly filtering experience.
     *
     * @param groups The set of muscle groups associated with a specific exercise.
     * @return true if a match is found for any target category; false otherwise.
     */
    private boolean matchesAnyCategory(Set<MuscleGroup> groups) {
        for (MuscleGroup group : groups) {
            String groupName = group.displayName().toLowerCase();
            if (targetCategories.contains(groupName)) {
                return true;
            }
        }
        return false;
    }
}
