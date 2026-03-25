package fitlogger;

import fitlogger.command.AddWorkoutCommand;
import fitlogger.command.Command;
import fitlogger.exception.FitLoggerException;
import fitlogger.parser.Parser;
import fitlogger.storage.Storage;
import fitlogger.workout.Workout;
import fitlogger.workout.RunWorkout;
import fitlogger.workout.StrengthWorkout;
import fitlogger.workoutlist.WorkoutList;
import fitlogger.ui.Ui;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParserTest {

    private WorkoutList workouts;
    private Storage storage;
    private TestUi ui;

    @BeforeEach
    void setUp() {
        workouts = new WorkoutList();
        storage = new Storage();
        ui = new TestUi();
    }

    // ── add-lift: happy path ──────────────────────────────────────────────

    @Test
    void addLift_validInput_addsWorkout() throws FitLoggerException {
        Command cmd = Parser.parse("add-lift Bench Press w/80.5 s/3 r/8", workouts);

        assertTrue(cmd instanceof AddWorkoutCommand, "Expected AddWorkoutCommand for add-lift");

        cmd.execute(storage, workouts, ui);

        assertEquals(1, workouts.getSize());
        assertTrue(workouts.getWorkoutAtIndex(0) instanceof StrengthWorkout);

        StrengthWorkout logged = (StrengthWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals("Bench Press", logged.getDescription());
        assertEquals(80.5, logged.getWeight(), 0.001);
        assertEquals(3, logged.getSets());
        assertEquals(8, logged.getReps());
    }

    @Test
    void addLift_zeroWeight_isAllowed() throws FitLoggerException {
        // Bodyweight exercises log w/0
        Command cmd = Parser.parse("add-lift Pull-up w/0 s/3 r/10", workouts);
        cmd.execute(storage, workouts, ui);
        StrengthWorkout logged = (StrengthWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals(0.0, logged.getWeight(), 0.001);
    }

    // ── add-lift: error cases ─────────────────────────────────────────────

    @Test
    void addLift_missingArgs_throwsException() {
        FitLoggerException ex =
                assertThrows(FitLoggerException.class, () -> Parser.parse("add-lift", workouts));
        assertTrue(ex.getMessage().toLowerCase().contains("missing"),
                "Error should mention missing arguments");
    }

    @Test
    void addLift_missingReps_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-lift Squat w/100 s/5", workouts));
        assertTrue(
                ex.getMessage().toLowerCase().contains("invalid format")
                        || ex.getMessage().toLowerCase().contains("usage"),
                "Error should describe the correct format");
    }

    @Test
    void addLift_nonNumericWeight_throwsException() {
        assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-lift Squat w/heavy s/3 r/5", workouts));
    }

    @Test
    void addLift_nonIntegerSets_throwsException() {
        assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-lift Squat w/100 s/3.5 r/5", workouts));
    }

    @Test
    void addLift_negativeWeight_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-lift Squat w/-10 s/3 r/5", workouts));
        assertTrue(ex.getMessage().toLowerCase().contains("negative"),
                "Error should mention negative weight");
    }

    @Test
    void addLift_zeroSets_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-lift Squat w/100 s/0 r/5", workouts));
        assertTrue(ex.getMessage().toLowerCase().contains("sets"), "Error should mention sets");
    }

    @Test
    void addLift_zeroReps_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-lift Squat w/100 s/3 r/0", workouts));
        assertTrue(ex.getMessage().toLowerCase().contains("reps"), "Error should mention reps");
    }

    // ── add-lift: delimiter injection ────────────────────────────────────

    @Test
    void addLift_pipeInName_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-lift Bad|Name w/80 s/3 r/8", workouts));
        assertTrue(ex.getMessage().contains("|"), "Error should call out the pipe character");
    }

    @Test
    void addLift_slashInName_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-lift Bad/Name w/80 s/3 r/8", workouts));
        assertTrue(ex.getMessage().contains("/"), "Error should call out the slash character");
    }

    // ── add-run: error cases (previously guarded only by assert) ─────────────

    @Test
    void addRun_missingArgs_throwsException() {
        FitLoggerException ex =
                assertThrows(FitLoggerException.class, () -> Parser.parse("add-run", workouts));
        assertTrue(ex.getMessage().toLowerCase().contains("missing"),
                "Error should mention missing arguments");
    }

    @Test
    void addRun_missingFlag_throwsException() {
        assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-run Morning Jog d/5.0", workouts));
    }

    @Test
    void addRun_nonNumericDistance_throwsException() {
        assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-run Morning Jog d/far t/30", workouts));
    }

    @Test
    void addRun_nonNumericDuration_throwsException() {
        assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-run Morning Jog d/5.0 t/long", workouts));
    }

    @Test
    void addRun_negativeDistance_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-run Morning Jog d/-5 t/30", workouts));
        assertTrue(ex.getMessage().toLowerCase().contains("positive"),
                "Error should mention positive distance");
    }

    @Test
    void addRun_zeroDuration_throwsException() {
        assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-run Morning Jog d/5.0 t/0", workouts));
    }

    @Test
    void addRun_pipeInName_throwsException() {
        FitLoggerException ex = assertThrows(FitLoggerException.class,
                () -> Parser.parse("add-run Bad|Name d/5 t/30", workouts));
        assertTrue(ex.getMessage().contains("|"));
    }

    // ── add-run: happy path ───────────────────────────────────────────────────

    @Test
    void addRun_validInput_addsWorkout() throws FitLoggerException {
        Command cmd = Parser.parse("add-run Morning Jog d/5.0 t/25.5", workouts);
        cmd.execute(storage, workouts, ui);

        assertEquals(1, workouts.getSize());
        assertTrue(workouts.getWorkoutAtIndex(0) instanceof RunWorkout);

        RunWorkout logged = (RunWorkout) workouts.getWorkoutAtIndex(0);
        assertEquals("Morning Jog", logged.getDescription());
        assertEquals(5.0, logged.getDistance(), 0.001);
        assertEquals(25.5, logged.getDurationMinutes(), 0.001);
    }

    // ── unknown command ───────────────────────────────────────────────────────

    @Test
    void parse_unknownCommand_throwsFitLoggerException() {
        assertThrows(FitLoggerException.class, () -> Parser.parse("foobar", workouts));
    }

    // ── delimiter validator (unit test for the helper directly) ──────────────

    @Test
    void validateDelimiters_pipe_throwsException() {
        assertThrows(FitLoggerException.class,
                () -> Parser.validateNoStorageDelimiters("has|pipe", "Field"));
    }

    @Test
    void validateDelimiters_slash_throwsException() {
        assertThrows(FitLoggerException.class,
                () -> Parser.validateNoStorageDelimiters("has/slash", "Field"));
    }

    @Test
    void validateDelimiters_cleanString_passes() throws FitLoggerException {
        // Should complete without throwing
        Parser.validateNoStorageDelimiters("Bench Press", "Exercise name");
    }

    private static class TestUi extends Ui {
        @Override
        public void showMessage(String m) {};

        @Override
        public void printWorkout(Workout w) {};
    }
}
