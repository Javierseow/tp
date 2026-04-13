package fitlogger.workout;

import fitlogger.exception.FitLoggerException;

import java.time.LocalDate;

/**
 * Represents an abstract workout in the FitLogger.command.FitLogger application.
 * This class serves as a base for specific workout types and contains
 * shared logic for storing a workout name and date.
 */
public abstract class Workout {

    /**
     * The stored workout name.
     *
     * <p>The field name remains {@code description} to preserve the existing storage and API
     * terminology used throughout the codebase.
     */
    protected String description;

    /** The date when the workout was performed or is scheduled for. */
    protected LocalDate date;

    /**
     * Initializes a new Workout with a name and date.
     *
     * @param description The workout name shown to users.
     * @param date        The date of the workout.
     * @throws FitLoggerException if the name is null or blank.
     */
    public Workout(String description, LocalDate date) throws FitLoggerException {
        setDescription(description);
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Updates the workout name.
     *
     * @param description New non-blank workout name.
     * @throws FitLoggerException if the name is null or blank.
     */
    public void setDescription(String description) throws FitLoggerException {
        if (description == null || description.isBlank()) {
            throw new FitLoggerException("Workout name cannot be blank.");
        }
        this.description = description.trim();
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
     * @return A formatted string containing the workout name and date.
     */
    @Override
    public String toString() {
        return description + " (Date: " + date + ")";
    }
}
