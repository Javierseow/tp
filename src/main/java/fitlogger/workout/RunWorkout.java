package fitlogger.workout;

import fitlogger.exception.FitLoggerException;

import java.time.LocalDate;

/**
 * Represents a running workout logged by the user.
 *
 * <p>Distance is stored in kilometres. Duration is stored in <em>minutes</em>
 * as a {@code double} to allow fractional values (e.g., 25.5 for 25 min 30 sec).
 */
public class RunWorkout extends Workout {
    private static final double MAX_DISTANCE_KM = 1000.0;
    private static final double MAX_DURATION_MINS = 14400.0; // 10 days in minutes
    /** Distance covered, in kilometres. */
    protected double distance;

    /** Duration of the run, in minutes. */
    protected double durationMinutes;

    /**
     * Creates a new RunWorkout.
     *
     * @param description     A short label for this run (e.g., "Morning jog").
     * @param date            The date the run was performed.
     * @param distance        Distance in kilometres (must be positive).
     * @param durationMinutes Duration in minutes (must be positive).
     * @throws FitLoggerException if distance or duration is not finite and positive,
     *                            or if description is blank.
     */
    public RunWorkout(String description, LocalDate date, double distance, double durationMinutes)
            throws FitLoggerException {
        super(description, date);
        setDistance(distance);
        setDurationMinutes(durationMinutes);
    }

    public double getDistance() {
        return distance;
    }

    /**
     * Updates the run distance in kilometres. Maximum allowed distance is 1000 km.
     *
     * @param distance New distance, must be greater than 0.
     * @throws FitLoggerException if distance is not finite, not positive, or exceeds 1000 km.
     */
    public void setDistance(double distance) throws FitLoggerException {
        if (!Double.isFinite(distance) || distance <= 0) {
            throw new FitLoggerException("Distance must be a positive number.");
        }
        if (distance > MAX_DISTANCE_KM) {
            throw new FitLoggerException("Distance cannot exceed " + MAX_DISTANCE_KM + "km.");
        }
        this.distance = distance;
    }

    /** Returns the run duration in minutes. */
    public double getDurationMinutes() {
        return durationMinutes;
    }

    /**
     * Updates the run duration in minutes. Maximum allowed duration is 14400 mins (10 days).
     *
     * @param durationMinutes New duration, must be greater than 0.
     * @throws FitLoggerException if duration is not finite or not positive.
     */
    public void setDurationMinutes(double durationMinutes) throws FitLoggerException {
        if (!Double.isFinite(durationMinutes) || durationMinutes <= 0) {
            throw new FitLoggerException("Duration must be a positive number.");
        }
        if (durationMinutes > MAX_DURATION_MINS) {
            throw new FitLoggerException("Duration cannot exceed 10 days (14400 minutes).");
        }
        this.durationMinutes = durationMinutes;
    }

    /**
     * Formats the workout for file storage.
     *
     * <p>Format: {@code R | <description> | <date> | <distance> | <durationMinutes>}
     */
    @Override
    public String toFileFormat() {
        return "R | " + description + " | " + date + " | " + distance + " | " + durationMinutes;
    }

    @Override
    public String toString() {
        return "[Run] " + super.toString()
                + " (Distance: " + distance + "km, Duration: " + durationMinutes + " mins)";
    }
}
