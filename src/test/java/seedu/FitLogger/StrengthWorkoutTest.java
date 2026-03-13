package seedu.FitLogger;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StrengthWorkoutTest {

    @Test
    public void toFileFormat_validInputs_correctlyFormattedString() {
        LocalDate testDate = LocalDate.parse("2026-03-13");
        StrengthWorkout workout = new StrengthWorkout("Bench Press", 80.5, 3, 8, testDate);

        String expected = "L | 0 | Bench Press | 2026-03-13 | 80.5 | 3 | 8";
        assertEquals(expected, workout.toFileFormat());
    }

    @Test
    public void toString_validInputs_correctlyFormattedString() {
        LocalDate testDate = LocalDate.parse("2026-03-13");
        StrengthWorkout workout = new StrengthWorkout("Squat", 100.0, 5, 5, testDate);

        String expected = "[L][ ] Squat (Date: 2026-03-13) - 100.0kg (5 sets of 5 reps)";
        assertEquals(expected, workout.toString());
    }
}
