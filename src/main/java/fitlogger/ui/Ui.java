package fitlogger.ui;

import fitlogger.musclegroup.MuscleGroup;
import fitlogger.workout.StrengthWorkout;
import fitlogger.workout.Workout;
import fitlogger.exercisedictionary.ExerciseDictionary;
import fitlogger.workout.RunWorkout;

import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.time.YearMonth;
import java.time.LocalDate;

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
                + "    view-detailed-database                         View exercise shortcuts, IDs, and muscle groups\n"
                + "    add-shortcut <lift/run> <ID> <name>            Add a custom exercise shortcut\n"
                + "    view-total-mileage                             View total distance ran across all run workouts\n"
                + "    lastlift <EXERCISE_NAME>                       View most recent lift for an exercise\n"
                + "    view-muscle-groups                             View all available muscle groups\n"
                + "    muscle-groups <lift_shortcut_ID>               View all muscle groups for a specific exercise\n"
                + "    tag-muscle <lift_shortcut_ID> <muscle>         Tag muscle groups to a shortcut\n"
                + "    untag-muscle <lift_shortcut_ID> <muscle>       Remove muscle group tags\n"
                + "    train <muscle>                                 List exercises targeting that muscle\n"
                + "    history                                        View all logged workouts\n"
                + "    filter <muscle_group>                          Filter workouts by muscle (e.g., filter chest)\n"
                + "    delete <index>                                 Delete workout by number\n"
                + "    search-date <YYYY-MM-DD>                       View workouts completed on a date\n"
                + "    view-calendar <YYYY-MM>                        View active days in a calendar month\n"
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

    public void showExerciseDatabase(ExerciseDictionary dictionary, boolean isDetailed) {
        showMessage("Strength Shortcuts:");
        for (java.util.Map.Entry<Integer, String> entry : dictionary.getLiftShortcuts()
                .entrySet()) {
            int id = entry.getKey();
            showMessageNoNewline("  [" + id + "] -> " + entry.getValue());

            Set<MuscleGroup> muscles = dictionary.getMusclesFor(id);
            if (isDetailed) {
                if (muscles.isEmpty()) {
                    showMessage(" (Muscles: no muscles currently tagged)");
                } else {
                    showMessage(" (Muscles: " + muscles + ")");
                }
            } else {
                showMessage("");
            }
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
                // don't show comma on the last element
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

    /**
     * Displays the personal record entry for a given exercise.
     * Shows weight/sets/reps for strength workouts, distance/duration for runs.
     *
     * @param prWorkout The workout entry representing the personal record.
     */
    public void showPr(Workout prWorkout) {
        showLine();
        showMessage("Personal Record for: " + prWorkout.getDescription());
        showMessage("  Date   : " + prWorkout.getDate());
        if (prWorkout instanceof StrengthWorkout) {
            StrengthWorkout lift = (StrengthWorkout) prWorkout;
            showMessage("  Weight : " + lift.getWeight() + "kg");
            showMessage("  Sets   : " + lift.getSets());
            showMessage("  Reps   : " + lift.getReps());
        } else if (prWorkout instanceof RunWorkout) {
            RunWorkout run = (RunWorkout) prWorkout;
            showMessage("  Distance : " + run.getDistance() + "km");
            showMessage("  Duration : " + run.getDurationMinutes() + " mins");
        }
        showLine();
    }

    public void showProfile(String name, double height, double weight) {
        showLine();
        showMessageNoNewline("Name: ");
        String nameToDisplay = (name == null) ? "name not set yet" : name;
        showMessage(nameToDisplay);

        showMessageNoNewline("Height: ");
        String heightToDisplay =
                (height == -1) ? "height not set yet" : String.format("%.2f", height) + "m";
        showMessage(heightToDisplay);

        showMessageNoNewline("Weight: ");
        String weightToDisplay =
                (weight == -1) ? "weight not set yet" : String.format("%.2f", weight) + "kg";
        showMessage(weightToDisplay);
        showLine();
    }

    public void showCalendar(YearMonth yearMonth, Set<Integer> activeDays) {
        showLine();
        String title = yearMonth.getMonth().name() + " " + yearMonth.getYear();
        showMessage("      " + title);
        showMessage(" Su  Mo  Tu  We  Th  Fr  Sa");

        // Get the first day of the month and its day of the week
        LocalDate firstOfMonth = yearMonth.atDay(1);
        int dayOfWeekValue = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday = 0

        // Print leading spaces
        for (int i = 0; i < dayOfWeekValue; i++) {
            showMessageNoNewline("    ");
        }

        int daysInMonth = yearMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            // Highlight active days with brackets []
            String dayStr = activeDays.contains(day) ? String.format("[%2d]", day)
                    : String.format(" %2d ", day);
            showMessageNoNewline(dayStr);

            // Break line every Saturday
            if ((day + dayOfWeekValue) % 7 == 0) {
                showMessage("");
            }
        }
        showMessage("\n");
        showLine();
    }
}
