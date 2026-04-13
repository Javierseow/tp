package fitlogger;

import fitlogger.exception.FitLoggerException;
import fitlogger.workout.RunWorkout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests the RunWorkout class, specifically checking the new realistic
 * limits for distance and duration.
 */
class RunWorkoutTest {
    private RunWorkout run;
    private LocalDate testDate;

    @BeforeEach
    public void setUp() throws FitLoggerException {
        testDate = LocalDate.of(2026, 3, 13);
        run = new RunWorkout("Morning Jog", testDate, 5.0, 30.0);
    }

    @Test
    public void constructor_validInput_setsAllFields() {
        assertEquals("Morning Jog", run.getDescription());
        assertEquals(5.0, run.getDistance(), 0.001);
        assertEquals(30.0, run.getDurationMinutes(), 0.001);
    }

    // --- POSITIVE LIMIT TESTS ---

    @Test
    public void setDistance_exactlyAtLimit_updatesCorrectly() throws FitLoggerException {
        run.setDistance(1000.0);
        assertEquals(1000.0, run.getDistance(), 0.001);
    }

    @Test
    public void setDurationMinutes_exactlyAtLimit_updatesCorrectly() throws FitLoggerException {
        run.setDurationMinutes(14400.0);
        assertEquals(14400.0, run.getDurationMinutes(), 0.001);
    }

    // --- NEGATIVE LIMIT TESTS ---

    @Test
    public void constructor_distanceExceedsLimit_throwsException() {
        assertThrows(FitLoggerException.class, () -> {
            new RunWorkout("Impossible Run", testDate, 1000.1, 60.0);
        });
    }

    @Test
    public void constructor_durationExceedsLimit_throwsException() {
        assertThrows(FitLoggerException.class, () -> {
            new RunWorkout("Infinity Run", testDate, 10.0, 14400.1);
        });
    }

    @Test
    public void setDistance_exceedsLimit_throwsException() {
        assertThrows(FitLoggerException.class, () -> {
            run.setDistance(1000.01);
        });
    }

    @Test
    public void setDurationMinutes_exceedsLimit_throwsException() {
        assertThrows(FitLoggerException.class, () -> {
            run.setDurationMinutes(14401.0);
        });
    }

    @Test
    public void setDistance_negativeValue_throwsException() {
        assertThrows(FitLoggerException.class, () -> {
            run.setDistance(-1.0);
        });
    }

    // --- FORMATTING TESTS ---

    @Test
    public void toFileFormat_standardInput_matchesExpectedString() {
        String expected = "R | Morning Jog | 2026-03-13 | 5.0 | 30.0";
        assertEquals(expected, run.toFileFormat());
    }
}