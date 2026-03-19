package fitlogger.workout;

import java.time.LocalDate;

/**
 * Represents a strength-based workout logged by the user.
 */
public class StrengthWorkout extends Workout {
    private double weight;
    private int sets;
    private int reps;

    /**
     * Creates a new StrengthWorkout with the specified details.
     *
     * @param description The name of the exercise (e.g., "Bench Press").
     * @param weight      The weight lifted in kilograms.
     * @param sets        The number of sets completed.
     * @param reps        The number of repetitions per set.
     * @param date        The date the workout was completed.
     */
    public StrengthWorkout(String description, double weight, int sets, int reps, LocalDate date) {
        super(description, date); // Passes the name and date to the Workout parent class
        this.weight = weight;
        this.sets = sets;
        this.reps = reps;
    }

    /**
     * Formats the workout details into a string for saving to a text file.
     * Includes the type [L], done status (1 or 0), date, and lifting stats.
     *
     * @return A formatted string suitable for local storage.
     */
    @Override
    public String toFileFormat() {
        // e.g., L | 0 | Bench Press | 2026-03-13 | 80.5 | 3 | 8
        String status = isDone ? "1" : "0";
        return "L | " + status + " | " + description + " | " + date + " | " + weight + " | " + sets + " | " + reps;
    }

    /**
     * Returns the string representation of the workout for the user interface.
     *
     * @return A formatted string displaying the workout details.
     */
    @Override
    public String toString() {
        // super.toString() handles the "[ ] Bench Press (Date: 2026-03-13)" part
        return "[L]" + super.toString() + " - " + weight + "kg (" + sets + " sets of " + reps + " reps)";
    }
}
