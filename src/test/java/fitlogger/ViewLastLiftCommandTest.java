package fitlogger.command;

import fitlogger.exception.FitLoggerException;
import fitlogger.profile.UserProfile;
import fitlogger.storage.Storage;
import fitlogger.ui.Ui;
import fitlogger.workout.RunWorkout;
import fitlogger.workout.StrengthWorkout;
import fitlogger.workoutlist.WorkoutList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests for {@link ViewLastLiftCommand}.
 */
class ViewLastLiftCommandTest {

    private WorkoutList workouts;
    private Ui ui;
    private Storage storage;
    private UserProfile profile;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        workouts = new WorkoutList();
        storage = new Storage();
        profile = new UserProfile();

        // Redirect System.out so we can assert on printed output
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        ui = new Ui();
    }

    private String getOutput() {
        return outputStream.toString();
    }

    // ── matching ─────────────────────────────────────────────────────────────

    @Test
    void execute_singleMatchingLift_displaysStats() throws FitLoggerException {
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 1)));

        new ViewLastLiftCommand("Bench Press").execute(storage, workouts, ui, profile);

        String output = getOutput();
        assertTrue(output.contains("Bench Press"));
        assertTrue(output.contains("80.0kg"));
        assertTrue(output.contains("3"));
        assertTrue(output.contains("8"));
    }

    @Test
    void execute_multipleMatchingLifts_displaysLastOne() throws FitLoggerException {
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 1)));
        workouts.addWorkout(new StrengthWorkout("Bench Press", 90.0, 4, 6, LocalDate.of(2026, 3, 10)));

        new ViewLastLiftCommand("Bench Press").execute(storage, workouts, ui, profile);

        String output = getOutput();
        // Most recent entry should be shown — 90kg not 80kg
        assertTrue(output.contains("90.0kg"));
        assertFalse(output.contains("80.0kg"));
    }

    @Test
    void execute_caseInsensitiveMatch_displaysStats() throws FitLoggerException {
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 1)));

        new ViewLastLiftCommand("bench press").execute(storage, workouts, ui, profile);

        assertTrue(getOutput().contains("Bench Press"));
    }

    // ── no match ─────────────────────────────────────────────────────────────

    @Test
    void execute_noMatchingExercise_showsNotFoundMessage() throws FitLoggerException {
        workouts.addWorkout(new StrengthWorkout("Squat", 100.0, 5, 5, LocalDate.of(2026, 3, 1)));

        new ViewLastLiftCommand("Bench Press").execute(storage, workouts, ui, profile);

        assertTrue(getOutput().contains("No record found for exercise: Bench Press"));
    }

    @Test
    void execute_emptyWorkoutList_showsNotFoundMessage() {
        new ViewLastLiftCommand("Bench Press").execute(storage, workouts, ui, profile);

        assertTrue(getOutput().contains("No record found for exercise: Bench Press"));
    }

    @Test
    void execute_blankExerciseId_showsUsageHint() {
        new ViewLastLiftCommand("   ").execute(storage, workouts, ui, profile);

        assertTrue(getOutput().contains("Usage: lastlift <EXERCISE_NAME>"));
    }

    // ── run workouts ignored ──────────────────────────────────────────────────

    @Test
    void execute_runWorkoutWithSameName_isIgnored() throws FitLoggerException {
        workouts.addWorkout(new RunWorkout("Bench Press", LocalDate.of(2026, 3, 1), 5.0, 30.0));

        new ViewLastLiftCommand("Bench Press").execute(storage, workouts, ui, profile);

        // Run workouts should not be matched — not-found message expected
        assertTrue(getOutput().contains("No record found for exercise: Bench Press"));
    }

    // ── most recent is returned (not highest weight) ──────────────────────────

    @Test
    void execute_lowerWeightMoreRecent_returnsMoreRecentEntry() throws FitLoggerException {
        workouts.addWorkout(new StrengthWorkout("Deadlift", 180.0, 3, 5, LocalDate.of(2026, 1, 1)));
        workouts.addWorkout(new StrengthWorkout("Deadlift", 120.0, 3, 5, LocalDate.of(2026, 4, 1)));

        new ViewLastLiftCommand("Deadlift").execute(storage, workouts, ui, profile);

        // Should return the most recent (120kg), not the heaviest (180kg)
        String output = getOutput();
        assertTrue(output.contains("120.0kg"));
        assertFalse(output.contains("180.0kg"));
    }
}
