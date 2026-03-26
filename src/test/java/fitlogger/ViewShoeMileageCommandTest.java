package fitlogger;

import fitlogger.exception.FitLoggerException;
import fitlogger.workout.RunWorkout;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ViewShoeMileageCommandTest {

    @Test
    public void execute_emptyWorkoutList_showsZeroMileage() {
        double totalMileage = 0;
        assertEquals("Your total distance ran is: 0.00km",
                "Your total distance ran is: " + String.format("%.2fkm", totalMileage));
    }

    @Test
    public void execute_singleRunWorkout_correctMileage() throws FitLoggerException {
        RunWorkout run = new RunWorkout("Morning jog", LocalDate.parse("2026-03-13"), 5.0, 30.0);
        double totalMileage = run.getDistance();
        assertEquals("Your total distance ran is: 5.00km",
                "Your total distance ran is: " + String.format("%.2fkm", totalMileage));
    }

    @Test
    public void execute_multipleRunWorkouts_correctMileage() throws FitLoggerException {
        RunWorkout run1 = new RunWorkout("Morning jog", LocalDate.parse("2026-03-13"), 5.0, 30.0);
        RunWorkout run2 = new RunWorkout("Evening run", LocalDate.parse("2026-03-14"), 3.75, 20.0);
        RunWorkout run3 = new RunWorkout("Long run", LocalDate.parse("2026-03-15"), 10.0, 60.0);
        double totalMileage = run1.getDistance() + run2.getDistance() + run3.getDistance();
        assertEquals("Your total distance ran is: 18.75km",
                "Your total distance ran is: " + String.format("%.2fkm", totalMileage));
    }

    @Test
    public void execute_decimalDistance_formatsToTwoDecimalPlaces() throws FitLoggerException {
        RunWorkout run = new RunWorkout("Trail run", LocalDate.parse("2026-03-13"), 4.1, 25.0);
        double totalMileage = run.getDistance();
        assertEquals("Your total distance ran is: 4.10km",
                "Your total distance ran is: " + String.format("%.2fkm", totalMileage));
    }

    @Test
    public void execute_largeDistance_correctMileage() throws FitLoggerException {
        RunWorkout run = new RunWorkout("Ultra marathon", LocalDate.parse("2026-03-13"), 100.0, 600.0);
        double totalMileage = run.getDistance();
        assertEquals("Your total distance ran is: 100.00km",
                "Your total distance ran is: " + String.format("%.2fkm", totalMileage));
    }

    @Test
    public void execute_smallDistance_correctMileage() throws FitLoggerException {
        RunWorkout run = new RunWorkout("Short jog", LocalDate.parse("2026-03-13"), 0.5, 5.0);
        double totalMileage = run.getDistance();
        assertEquals("Your total distance ran is: 0.50km",
                "Your total distance ran is: " + String.format("%.2fkm", totalMileage));
    }
}