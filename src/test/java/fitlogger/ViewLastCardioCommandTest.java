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
 * Tests for {@link ViewLastCardioCommand}.
 */
class ViewLastCardioCommandTest {

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

    // ── matching ─────────────────────────────────────────────────────────────

    @Test
    void execute_singleMatchingRun_displaysStats() throws FitLoggerException {
        workouts.addWorkout(new RunWorkout("Easy Run", LocalDate.of(2026, 3, 1), 5.0, 30.0));

        new ViewLastCardioCommand("Easy Run").execute(storage, workouts, ui, profile);

        String output = getOutput();
        assertTrue(output.contains("Easy Run"));
        assertTrue(output.contains("5.0") || output.contains("5.00"));
        assertTrue(output.contains("30.0") || output.contains("30.00"));
    }

    @Test
    void execute_multipleMatchingRuns_displaysLastOne() throws FitLoggerException {
        workouts.addWorkout(new RunWorkout("Easy Run", LocalDate.of(2026, 3, 1), 5.0, 30.0));
        workouts.addWorkout(new RunWorkout("Easy Run", LocalDate.of(2026, 3, 10), 10.0, 60.0));

        new ViewLastCardioCommand("Easy Run").execute(storage, workouts, ui, profile);

        String output = getOutput();
        assertTrue(output.contains("10.0") || output.contains("10.00"));
        assertFalse(output.contains("5.0km") || output.contains("5.00km"));
    }

    @Test
    void execute_caseInsensitiveMatch_displaysStats() throws FitLoggerException {
        workouts.addWorkout(new RunWorkout("Easy Run", LocalDate.of(2026, 3, 1), 5.0, 30.0));

        new ViewLastCardioCommand("easy run").execute(storage, workouts, ui, profile);

        assertTrue(getOutput().contains("Easy Run"));
    }

    @Test
    void execute_displaysDateDistanceAndDuration() throws FitLoggerException {
        workouts.addWorkout(new RunWorkout("Tempo Run", LocalDate.of(2026, 4, 1), 8.0, 40.0));

        new ViewLastCardioCommand("Tempo Run").execute(storage, workouts, ui, profile);

        String output = getOutput();
        assertTrue(output.contains("2026-04-01"));
        assertTrue(output.contains("8.0") || output.contains("8.00"));
        assertTrue(output.contains("40.0") || output.contains("40.00"));
    }

    // ── no match ─────────────────────────────────────────────────────────────

    @Test
    void execute_noMatchingRun_showsNotFoundMessage() throws FitLoggerException {
        workouts.addWorkout(new RunWorkout("Tempo Run", LocalDate.of(2026, 3, 1), 5.0, 25.0));

        new ViewLastCardioCommand("Easy Run").execute(storage, workouts, ui, profile);

        assertTrue(getOutput().contains("No record found for exercise: Easy Run"));
    }

    @Test
    void execute_emptyWorkoutList_showsNotFoundMessage() {
        new ViewLastCardioCommand("Easy Run").execute(storage, workouts, ui, profile);

        assertTrue(getOutput().contains("No record found for exercise: Easy Run"));
    }

    @Test
    void execute_blankExerciseId_showsUsageHint() {
        new ViewLastCardioCommand("   ").execute(storage, workouts, ui, profile);

        assertTrue(getOutput().contains("Usage: lastcardio <EXERCISE_NAME_OR_ID>"));
    }

    // ── strength workouts ignored ─────────────────────────────────────────────

    @Test
    void execute_strengthWorkoutWithSameName_isIgnored() throws FitLoggerException {
        workouts.addWorkout(new StrengthWorkout("Easy Run", 50.0, 3, 10,
                LocalDate.of(2026, 3, 1)));

        new ViewLastCardioCommand("Easy Run").execute(storage, workouts, ui, profile);

        assertTrue(getOutput().contains("No record found for exercise: Easy Run"));
    }

    // ── most recent is returned ───────────────────────────────────────────────

    @Test
    void execute_shorterDistanceMoreRecent_returnsMoreRecentEntry() throws FitLoggerException {
        workouts.addWorkout(new RunWorkout("Easy Run", LocalDate.of(2026, 1, 1), 21.0, 120.0));
        workouts.addWorkout(new RunWorkout("Easy Run", LocalDate.of(2026, 4, 1), 3.0, 20.0));

        new ViewLastCardioCommand("Easy Run").execute(storage, workouts, ui, profile);

        String output = getOutput();
        assertTrue(output.contains("3.0") || output.contains("3.00"));
        assertFalse(output.contains("21.0km") || output.contains("21.00km"));
    }
}
