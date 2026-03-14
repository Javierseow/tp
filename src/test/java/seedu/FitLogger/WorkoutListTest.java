package seedu.FitLogger;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class WorkoutListTest {
    private WorkoutList list;
    private RunWorkout run;
    private StrengthWorkout lift;

    public WorkoutListTest() {
        list = new WorkoutList();
        run = new RunWorkout("Morning Run", LocalDate.now(), 5.0, 30.0);
        lift = new StrengthWorkout("Bench Press", 60.0, 3, 10, LocalDate.now());
    }

    @Test
    public void addWorkout_validWorkouts_sizeIncreases() {
        list.addWorkout(run);
        list.addWorkout(lift);
        assertEquals(2, list.getSize());
        assertEquals(run, list.getWorkout(0));
    }

    @Test
    public void deleteWorkout_validIndex_sizeDecreases() {
        list.addWorkout(run);
        list.deleteWorkout(0);
        assertTrue(list.isEmpty());
    }

    @Test
    public void markDone_validIndex_changesStatus() {
        list.addWorkout(run);
        list.markDone(0);
        // Assuming your Workout class has an isDone() method
        assertTrue(list.getWorkout(0).getDoneStatus());
    }

    @Test
    public void findWorkout_matchingKeyword_returnsTrue() {
        list.addWorkout(run); // "Morning Run"
        assertTrue(list.findWorkout(0, "Morning"));
        assertFalse(list.findWorkout(0, "Evening"));
    }

    @Test
    public void getWorkouts_returnsCorrectList() {
        list.addWorkout(run);
        assertEquals(1, list.getWorkouts().size());
        assertEquals(run, list.getWorkouts().get(0));
    }
}
