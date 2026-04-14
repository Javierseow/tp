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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link ViewPrCommand}.
 *
 * PR definitions:
 * - Strength: highest weight
 * - Run: shortest duration (fastest time)
 *
 * Note: Ui.showPr() prints duration as raw double (e.g. "30.0 mins"),
 * not formatted to 2 decimal places.
 */
class ViewPrCommandTest {

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

        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        ui = new Ui();
    }

    private String getOutput() {
        return outputStream.toString();
    }

    // ── strength PR ───────────────────────────────────────────────────────────

    @Test
    void execute_singleStrengthWorkout_displaysPr() throws FitLoggerException {
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8,
                LocalDate.of(2026, 3, 1)));

        new ViewPrCommand("Bench Press").execute(storage, workouts, ui, profile);

        String output = getOutput();
        assertTrue(output.contains("Personal Record for: Bench Press"));
        assertTrue(output.contains("80.0kg"));
    }

    @Test
    void execute_multipleStrengthWorkouts_displaysHighestWeight() throws FitLoggerException {
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8,
                LocalDate.of(2026, 3, 1)));
        workouts.addWorkout(new StrengthWorkout("Bench Press", 100.0, 3, 5,
                LocalDate.of(2026, 3, 10)));
        workouts.addWorkout(new StrengthWorkout("Bench Press", 90.0, 4, 6,
                LocalDate.of(2026, 3, 20)));

        new ViewPrCommand("Bench Press").execute(storage, workouts, ui, profile);

        String output = getOutput();
        assertTrue(output.contains("100.0kg"));
        assertFalse(output.contains("80.0kg"));
        assertFalse(output.contains("90.0kg"));
    }

    @Test
    void execute_strengthPr_displaysSetsAndReps() throws FitLoggerException {
        workouts.addWorkout(new StrengthWorkout("Squat", 120.0, 5, 5,
                LocalDate.of(2026, 3, 1)));

        new ViewPrCommand("Squat").execute(storage, workouts, ui, profile);

        String output = getOutput();
        assertTrue(output.contains("120.0kg"));
        assertTrue(output.contains("5"));
    }

    // ── run PR (shortest duration = fastest time) ─────────────────────────────

    @Test
    void execute_singleRunWorkout_displaysPr() throws FitLoggerException {
        workouts.addWorkout(new RunWorkout("Easy Run", LocalDate.of(2026, 3, 1), 5.0, 30.0));

        new ViewPrCommand("Easy Run").execute(storage, workouts, ui, profile);

        String output = getOutput();
        assertTrue(output.contains("Personal Record for: Easy Run"));
        // Ui.showPr() prints raw double: "30.0 mins"
        assertTrue(output.contains("30.0 mins"));
    }

    @Test
    void execute_multipleRunWorkouts_displaysShortestDuration() throws FitLoggerException {
        // Entry 1: slower time (should NOT be PR)
        workouts.addWorkout(new RunWorkout("Easy Run", LocalDate.of(2026, 4, 10), 5.0, 60.0));
        // Entry 2: faster time (should BE PR)
        workouts.addWorkout(new RunWorkout("Easy Run", LocalDate.of(2026, 4, 10), 5.0, 20.0));

        new ViewPrCommand("Easy Run").execute(storage, workouts, ui, profile);

        String output = getOutput();
        // PR is 20.0 mins (fastest), not 60.0 mins
        assertTrue(output.contains("20.0 mins"));
        assertFalse(output.contains("60.0 mins"));
    }

    @Test
    void execute_runPr_displaysDistanceAndDuration() throws FitLoggerException {
        workouts.addWorkout(new RunWorkout("Tempo Run", LocalDate.of(2026, 3, 1), 10.0, 55.0));

        new ViewPrCommand("Tempo Run").execute(storage, workouts, ui, profile);

        String output = getOutput();
        // Ui.showPr() prints raw doubles: "10.0km" and "55.0 mins"
        assertTrue(output.contains("10.0km"));
        assertTrue(output.contains("55.0 mins"));
    }

    // ── no match ─────────────────────────────────────────────────────────────

    @Test
    void execute_noMatchingExercise_showsNotFoundMessage() throws FitLoggerException {
        workouts.addWorkout(new StrengthWorkout("Squat", 100.0, 5, 5,
                LocalDate.of(2026, 3, 1)));

        new ViewPrCommand("Bench Press").execute(storage, workouts, ui, profile);

        assertTrue(getOutput().contains("No record found for exercise: Bench Press"));
    }

    @Test
    void execute_emptyWorkoutList_showsNotFoundMessage() {
        new ViewPrCommand("Bench Press").execute(storage, workouts, ui, profile);

        assertTrue(getOutput().contains("No record found for exercise: Bench Press"));
    }

    @Test
    void execute_blankExerciseId_showsUsageHint() {
        new ViewPrCommand("   ").execute(storage, workouts, ui, profile);

        assertTrue(getOutput().contains("Usage: pr <EXERCISE_NAME>"));
    }

    // ── case insensitivity ────────────────────────────────────────────────────

    @Test
    void execute_caseInsensitiveMatch_findsPr() throws FitLoggerException {
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8,
                LocalDate.of(2026, 3, 1)));

        new ViewPrCommand("bench press").execute(storage, workouts, ui, profile);

        assertTrue(getOutput().contains("Personal Record for: Bench Press"));
    }
}
