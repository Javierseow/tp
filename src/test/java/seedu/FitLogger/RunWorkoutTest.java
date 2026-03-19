package seedu.fitlogger;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import seedu.fitlogger.workout.RunWorkout;
// import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests the functionality of the RunWorkout class, including inheritance
 * from Workout and file string formatting.
 */
public class RunWorkoutTest {

    @Test
    public void testRunWorkoutInitialization() {
        LocalDate date = LocalDate.of(2026, 3, 13);
        RunWorkout run = new RunWorkout("Morning Jog", date, 5, 0.20);

        // Test if fields are assigned correctly
        assertEquals("Morning Jog", run.getDescription(), "Description should match constructor input");
        assertEquals(date, run.getDate(), "Date should match constructor input");
        assertEquals(5, run.getDistance(), "Distance should match constructor input");
        assertEquals(0.2, run.getDuration(), "Time should match constructor input");

        // Test default status
        assertFalse(run.getDoneStatus(), "New workout should have isDone as false");
    }

    @Test
    public void testMarkAsDone() {
        RunWorkout run = new RunWorkout("Sprint", LocalDate.now(), 2, 15);
        run.markAsDone();
        assertTrue(run.getDoneStatus()
                , "isDone should be true after calling markAsDone()");
    }

    @Test
    public void testToFileFormat() {
        LocalDate date = LocalDate.of(2025, 7, 30);
        RunWorkout run = new RunWorkout("Tempo Run", date, 14, 50.3);

        // Check format for uncompleted run
        String expectedUncompleted = "R | 0 | Tempo Run | 2025-07-30 | 14.0 | 50.3";
        assertEquals(expectedUncompleted, run.toFileFormat(), "Uncompleted file format mismatch");

        // Check format after completion
        run.markAsDone();
        String expectedCompleted = "R | 1 | Tempo Run | 2025-07-30 | 14.0 | 50.3";
        assertEquals(expectedCompleted, run.toFileFormat(), "Completed file format mismatch");
    }

    @Test
    public void testInvalidInputHandling() {
        // Example of using fail() - you can expand this when you add validation logic
        try {
            new RunWorkout(null, null, 0, 0);
            // If you eventually add checks for nulls or negative distance,
            // you would uncomment the line below:
            // fail("Should have thrown an exception for invalid inputs");
        } catch (Exception e) {
            // Success: Exception caught
        }
    }
}
