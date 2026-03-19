package seedu.fitlogger.workout;

import java.time.LocalDate;

public class RunWorkout extends Workout{
    protected double distance;
    protected double duration;

    public RunWorkout(String description, LocalDate date, double distance, double duration) {
        super(description, date);
        this.distance = distance;
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public double getDuration() {
        return duration;
    }

    @Override
    public String toFileFormat() {
        return "R | " + (isDone ? "1" : "0") + " | " + description + " | " + date + " | " + distance + " | " + duration;
    }

    @Override
    public String toString() {
        return "[Run] " + super.toString() + " (Distance: " + distance + ", Time: " + duration + ")";
    }
}
