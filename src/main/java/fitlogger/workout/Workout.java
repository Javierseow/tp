package fitlogger.workout;

import java.time.LocalDate;

/**
 * Represents an abstract workout in the FitLogger.command.FitLogger application.
 * This class serves as a base for specific workout types and contains
 * shared logic for tracking completion status and dates.
 */
public abstract class Workout {

    /** The description or name of the workout activity. */
    protected String description;

    /** The date when the workout was performed or is scheduled for. */
    protected LocalDate date;

    /** Tracks whether the workout has been completed. */
    protected boolean isDone;

    /**
     * Initializes a new Workout with a description and date.
     * The initial status of the workout is set to not done.
     *
     * @param description A short summary of the workout.
     * @param date        The date of the workout.
     */
    public Workout(String description, LocalDate date) {
        this.description = description;
        this.date = date;
        this.isDone = false;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public boolean getDoneStatus() {
        return isDone;
    }

    /**
     * Returns a visual indicator of the workout's completion status.
     *
     * @return "[X]" if completed, "[ ]" otherwise.
     */
    public String getStatusIcon() {
        return (isDone ? "[X]" : "[ ]");
    }

    /**
     * Updates the workout status to completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Updates the workout status to incomplete.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Formats the workout data into a standardized string for file storage.
     * Each child class must implement this to ensure its specific data
     * (e.g., distance for runs) is saved correctly.
     *
     * @return A pipe-separated string representing the workout's data.
     */
    public abstract String toFileFormat();

    /**
     * Returns a string representation of the workout for display to the user.
     *
     * @return A formatted string containing status, description, and date.
     */
    @Override
    public String toString() {
        return getStatusIcon() + " " + description + " (Date: " + date + ")";
    }
}
