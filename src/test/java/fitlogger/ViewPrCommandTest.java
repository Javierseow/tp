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
 * Tests for {@link ViewPrCommand}.
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

        // Redirect System.out so we can assert on printed output
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
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 1)));

        new ViewPrCommand("Bench Press").execute(storage, workouts, ui, profile);

        String output = getOutput();
        assertTrue(output.contains("Personal Record for: Bench Press"));
        assertTrue(output.contains("80.0kg"));
    }

    @Test
    void execute_multipleStrengthWorkouts_displaysHighestWeight() throws FitLoggerException {
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 1)));
        workouts.addWorkout(new StrengthWorkout("Bench Press", 100.0, 3, 5, LocalDate.of(2026, 3, 10)));
        workouts.addWorkout(new StrengthWorkout("Bench Press", 90.0, 4, 6, LocalDate.of(2026, 3, 20)));

        new ViewPrCommand("Bench Press").execute(storage, workouts, ui, profile);

        String output = getOutput();
        // PR should be 100kg (highest), not 90kg (most recent)
        assertTrue(output.contains("100.0kg"));
        assertFalse(output.contains("80.0kg"));
        assertFalse(output.contains("90.0kg"));
    }

    @Test
    void execute_strengthPr_displaysSetsAndReps() throws FitLoggerException {
        workouts.addWorkout(new StrengthWorkout("Squat", 120.0, 5, 5, LocalDate.of(2026, 3, 1)));

        new ViewPrCommand("Squat").execute(storage, workouts, ui, profile);

        String output = getOutput();
        assertTrue(output.contains("120.0kg"));
        assertTrue(output.contains("5"));
    }

    // ── run PR ────────────────────────────────────────────────────────────────

    @Test
    void execute_singleRunWorkout_displaysPr() throws FitLoggerException {
        workouts.addWorkout(new RunWorkout("Easy Run", LocalDate.of(2026, 3, 1), 5.0, 30.0));

        new ViewPrCommand("Easy Run").execute(storage, workouts, ui, profile);

        String output = getOutput();
        assertTrue(output.contains("Personal Record for: Easy Run"));
        assertTrue(output.contains("5.0km"));
    }

    @Test
    void execute_multipleRunWorkouts_displaysLongestDistance() throws FitLoggerException {
        workouts.addWorkout(new RunWorkout("Easy Run", LocalDate.of(2026, 3, 1), 5.0, 30.0));
        workouts.addWorkout(new RunWorkout("Easy Run", LocalDate.of(2026, 3, 10), 21.1, 120.0));
        workouts.addWorkout(new RunWorkout("Easy Run", LocalDate.of(2026, 3, 20), 10.0, 60.0));

        new ViewPrCommand("Easy Run").execute(storage, workouts, ui, profile);

        String output = getOutput();
        // PR should be 21.1km (longest), not 10.0km (most recent)
        assertTrue(output.contains("21.1km"));
        assertFalse(output.contains("5.0km"));
        assertFalse(output.contains("10.0km"));
    }

    // ── no match ─────────────────────────────────────────────────────────────

    @Test
    void execute_noMatchingExercise_showsNotFoundMessage() throws FitLoggerException {
        workouts.addWorkout(new StrengthWorkout("Squat", 100.0, 5, 5, LocalDate.of(2026, 3, 1)));

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
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 1)));

        new ViewPrCommand("bench press").execute(storage, workouts, ui, profile);

        assertTrue(getOutput().contains("Personal Record for: Bench Press"));
    }

    // ── mixed workout types ───────────────────────────────────────────────────

    @Test
    void execute_mixedWorkoutTypes_onlyMatchesCorrectType() throws FitLoggerException {
        workouts.addWorkout(new RunWorkout("Bench Press", LocalDate.of(2026, 3, 1), 5.0, 30.0));
        workouts.addWorkout(new StrengthWorkout("Bench Press", 80.0, 3, 8, LocalDate.of(2026, 3, 5)));

        new ViewPrCommand("Bench Press").execute(storage, workouts, ui, profile);

        // Both match by name — whichever has a higher comparable value wins
        // Run: 5.0 distance, Lift: 80.0 weight — lift wins
        String output = getOutput();
        assertTrue(output.contains("80.0kg"));
    }
}
