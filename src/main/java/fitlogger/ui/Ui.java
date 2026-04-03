package fitlogger.ui;

import fitlogger.musclegroup.MuscleGroup;
import fitlogger.workout.StrengthWorkout;
import fitlogger.workout.Workout;
import fitlogger.exercisedictionary.ExerciseDictionary;

import java.util.List;
import java.util.Scanner;

public class Ui {
    private static final String LINE = "-----------------------------------------------------";

    private Scanner in = new Scanner(System.in);

    public String readCommand() {
        return in.nextLine();
    }

    public void showWelcome() {
        String logo = " ______   _   _                                 \n"
                + "|  ____(_) | | |                                \n"
                + "| |__   _| |_| |     ___   __ _  __ _  ___ _ __  \n"
                + "|  __| | | __| |    / _ \\ / _` |/ _` |/ _ \\ '__| \n"
                + "| |    | | |_| |___| (_) | (_| | (_| |  __/ |    \n"
                + "|_|    |_|\\__|______\\___/ \\__, |\\__, |\\___|_|    \n"
                + "                           __/ | __/ |           \n"
                + "                          |___/ |___/            ";
        showMessage(logo);
        showLine();
        showMessage("Welcome to FitLogger!");
        showMessage("Type 'help' to see available commands.");
    }

    public void showGoodbye() {
        showMessage("Goodbye! See you at your next workout!");
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays an error message prefixed with "[ERROR]".
     *
     * @param message The error description to display.
     */
    public void showError(String message) {
        System.out.println("[ERROR] " + message);
    }

    public void showHelpMenu() {
        String helpMessage = "Command Guide:\n"
                + "    help                                           List available commands\n"
                + "    profile view                                   View your profile\n"
                + "    profile set <field> <value>                    Update your profile. "
                + "Available fields: name / weight / height \n"
                + "    add-run <name_or_id> d/<distKm> t/<mins>       Log a run\n"
                + "    add-lift <name_or_id> w/<kg> s/<sets> r/<reps> Log a lift workout\n"
                + "    edit <index> <field>/<value>                   "
                + "Edit field: name/description/weight/sets/reps/distance/duration\n"
                + "    view-database                                  View exercise shortcuts and their IDs\n"
                + "    add-shortcut <lift/run> <ID> <name>            Add a custom exercise shortcut\n"
                + "    view-total-mileage                             View total distance ran across all run workouts\n"
                + "    lastlift <EXERCISE_NAME>                       View most recent lift for an exercise\n"
                + "    view-muscle-groups                             View all available muscle groups\n"
                + "    muscle-groups <shortcut_ID>                    View all muscle groups for a specific exercise\n"
                + "    tag-muscle <shortcut_ID> <muscle>              Tag muscle groups to a shortcut\n"
                + "    untag-muscle <shortcut_ID> <muscle>            Remove muscle group tags\n"
                + "    train <muscle>                                 List exercises targeting that muscle\n"
                + "    history                                        View all logged workouts\n"
                + "    delete <index>                                 Delete workout by number\n"
                + "    search-date <YYYY-MM-DD>                       View workouts completed on a date\n"
                + "    exit                                           Save and close FitLogger";
        showMessage(helpMessage);
    }

    public void showMessageNoNewline(String message) {
        System.out.print(message);
    }

    public void showLine() {
        showMessage(LINE);
    }

    public void printWorkout(Workout workout) {
        showMessage(workout.toString());
    }

    public void showWorkoutList(List<Workout> workouts) {
        if (workouts.isEmpty()) {
            showMessage("No workouts found.");
            return;
        }

        for (int i = 0; i < workouts.size(); i++) {
            showMessageNoNewline(i + 1 + ". ");
            printWorkout(workouts.get(i));
        }
    }

    public void showExerciseDatabase(ExerciseDictionary dictionary) {
        showMessage("Strength Shortcuts:");
        for (java.util.Map.Entry<Integer, String> entry : dictionary.getLiftShortcuts().entrySet()) {
            showMessage("  [" + entry.getKey() + "] -> " + entry.getValue());
        }
        showMessage("");
        showMessage("Run Shortcuts:");
        for (java.util.Map.Entry<Integer, String> entry : dictionary.getRunShortcuts().entrySet()) {
            showMessage("  [" + entry.getKey() + "] -> " + entry.getValue());
        }
        showLine();
    }

    public void showMuscleGroups() {
        showMessage("Here are all available muscle groups: ");
        MuscleGroup[] groups = MuscleGroup.values();
        for (int i = 0; i < groups.length; i++) {
            showMessageNoNewline(groups[i].displayName());
            if (i != groups.length - 1) {
                //don't show comma on the last element
                showMessageNoNewline(", ");
            }
        }
        showMessage("");
    }

    /**
     * Displays the stats of the most recently found {@link StrengthWorkout}.
     *
     * @param lift The most recent strength workout to display.
     */
    public void showLastLift(StrengthWorkout lift) {
        showLine();
        showMessage("Last recorded lift for: " + lift.getDescription());
        showMessage("  Date   : " + lift.getDate());
        showMessage("  Weight : " + lift.getWeight() + "kg");
        showMessage("  Sets   : " + lift.getSets());
        showMessage("  Reps   : " + lift.getReps());
        showLine();
    }

    public void showProfile(String name, double height, double weight) {
        showLine();
        showMessageNoNewline("Name: ");
        String nameToDisplay = (name == null) ? "name not set yet" : name;
        showMessage(nameToDisplay);

        showMessageNoNewline("Height: ");
        String heightToDisplay = (height == -1) ?
                "height not set yet" : String.format("%.2f", height) + "m";
        showMessage(heightToDisplay);

        showMessageNoNewline("Weight: ");
        String weightToDisplay = (weight == -1) ?
                "weight not set yet" : String.format("%.2f", weight) + "kg";
        showMessage(weightToDisplay);
        showLine();
    }
}
