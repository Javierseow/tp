package seedu.FitLogger;

import java.time.LocalDate;

public class RunWorkout extends Workout{
    protected String distance;
    protected String duration;

    public RunWorkout(String description, LocalDate date, String distance, String duration) {
        super(description, date);
        this.distance = distance;
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public String getTime() {
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
