package seedu.fitlogger.workout;

import java.time.LocalDate;

/**
 * Represents a running workout.
 * Inherits from the Workout class and adds distance-specific tracking.
 */
public class Running extends Workout {
    protected double distance;

    /**
     * Initializes a new Running workout with distance, description, and date.
     *
     * @param description A short summary of the run (e.g., "Interval training").
     * @param date        The date the run occurred.
     * @param distance    The distance covered in kilometers.
     */
    public Running(String description, LocalDate date, double distance) {
        // Calls the parent (Workout) constructor
        super(description, date);
        this.distance = distance;
    }

    /**
     * Formats the running data for file storage.
     * Format: R | status | description | date | distance
     *
     * @return A formatted string for saving to a text file.
     */
    @Override
    public String toFileFormat() {
        return "R | " + (isDone ? "1" : "0") + " | " + description + " | " + date + " | " + distance;
    }

    /**
     * Returns a formatted string for the CLI display.
     *
     * @return String representation of the run.
     */
    @Override
    public String toString() {
        return "[R]" + super.toString() + " - Distance: " + distance + "km";
    }
}
